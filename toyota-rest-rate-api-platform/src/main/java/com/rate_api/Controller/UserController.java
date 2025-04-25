package com.rate_api.Controller;

import com.rate_api.Authentication.UserAuth;
import com.rate_api.Dto.RateDto;
import com.rate_api.Dto.UserDto;
import com.rate_api.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling user-related operations such as login.
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Authenticates a user based on the provided credentials.
     *
     * @param dto the user credentials as a {@link UserDto} object.
     * @return a {@link UserAuth} object indicating authentication status and message.
     */
    @PostMapping("/login")
    public UserAuth login(@RequestBody UserDto dto) {
        return userService.login(dto);
    }
}
