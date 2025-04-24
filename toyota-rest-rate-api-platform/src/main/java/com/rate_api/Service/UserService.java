package com.rate_api.Service;

import com.rate_api.Authentication.UserAuth;
import com.rate_api.Dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public interface UserService {
    UserAuth login(UserDto dto);
}
