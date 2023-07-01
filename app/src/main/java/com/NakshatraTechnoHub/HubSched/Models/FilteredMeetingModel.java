package com.NakshatraTechnoHub.HubSched.Models;

public class FilteredMeetingModel {
    private String subject;
    private String date;
    private String time;

    public FilteredMeetingModel(String subject, String date, String time) {
        this.subject = subject;
        this.date = date;
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

