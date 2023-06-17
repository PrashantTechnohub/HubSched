package com.NakshatraTechnoHub.HubSched.Models;

import org.json.JSONArray;


public class PantryModel {
    String RoomAddress, OrderedBy, status;

    int meetId, _id;

    JSONArray jsonArray;



    public PantryModel(String roomAddress, String orderedBy, String status, int meetId, int _id, JSONArray jsonArray) {
        RoomAddress = roomAddress;
        OrderedBy = orderedBy;
        this.status = status;
        this.meetId = meetId;
        this._id = _id;
        this.jsonArray = jsonArray;
    }

    public int get_id() {
        return _id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getMeetId() {
        return meetId;
    }

    public void setMeetId(int meetId) {
        this.meetId = meetId;
    }

    public String getRoomAddress() {
        return RoomAddress;
    }

    public void setRoomAddress(String RoomAddress) {
        this.RoomAddress = RoomAddress;
    }

    public String getOrderedBy() {
        return OrderedBy;
    }

    public void setOrderedBy(String OrderedBy) {
        this.OrderedBy = OrderedBy;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }
}