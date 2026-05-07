package com.uet.auction.server.model;

public class Seller extends User {
    public Seller(String username, String password) {
        super(username, password, UserRole.SELLER);
    }

    @Override
    public void printInfo() {
        System.out.println("Seller: " + this.username);
    }

    @Override
    public void performRoleAction() {
        System.out.println(this.username + " is adding products for auction.");
    }
}
