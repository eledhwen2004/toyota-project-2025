package com.main.Authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object (DTO) representing a login request.
 * <p>
 * This class is typically used to encapsulate the username and password
 * provided by the client when attempting to authenticate.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequest {

    /**
     * Username provided by the user during login.
     */
    private String username;

    /**
     * Password provided by the user during login.
     */
    private String password;
}
