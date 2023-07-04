package com.NakshatraTechnoHub.HubSched.Models;

public class ContactModel {
    private String id;
    private String name;
    private String photoUri;

    public ContactModel(String id, String name, String photoUri) {
        this.id = id;
        this.name = name;
        this.photoUri = photoUri;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUri() {
        return photoUri;
    }
}
