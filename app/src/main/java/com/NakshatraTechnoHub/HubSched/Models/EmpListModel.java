package com.NakshatraTechnoHub.HubSched.Models;

public class EmpListModel {

    private String emp_id;

    private String emp_name;

    private String emp_post;

    public EmpListModel() {
    }

    public EmpListModel(String emp_id, String emp_name, String emp_post) {
        this.emp_id = emp_id;
        this.emp_name = emp_name;
        this.emp_post = emp_post;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getEmp_post() {
        return emp_post;
    }

    public void setEmp_post(String emp_post) {
        this.emp_post = emp_post;
    }
}
