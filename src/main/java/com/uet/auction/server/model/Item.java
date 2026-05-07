package com.uet.auction.server.model;

import java.math.BigDecimal;

public abstract class Item extends Entity {
    protected String name;
    protected String description;
    protected BigDecimal startingPrice;
    protected String sellerId;
    protected com.uet.auction.server.model.ItemCategory category;

    public Item() { super(); }

    public Item(String name, String description, BigDecimal startingPrice,
                String sellerId, com.uet.auction.server.model.ItemCategory category) {
        super();
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
        this.sellerId = sellerId;
        this.category = category;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getStartingPrice() { return startingPrice; }
    public void setStartingPrice(BigDecimal startingPrice) { this.startingPrice = startingPrice; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public com.uet.auction.server.model.ItemCategory getCategory() { return category; }
    public void setCategory(com.uet.auction.server.model.ItemCategory category) { this.category = category; }
}