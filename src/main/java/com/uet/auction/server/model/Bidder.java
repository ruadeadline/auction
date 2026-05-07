package com.uet.auction.server.model;

public class Bidder extends User {
    public Bidder(String username, String password) {
        super(username, password, UserRole.BIDDER);
    }

    @Override
    public void printInfo() {
        System.out.println("Bidder: " + this.username + " | Balance: $" + this.balance);
    }

    @Override
    public void performRoleAction() {
        System.out.println(this.username + " is participating in auctions.");
    }
}
