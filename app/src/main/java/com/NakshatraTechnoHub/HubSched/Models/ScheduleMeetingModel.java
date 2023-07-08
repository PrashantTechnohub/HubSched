package com.NakshatraTechnoHub.HubSched.Models;

import java.util.List;

public class ScheduleMeetingModel {
    private int _id;
    private int roomId;
    private int organiser_id;
    private int company_id;
    private List<Integer> employee_ids;
    private List<Integer> accepted;
    private List<Integer> declined;

    private String acceptance_status;
    private String startTime;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String endTime;
    private String status;
    private String subject;

//    public ScheduleMeetingModel(int organiser_id, int[] employee_ids, String startTime, String endTime, String subject) {
//        this.organiser_id = organiser_id;
//        this.employee_ids = employee_ids;
//        this.startTime = startTime;
//        this.endTime = endTime;
//        this.subject = subject;
//    }


    public List<Integer> getAccepted() {
        return accepted;
    }

    public void setAccepted(List<Integer> accepted) {
        this.accepted = accepted;
    }

    public List<Integer> getDeclined() {
        return declined;
    }

    public void setDeclined(List<Integer> declined) {
        this.declined = declined;
    }

    public String getAcceptance_status() {
        return acceptance_status;
    }

    public void setAcceptance_status(String acceptance_status) {
        this.acceptance_status = acceptance_status;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getOrganiser_id() {
        return organiser_id;
    }

    public void setOrganiser_id(int organiser_id) {
        this.organiser_id = organiser_id;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public List<Integer> getEmployee_ids() {
        return employee_ids;
    }

    public void setEmployee_ids(List<Integer> employee_ids) {
        this.employee_ids = employee_ids;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
