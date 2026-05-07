package com.uet.auction.server.model;

import java.util.ArrayList;
import java.util.List;

public abstract class User extends Entity {
    protected String username;
    protected String password;
    protected UserRole role;
    protected double balance;
    protected List<String> notifications;

    public User(String username, String password, UserRole role) {
        super();
        this.username = username;
        this.password = password;
        this.role = role;
        this.balance = 0.0;
        this.notifications = new ArrayList<>();
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public void addNotification(String message) {
        this.notifications.add(message);
    }
    
    public List<String> getNotifications() {
        return notifications;
    }

    // Abstract method to demonstrate polymorphism
    public abstract void performRoleAction();
}
