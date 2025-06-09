package com.main.Authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object (DTO) used to represent the result of a user authentication process.
 * <p>
 * Typically returned by authentication endpoints to indicate whether login was successful
 * and to provide additional information or error messages.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAuth {

    /**
     * Authentication status (e.g., "success", "failure").
     */
    private String status;

    /**
     * Descriptive message associated with the authentication result.
     * For example, error reason or success confirmation.
     */
    private String message;
}
