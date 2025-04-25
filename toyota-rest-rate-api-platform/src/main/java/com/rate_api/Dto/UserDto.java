package com.rate_api.Dto;

/**
 * Data Transfer Object for user authentication credentials.
 */
public class UserDto {

    private String username;
    private String password;
    public UserDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }
}
