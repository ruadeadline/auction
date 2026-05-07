package com.uet.auction.server.model;

import java.math.BigDecimal;

import static com.uet.auction.server.model.ItemCategory.ART;

public class Art extends com.uet.auction.server.model.Item {
    private String artist;
    private int yearCreated;
    private String medium;

    public Art() { super(); }

    public Art(String name, String description, BigDecimal startingPrice, String sellerId,
               String artist, int yearCreated, String medium) {
        super(name, description, startingPrice, sellerId, ART);
        this.artist = artist;
        this.yearCreated = yearCreated;
        this.medium = medium;
    }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public int getYearCreated() { return yearCreated; }
    public void setYearCreated(int yearCreated) { this.yearCreated = yearCreated; }

    public String getMedium() { return medium; }
    public void setMedium(String medium) { this.medium = medium; }

    @Override
    public void printInfo() {
        System.out.println("[ART] " + name + " | Tác giả: " + artist
                + " | Năm: " + yearCreated + " | Giá KĐ: " + startingPrice);
    }
}