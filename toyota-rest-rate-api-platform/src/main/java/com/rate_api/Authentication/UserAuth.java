package com.rate_api.Authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the response of an authentication attempt.
 * <p>
 * This class holds the result status and a related message after a login request.
 * Typically used in API responses to indicate whether authentication was successful.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAuth {

    /**
     * The authentication result status.
     * <p>
     * Common values might be "success" or "failure".
     */
    private String status;

    /**
     * A human-readable message explaining the authentication result.
     * <p>
     * For example: "Login successful" or "Invalid credentials".
     */
    private String message;
}
