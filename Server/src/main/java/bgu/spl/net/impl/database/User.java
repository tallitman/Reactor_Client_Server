package bgu.spl.net.impl.database;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class User {
    private String fUserName;
    private String fPassword;
    private List<String> fFollowers;
    private List<String> fFollowing;
    private LinkedBlockingQueue<Messages> fPendingMessages;
    private int fNumofPosts;
    private final LocalDateTime fDate;

    public User(String userName, String password) {
        this.fNumofPosts = 0;
        this.fDate = LocalDateTime.now();
        this.fPendingMessages = new LinkedBlockingQueue();
        this.fFollowers = Collections.synchronizedList(new ArrayList());
        this.fFollowing = Collections.synchronizedList(new ArrayList());
        this.fUserName = userName;
        this.fPassword = password;

    }

    public String getfPassword() {
        return fPassword;
    }

    public String getfUserName() {
        return fUserName;
    }


    public LocalDateTime getDate() {
        return fDate;
    }

    public boolean followMe(User user) {
        boolean ans = false;
        if (!fFollowers.contains(user.getfUserName())) {
            fFollowers.add(user.getfUserName());
            return true;
        }
        return ans;
    }

    public boolean unFollowMe(User user) {
        if (user == null) return false;
        return fFollowers.remove(user.getfUserName());
    }

    public boolean follow(User user) {
        if (user != null && !fFollowing.contains(user.getfUserName())) {
            fFollowing.add(user.getfUserName());
            return true;
        }
        return false;

    }

    public boolean unFollow(User user) {
        if (user == null) return false;
        return fFollowing.remove(user.getfUserName());
    }

    public List<String> getfFollowers() {
        return fFollowers;
    }

    public void addAwaitsMessage(Messages msg) {
        try {
            fPendingMessages.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void postAdded() {
        this.fNumofPosts++;
    }

    public int getNumofPosts() {
        return fNumofPosts;
    }

    public List<String> getUserStatus() {
        List<String> ans = new ArrayList<>();
        ans.add(Integer.toString(8));
        ans.add(Integer.toString(fNumofPosts));
        ans.add(Integer.toString(fFollowers.size()));
        ans.add(Integer.toString(fFollowing.size()));
        return ans;
    }

    public Messages getPendingMessage() {
        return fPendingMessages.poll();
    }
}

