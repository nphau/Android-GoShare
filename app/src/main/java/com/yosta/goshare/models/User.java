package com.yosta.goshare.models;

import java.util.ArrayList;

/**
 * Created by dinhhieu on 3/26/16.
 */
public class User {
    protected String username;
    protected String password;
    protected String email;
    private ArrayList<Resource> listResource;
    private ArrayList<User> friendList;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.listResource = new ArrayList<>();
        this.friendList = new ArrayList<>();
    }

    public void addResource(Resource resource) {
        listResource.add(resource);
    }

    public void addFriend(User friend) {
        this.friendList.add(friend);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
