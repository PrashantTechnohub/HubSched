package com.NakshatraTechnoHub.HubSched.Models;

public class RoomListModel {
    private Integer room_id;
    private Integer _id;

    private Integer room_no;

    private String floor_no;

    private String room_name;

    private Integer seat_cap;

    private String [] facilities;

    public RoomListModel(Integer room_id, Integer room_no, String floor_no, String room_name, Integer seat_cap, String[] facilities) {
        this.room_id = room_id;
        this.room_no = room_no;
        this.floor_no = floor_no;
        this.room_name = room_name;
        this.seat_cap = seat_cap;
        this.facilities = facilities;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public Integer getRoom_id() {
        return room_id;
    }

    public void setRoom_id(Integer room_id) {
        this.room_id = room_id;
    }

    public Integer getRoom_no() {
        return room_no;
    }

    public void setRoom_no(Integer room_no) {
        this.room_no = room_no;
    }

    public String getFloor_no() {
        return floor_no;
    }

    public void setFloor_no(String floor_no) {
        this.floor_no = floor_no;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public Integer getSeat_cap() {
        return seat_cap;
    }

    public void setSeat_cap(Integer seat_cap) {
        this.seat_cap = seat_cap;
    }

    public String[] getFacilities() {
        return facilities;
    }

    public void setFacilities(String[] facilities) {
        this.facilities = facilities;
    }
}
