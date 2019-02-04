package bgu.spl.net.impl.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataBase {


    private ConcurrentHashMap<String, User> fUsers;
    private ConcurrentHashMap<String, Integer> fLoggedInUsers;
    private ConcurrentHashMap<String, LinkedBlockingQueue<? extends Messages>> fAwaitingMessages;
    private List<Messages> fMessages;
    private ReentrantReadWriteLock fLockUsers;

    public DataBase() {
        fLockUsers = new ReentrantReadWriteLock(true);
        fAwaitingMessages = new ConcurrentHashMap<>();
        fMessages = Collections.synchronizedList(new ArrayList<>());
        fUsers = new ConcurrentHashMap<>();
        fLoggedInUsers = new ConcurrentHashMap<>();
    }

    public User register(String username, String password) {
        fLockUsers.writeLock().lock();
        try {
            User usr = new User(username, password);
            if (!fUsers.containsKey(usr.getfUserName())) {
                fUsers.put(usr.getfUserName(), usr);
                fAwaitingMessages.put(usr.getfUserName(), new LinkedBlockingQueue<>());
                return usr;
            } else {
                return null;
            }
        } finally {
            fLockUsers.writeLock().unlock();
        }

    }

    public User getUserByName(String userName) {

        return fUsers.get(userName);
    }

    public boolean login(String username, int conID) {
        if (fLoggedInUsers.containsKey(username)) return false;
        fLoggedInUsers.put(username, conID);
        return true;
    }


    public boolean logout(User user) {
        Integer conID = fLoggedInUsers.remove(user.getfUserName());
        return conID != null;
    }

    public int getUsedConId(String username) {
        Integer ans = fLoggedInUsers.get(username);
        if (ans == null) {
            return -1;
        }
        return ans;
    }

    public void addMessage(Messages msg) {
        fMessages.add(msg);
    }

    public List<String> getfUsers() {
        List<User> users = new ArrayList<>(fUsers.values());

        users.sort((User u1, User u2) -> u1.getDate().compareTo(u2.getDate()));
        List<String> ans = new ArrayList<>();
        for (User u : users) {
            ans.add(u.getfUserName());
        }
        return ans;
    }

    public User authenticate(String userName, String passWord) {
        User result = fUsers.get(userName);
        if (result != null && result.getfPassword().equals(passWord)) {
            return result;
        }
        return null;
    }
}
