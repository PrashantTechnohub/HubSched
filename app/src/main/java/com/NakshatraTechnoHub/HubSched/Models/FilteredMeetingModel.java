package com.NakshatraTechnoHub.HubSched.Models;

public class FilteredMeetingModel {
    private String subject;
    private String location;
    private String time;

    public FilteredMeetingModel(String subject, String location, String time) {
        this.subject = subject;
        this.location = location;
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }
}

