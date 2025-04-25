package com.main.Authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a login request containing the user's credentials.
 * This class is used for capturing the login credentials (username and password)
 * during the authentication process.
 *
 * <p>It uses Lombok annotations to automatically generate the constructor, getters, setters,
 * and other methods such as equals, hashCode, and toString.</p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequest {

    /**
     * The username provided by the user for authentication.
     *
     * @return the username as a {@link String}
     */
    private String username;

    /**
     * The password provided by the user for authentication.
     *
     * @return the password as a {@link String}
     */
    private String password;
}
