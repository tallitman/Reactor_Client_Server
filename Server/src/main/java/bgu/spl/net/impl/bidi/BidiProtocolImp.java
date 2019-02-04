package bgu.spl.net.impl.bidi;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.bidi.operations.*;
import bgu.spl.net.impl.database.*;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * The {@link BidiProtocolImp} is an implementation of the {@link BidiMessagingProtocol} inteface
 * in our implement we using the Operation Class wich represents a decoded message which we recieved from the server
 * or we can create new Opeartion filled with data need to be send to the client and than encode them
 * 
 *
 * **/
public class BidiProtocolImp implements BidiMessagingProtocol<Operation> {
    private boolean fShouldTerminate;
    private Connections<Operation> fConncections;
    private DataBase fDataBase;
    private int fConnectionID;
    private User fLoggedInUser;


    public BidiProtocolImp(DataBase fDataBase) {
        this.fDataBase = fDataBase;

    }

    @Override
    public void start(int connectionId, Connections<Operation> connections) {
        fConnectionID = connectionId;
        fConncections = connections;
    }

    /**
     * @param operation represnts a decoded message recieved from the client.
     *                  each operation opcode will lead to the the proper way our server need to response to the client
     */
    @Override
    public void process(Operation operation) {
        short opt = operation.getfOPcode();
        if ((opt != 1 && opt != 2) && fLoggedInUser == null) {
            send(sendError(operation.getfOPcode()));
            return;
        }
        switch (opt) {
            case Operation.REGISTER:
                register(operation);
                break;
            case Operation.LOGIN:
                login(operation);
                break;
            case Operation.LOGOUT:
                logout(operation);
                break;
            case Operation.FOLLOW:
                follow(operation);
                break;
            case Operation.POST:
                post(operation);
                break;
            case Operation.PM:
                pmRequest(operation);
                break;
            case Operation.USERLIST:
                userListRequest(operation);
                break;
            case Operation.STAT:
                statsRequest(operation);
                break;

        }

    }

    @Override

    public boolean shouldTerminate() {
        return fShouldTerminate;
    }
    //register a new user to out system
    //if a user is exists an error message will be sent
    private void register(Operation operation) {
        if (fLoggedInUser != null) {
            send(sendError(operation.getfOPcode()));
            return;
        }
        String userName = operation.getfArguments().get(0);
        String passWord = operation.getfArguments().get(1);
        User newUser = fDataBase.register(userName, passWord);
        if (newUser != null) {
            send(sendAck(operation));
        } else {
            send(sendError(operation.getfOPcode()));
        }


    }

    private void login(Operation operation) {
        if (fLoggedInUser != null) {
            send(sendError(operation.getfOPcode()));
            return;
        }
        //loading user data from the operation argument
        String userName = operation.getfArguments().get(0);
        String passWord = operation.getfArguments().get(1);
        User user = fDataBase.authenticate(userName, passWord);
        if (user != null) {
            synchronized (user) {

                if (fDataBase.login(userName, fConnectionID)) {
                    fLoggedInUser = user;
                    send(sendAck(operation));
                    loadPendingMessages();
                } else {
                    send(sendError(operation.getfOPcode()));
                }
            }
        } else {
            send(sendError(operation.getfOPcode()));
        }
    }

    private void logout(Operation operation) {
        if (fDataBase.logout(fLoggedInUser)) {
            send(sendAck(operation));
            fConncections.disconnect(fConnectionID);
            fShouldTerminate = true;
        } else {
            send(sendError(operation.getfOPcode()));
        }

    }
    //is used to follow other users using our system
    private void follow(Operation operation) {
        List<String> followedUsers = new ArrayList<>();
        List<String> arguments = operation.getfArguments();
        int followSuccess = 0; //represents the number of users we were able to follow if in the end of the process we get 0 success followers then error will be send
        int follow = Integer.parseInt(arguments.get(0));
        switch (follow) {
            case 0: //follow
                for (int i = 2; i < Integer.parseInt(arguments.get(1)) + 2; i++) {
                    User u = fDataBase.getUserByName(arguments.get(i));
                    boolean toFollow = fLoggedInUser.follow(u);
                    if (u != null && toFollow && u.followMe(fLoggedInUser)) {
                        followSuccess++;
                        followedUsers.add(u.getfUserName());
                    }
                }

                break;
            case 1://unfollow
                for (int i = 2; i < Integer.parseInt(arguments.get(1)) + 2; i++) {
                    User u = fDataBase.getUserByName(arguments.get(i));
                    boolean canUnFollow = fLoggedInUser.unFollow(u);
                    if (u != null && canUnFollow && u.unFollowMe(fLoggedInUser)) {
                        followSuccess++;
                        followedUsers.add(u.getfUserName());
                    }
                }
                break;
        }
        //if couldnt follow any requested user an error will be send
        if (followSuccess == 0) {
            send(sendError(operation.getfOPcode()));
        } else {
            List<String> ackArguments = new ArrayList<>();
            ackArguments.add(Short.toString(operation.getfOPcode()));
            ackArguments.add(Integer.toString(followSuccess));
            ackArguments.addAll(followedUsers);
            send(sendAck(ackArguments));

        }


    }
    /**
     * using when a OPPost has recieved with the post data
     * we can find the post tagged user an notify them
     *
     * **/

    private void post(Operation operation) {

        String post = operation.getfArguments().get(0);
        List<String> temp = ((OPPost) operation).getTaggedUsers();
        List<String> targets = new ArrayList<>();
        for (String s : temp) {
            if (fDataBase.getUserByName(s) != null)
                targets.add(s);
        }
        //creating the ack info for the tagged users
        List<String> arguments = new ArrayList<>();
        arguments.add(Short.toString(operation.getfOPcode()));//post/pm
        arguments.add(fLoggedInUser.getfUserName());//sender
        arguments.add(post);//content
        for (String s : fLoggedInUser.getfFollowers()) {
            if (!targets.contains(s)) {
                targets.add(s);
            }
        }
        fLoggedInUser.postAdded();
        Messages postMsg = new Post(fLoggedInUser.getfUserName(), post);
        fDataBase.addMessage(postMsg);
        send(sendAck(operation));
        sendNotification(arguments, targets, postMsg);


    }

    private void pmRequest(Operation operation) {

        //getting the pm content
        String content = operation.getfArguments().get(1);
        String target = operation.getfArguments().get(0);
        //creating the values needed to make a notification for the PM (@arguments)
        List<String> targets = new ArrayList<>();
        targets.add(target);//destination of the pm
        User targetUser = fDataBase.getUserByName(target);
        if (targetUser == null) {
            send(sendError(operation.getfOPcode()));
            return;
        }
        List<String> arguments = new ArrayList<>();
        arguments.add(Short.toString(operation.getfOPcode()));//post/pm
        arguments.add(fLoggedInUser.getfUserName());//sender
        arguments.add(content);//content
        //collecting data to DataBase
        Messages pMMsg = new PM(fLoggedInUser.getfUserName(), content);
        fDataBase.addMessage(pMMsg);
        //sending the PM notification+acks
        send(sendAck(operation));
        sendNotification(arguments, targets, pMMsg);


    }

    private void userListRequest(Operation operation) {
        List<String> arguments = new ArrayList<>();
        arguments.add(Short.toString(operation.getfOPcode()));
        List<String> registeredUsers = fDataBase.getfUsers();
        arguments.add(Integer.toString(registeredUsers.size()));
        arguments.addAll(registeredUsers);
        send(sendAck(arguments));
    }

    private void statsRequest(Operation operation) {
        String userStat = operation.getfArguments().get(0);
        User user = fDataBase.getUserByName(userStat);
        if (user != null) {
            List<String> ackArguments = user.getUserStatus();
            send(sendAck(ackArguments));
            return;
        }
        send(sendError(operation.getfOPcode()));

    }

    private void sendNotification(List<String> arguments, List<String> targets, Messages msg) {
        Operation notification = new OPNotification(arguments);
        for (String s : targets) {
            User target = fDataBase.getUserByName(s);

            synchronized (target) {
                int conID = fDataBase.getUsedConId(s);
                if (conID != -1) {
                    fConncections.send(conID, notification);
                } else { //pending message
                    target.addAwaitsMessage(msg);
                }
            }
        }


    }

    private Operation sendAck(List<String> arguments) {
        return new OPAck(arguments);

    }
    //creating an ack operation from given argumets
    private Operation sendAck(Operation op) {
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(Short.toString(op.getfOPcode()));
        return sendAck(arguments);

    }
    //sending en error

    private Operation sendError(short senderOP) {
        return new OPError(senderOP);
    }

    private void send(Operation operation) {
        if (fLoggedInUser == null) {
            fConncections.send(fConnectionID, operation);
        } else {
            synchronized (fLoggedInUser) {
                fConncections.send(fConnectionID, operation);
            }
        }

    }
    //when a client is logging into our system
    //we first verify if there is any pending messages from others users while he was offline


    private void loadPendingMessages() {
        Messages pendingMessage = fLoggedInUser.getPendingMessage();
        while (pendingMessage != null) {
            List<String> target = new ArrayList<>();
            target.add(fLoggedInUser.getfUserName());
            List<String> arguments = new ArrayList<>();
            if (pendingMessage instanceof Post) {
                arguments.add("5");
            } else {
                arguments.add("6");
            }
            arguments.add(pendingMessage.getfSender());
            arguments.add(pendingMessage.getfContent());

            sendNotification(arguments, target, pendingMessage);
            pendingMessage = fLoggedInUser.getPendingMessage();
        }
    }
}

