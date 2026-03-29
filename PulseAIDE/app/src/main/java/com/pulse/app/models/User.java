package com.pulse.app.models;

public class User {
    public int id;
    public String username;
    public String password;
    public String displayName;
    public String bio;
    public boolean isOnline;

    public String getInitial() {
        if (displayName != null && !displayName.isEmpty()) return String.valueOf(displayName.charAt(0)).toUpperCase();
        if (username != null && !username.isEmpty()) return String.valueOf(username.charAt(0)).toUpperCase();
        return "?";
    }
}
