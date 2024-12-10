package com.example.myvotingapp;

public class User {
    public String name;
    public String email;
    public String password;
    public String voterId;
    public String gender;
    public String votingStatus;
    public String imageUri;

    // Default constructor (required for Firebase)
    public User() {
    }

    // Parameterized constructor
    public User(String name, String email, String password, String voterId, String gender, String votingStatus, String imageUri) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.voterId = voterId;
        this.gender = gender;
        this.votingStatus = votingStatus;
        this.imageUri = imageUri;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getVoterId() { return voterId; }
    public void setVoterId(String voterId) { this.voterId = voterId; }

    public String getVotingStatus() { return votingStatus; }
    public void setVotingStatus(String votingStatus) { this.votingStatus = votingStatus; }
}
