package com.example.reme;

import java.io.Serializable;

public class LoginDetail implements Serializable {
    public String email;
    public String name;
    public String uid;
    public String phoneNumber;

    public LoginDetail() {

    }

    public LoginDetail(String email, String name, String uid, String phoneNumber){
        this.email = email;
        this.name = name;
        this.uid = uid;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}