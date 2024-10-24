package com.example.myvotingapp;

public class User {
    public String name;
    public String phone;
    public String nid;
    public String email;
    public String gender;
    public String imageUri;
    public String nidFileUri;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String phone, String nid, String email, String gender, String imageUri, String nidFileUri) {
        this.name = name;
        this.phone = phone;
        this.nid = nid;
        this.email = email;
        this.gender = gender;
        this.imageUri = imageUri;
        this.nidFileUri = nidFileUri;
    }
}
