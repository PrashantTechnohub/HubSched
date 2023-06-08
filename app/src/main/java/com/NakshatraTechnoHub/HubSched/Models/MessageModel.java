package com.NakshatraTechnoHub.HubSched.Models;

public class MessageModel {

    int id ;
    private String message;
    private String senderName;

    public MessageModel(int id, String message, String senderName) {
        this.id = id;
        this.message = message;
        this.senderName = senderName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
