package com.example.fypapp;

public final class Signer {

    public String username;
    public String password;

    public final static Signer INSTANCE = new Signer();

    private Signer(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
