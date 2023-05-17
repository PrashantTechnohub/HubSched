package com.NakshatraTechnoHub.HubSched.Models;

public class MeetingEmpListModel {
    int _id;
    String email, name;

    public MeetingEmpListModel( String email, String name) {
        this.email = email;
        this.name = name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
