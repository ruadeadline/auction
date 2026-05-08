package com.uet.auction.server.model;

import java.math.BigDecimal;

public class ItemFactory {
    public static Item createItem(ItemCategory category, String name, String description, BigDecimal startingPrice, String sellerId) {
        switch (category) {
            case ELECTRONICS:
                return new Electronics(name, description, startingPrice, sellerId, "Unknown Brand", "Unknown Model", 0);
            case ART:
                return new Art(name, description, startingPrice, sellerId, "Unknown Artist", 2023, "Mixed Media");
            case VEHICLE:
                return new Vehicle(name, description, startingPrice, sellerId, "Unknown Make", "Unknown Model", 2023, 0);
            case GENERAL:
            default:
                // If we don't have a specific GeneralItem class, we can return a basic item or throw an exception.
                // Since Item is abstract, let's just default to Electronics for now if GENERAL is selected.
                return new Electronics(name, description, startingPrice, sellerId, "Generic Brand", "Generic Model", 0);
        }
    }
}
