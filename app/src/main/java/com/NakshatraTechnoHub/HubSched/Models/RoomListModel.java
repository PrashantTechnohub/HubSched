package com.NakshatraTechnoHub.HubSched.Models;

public class RoomListModel {
    private Integer _id;

    private Integer room_no;

    private String floor_no;

    private String room_name;

    private Integer seat_cap;

    private String [] facilities;

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
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
