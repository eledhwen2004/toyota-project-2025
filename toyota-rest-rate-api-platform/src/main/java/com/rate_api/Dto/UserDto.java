package com.rate_api.Dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing user credentials.
 * <p>
 * This class is typically used for authentication or login-related operations.
 */
@Getter
@Setter
public class UserDto {

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The password associated with the user account.
     */
    private String password;

    /**
     * Constructs a new {@code UserDto} instance with the specified username and password.
     *
     * @param username the user's username
     * @param password the user's password
     */
    public UserDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
