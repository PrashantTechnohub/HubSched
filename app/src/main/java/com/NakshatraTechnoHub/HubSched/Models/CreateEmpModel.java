package com.NakshatraTechnoHub.HubSched.Models;

public class CreateEmpModel {
    String _id ,empId, name, email, mobile, gender, position,password;

    public CreateEmpModel() {
    }

    public CreateEmpModel(String empId, String name, String email, String mobile, String gender, String position, String password) {
        this.empId = empId;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.gender = gender;
        this.position = position;
        this.password = password;
    }

    public String get_id() {
        return _id;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
