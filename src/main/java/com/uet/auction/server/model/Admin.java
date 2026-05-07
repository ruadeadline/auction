package com.uet.auction.server.model;

public class Admin extends User {
    public Admin(String username, String password) {
        super(username, password, UserRole.ADMIN);
    }

    @Override
    public void printInfo() {
        System.out.println("Admin: " + this.username);
    }

    @Override
    public void performRoleAction() {
        System.out.println(this.username + " is managing the auction system.");
    }
}
