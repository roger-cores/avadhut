package com.frostox.calculoII.nodes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by shannonpereira601 on 23/04/16.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class User {
    private String fullName;
    private String email;
    private String uid;
    private boolean blocked;
    private long time;
    private boolean activated;

    public User() {
    }

    public User(String email, String uid, String fullName, boolean blocked, boolean activated, long time) {
        this.fullName = fullName;
        this.activated = activated;
        this.time = time;
        this.uid = uid;
        this.email = email;
        this.blocked = blocked;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public boolean getBlocked() {
        return false;
    }

    public boolean getActivated() {
        return activated;
    }

    public long getTime() {
        return time;
    }
}