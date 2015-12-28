package com.saarang.samples.apps.iosched.util;

import java.io.Serializable;

/**
 * Created by Moochi on 12/15/2015.
 */
public class ContactObject implements Serializable {
    String number;
    int profilePic;
    String email;
    String name;
    String publicProfile;
    public ContactObject(String name, String email, String number, String publicProfile, int profilePic){
        this.name = name;
        this.email = email;
        this.number=number;
        this.publicProfile=publicProfile;
        this.profilePic=profilePic;
    }

    public String getNumber(){return this.number;}
    public  String getName(){return this.name;}
    public String getMail(){return this.email;}
    public String getPublicProfile(){return this.publicProfile;}
    public int getProfilePic(){return this.profilePic;}

}

