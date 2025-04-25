package com.rate_api.Authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the authentication response for a user.
 * Contains status and message information.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAuth {

    /**
     * The status of the authentication process.
     * Example values might be "success", "failure", etc.
     */
    private String status;

    /**
     * A message providing additional information about the authentication result.
     */
    private String message;
}
