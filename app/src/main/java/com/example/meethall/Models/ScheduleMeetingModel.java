package com.example.meethall.Models;

public class ScheduleMeetingModel {
    String meetId, orgName, meetingSubject, meetingTime, meetingLocation, meetingStatus;


    public ScheduleMeetingModel(String meetId, String orgName, String meetingSubject, String meetingTime, String meetingLocation, String meetingStatus) {
        this.meetId = meetId;
        this.orgName = orgName;
        this.meetingSubject = meetingSubject;
        this.meetingTime = meetingTime;
        this.meetingLocation = meetingLocation;
        this.meetingStatus = meetingStatus;
    }

    public String getMeetId() {
        return meetId;
    }

    public void setMeetId(String meetId) {
        this.meetId = meetId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getMeetingSubject() {
        return meetingSubject;
    }

    public void setMeetingSubject(String meetingSubject) {
        this.meetingSubject = meetingSubject;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getMeetingLocation() {
        return meetingLocation;
    }

    public void setMeetingLocation(String meetingLocation) {
        this.meetingLocation = meetingLocation;
    }

    public String getMeetingStatus() {
        return meetingStatus;
    }

    public void setMeetingStatus(String meetingStatus) {
        this.meetingStatus = meetingStatus;
    }
}
