package com.example.pethotel;

public interface IUser {

    void setUsername(String username);
    String getUsername(String username);

    void setUserType(UserType type);
    UserType getUserType(UserType type);

    void setEmail(String email);
    String getEmail(String email);

}
