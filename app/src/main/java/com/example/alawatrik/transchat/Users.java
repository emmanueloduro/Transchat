package com.example.alawatrik.transchat;

/**
 * Created by ALAWATRIK on 2/17/2018.
 */

public class Users {
    String name;
    String status;
    String languages;


    public Users(){

    }

    //constructor of the class users
    public Users(String username, String userstatus, String language) {
        this.name = username;
        this.status = userstatus;
        this.languages = language;
    }


    //getters of the class users


    public String getStatus() {
        return status;
    }

    public String getUsername() {

        return name;
    }

    public String getLanguage() {

        return languages;
    }

    //setters of the class users




}
