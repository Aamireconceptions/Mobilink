package com.ooredoo.bizstore.model;

/**
 * @author Pehlaj Rai
 * @since 11/11/2014.
 */
public class User {

    public String username;
    public String password;

    public String verificationCode;

    public User() {
    }

    public User(String username, String password, String verificationCode) {
        this.username = username;
        this.password = password;
        this.verificationCode = verificationCode;
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
