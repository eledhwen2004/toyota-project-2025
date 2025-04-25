package com.main.User;

/**
 * Represents a user with a username and password.
 * Used for authentication and authorization purposes.
 */
public class User {

    private String username;
    private String password;

    /**
     * Constructs a new user with the given username and password.
     *
     * @param username the username of the user.
     * @param password the password of the user.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the username of the user.
     *
     * @return the username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for the user.
     *
     * @param username the new username to set for the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of the user.
     *
     * @return the password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for the user.
     *
     * @param password the new password to set for the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
