package com.NakshatraTechnoHub.HubSched.Models;

public class RoomListModel {
    private Integer room_id;

    private String floor_no;

    private String room_name;

    private String seat_cap;

    private String facilities;


    public RoomListModel(Integer room_id, String floor_no, String room_name, String seat_cap, String facilities) {
        this.room_id = room_id;
        this.floor_no = floor_no;
        this.room_name = room_name;
        this.seat_cap = seat_cap;
        this.facilities = facilities;
    }

    public Integer getRoom_id() {
        return room_id;
    }

    public void setRoom_id(Integer room_id) {
        this.room_id = room_id;
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

    public String getSeat_cap() {
        return seat_cap;
    }

    public void setSeat_cap(String seat_cap) {
        this.seat_cap = seat_cap;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }
}
