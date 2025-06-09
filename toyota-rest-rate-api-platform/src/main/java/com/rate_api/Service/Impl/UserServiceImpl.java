package com.rate_api.Service.Impl;

import com.rate_api.Authentication.UserAuth;
import com.rate_api.Dto.UserDto;
import com.rate_api.Service.UserService;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link UserService} interface.
 * <p>
 * This service handles user authentication by checking
 * the provided credentials against a predefined user.
 * </p>
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * A temporary user used for basic in-memory authentication.
     */
    private UserDto tempUser;

    /**
     * Initializes the service with a static user (username: 1234, password: 1234).
     */
    public UserServiceImpl() {
        tempUser = new UserDto("1234", "1234");
    }

    /**
     * Performs login authentication for the given user credentials.
     *
     * @param dto the user credentials
     * @return {@link UserAuth} object indicating login success or failure
     */
    @Override
    public UserAuth login(UserDto dto) {
        if (dto.getUsername().equals(tempUser.getUsername()) &&
                dto.getPassword().equals(tempUser.getPassword())) {
            return new UserAuth("success", "Login success!");
        }
        return new UserAuth("fail", "Wrong password or username!");
    }
}
