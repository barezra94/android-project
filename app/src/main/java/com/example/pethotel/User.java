package com.example.pethotel;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User implements IUser {

    private String username;
    private String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }


    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUsername(String username) {
        return this.username;
    }
}
