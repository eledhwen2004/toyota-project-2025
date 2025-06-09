package com.rate_api.Service;

import com.rate_api.Authentication.UserAuth;
import com.rate_api.Dto.UserDto;
import org.springframework.stereotype.Component;

/**
 * Service interface for user authentication operations.
 * <p>
 * This interface defines the contract for validating user credentials
 * and performing login functionality.
 * </p>
 */
@Component
public interface UserService {

    /**
     * Validates the provided user credentials and returns the authentication result.
     *
     * @param dto a {@link UserDto} object containing the username and password
     * @return a {@link UserAuth} object indicating the login result (success or failure)
     */
    UserAuth login(UserDto dto);
}
