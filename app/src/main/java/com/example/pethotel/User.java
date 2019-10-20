package com.example.pethotel;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User implements IUser {

    private String username;
    private String email;
    private UserType type;



    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, UserType type ) {
        this.username = username;
        this.email = email;
        this.type = type;
    }


    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUsername(String username) {
        return this.username;
    }

    @Override
    public void setUserType(UserType type) { this.type = type; }

    @Override
    public UserType getUserType(UserType type) {
        return this.type;
    }

    @Override
    public void setEmail(String email) { this.email = email; }

    @Override
    public String getEmail(String email) {
        return this.email;
    }


}