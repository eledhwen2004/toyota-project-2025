package com.rate_api.Service.Impl;

import com.rate_api.Authentication.UserAuth;
import com.rate_api.Dto.UserDto;
import com.rate_api.Service.UserService;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link UserService} interface that handles user authentication.
 * This service checks the provided credentials against a hardcoded temporary user.
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * A temporary user used for authentication purposes.
     * In a real application, this would be replaced with a user database lookup.
     */
    private UserDto tempUser;

    /**
     * Constructs a {@link UserServiceImpl} and initializes a temporary user with hardcoded credentials.
     */
    public UserServiceImpl() {
        tempUser = new UserDto("1234", "1234");
    }

    /**
     * Authenticates a user based on the provided {@link UserDto}.
     * Checks if the provided username and password match the hardcoded temporary user credentials.
     *
     * @param dto the user credentials as a {@link UserDto}.
     * @return a {@link UserAuth} object indicating whether the login was successful or not.
     */
    @Override
    public UserAuth login(UserDto dto) {
        if (dto.getUsername().equals(tempUser.getUsername()) && dto.getPassword().equals(tempUser.getPassword())) {
            return new UserAuth("success", "Login success!");
        }
        return new UserAuth("fail", "Wrong password or username!");
    }
}
