package com.NakshatraTechnoHub.HubSched.Models;

public class QueryModel {

    int userId, _id;
    String description, status;

    public QueryModel(int userId, int _id, String description, String status) {
        this.userId = userId;
        this._id = _id;
        this.description = description;
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
