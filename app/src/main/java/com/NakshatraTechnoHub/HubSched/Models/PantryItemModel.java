package com.NakshatraTechnoHub.HubSched.Models;

public class PantryItemModel {
    private String itemName;
    private Boolean availability;
    private int quantity;
    private String description;
    private boolean isSelected;

    public PantryItemModel(String itemName) {
        this.itemName = itemName;
        this.quantity = 0;
        this.isSelected = false;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
