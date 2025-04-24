package com.rate_api.Controller;

import com.rate_api.Authentication.UserAuth;
import com.rate_api.Dto.RateDto;
import com.rate_api.Dto.UserDto;
import com.rate_api.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public UserAuth login(@RequestBody UserDto dto) {
        return userService.login(dto);
    }
}
