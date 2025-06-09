package com.rate_api.Controller;

import com.rate_api.Authentication.UserAuth;
import com.rate_api.Dto.RateDto;
import com.rate_api.Dto.UserDto;
import com.rate_api.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that handles user-related operations such as authentication.
 * <p>
 * All endpoints in this controller are prefixed with {@code /api}.
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Authenticates the user based on the provided credentials.
     *
     * @param dto a {@link UserDto} object containing username and password
     * @return a {@link UserAuth} object indicating authentication status and message
     */
    @PostMapping("/login")
    public UserAuth login(@RequestBody UserDto dto) {
        return userService.login(dto);
    }
}
