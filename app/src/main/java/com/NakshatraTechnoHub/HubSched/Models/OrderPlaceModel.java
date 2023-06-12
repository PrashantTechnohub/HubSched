package com.NakshatraTechnoHub.HubSched.Models;

public class OrderPlaceModel {
    private String meetId;
    private String itemName;
    private int quantity;
    private String description;

    public OrderPlaceModel(String meetId, String itemName, int quantity, String description) {
        this.meetId = meetId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.description = description;
    }

    public String getMeetId() {
        return meetId;
    }

    public void setMeetId(String meetId) {
        this.meetId = meetId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
