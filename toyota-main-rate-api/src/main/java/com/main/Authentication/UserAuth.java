package com.main.Authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the authentication response containing the status and message
 * related to the user authentication process.
 * This class is typically used for sending feedback about the authentication result.
 *
 * <p>It uses Lombok annotations to automatically generate the constructor, getters, setters,
 * and other methods such as equals, hashCode, and toString.</p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAuth {

    /**
     * The status of the authentication process.
     * This could indicate whether the authentication was successful or failed.
     *
     * @return the status as a {@link String}
     */
    private String status;

    /**
     * The message providing additional information about the authentication process.
     * This could include error messages or success messages.
     *
     * @return the message as a {@link String}
     */
    private String message;
}
