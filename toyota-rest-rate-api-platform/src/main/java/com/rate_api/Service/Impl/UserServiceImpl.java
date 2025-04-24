package com.rate_api.Service.Impl;

import com.rate_api.Authentication.UserAuth;
import com.rate_api.Dto.UserDto;
import com.rate_api.Service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserDto tempUser;

    public UserServiceImpl() {
        tempUser = new UserDto("1234","1234");
    }

    @Override
    public UserAuth login(UserDto dto) {
        if(dto.getUsername().equals(tempUser.getUsername()) && dto.getPassword().equals(tempUser.getPassword())) {
            return new UserAuth("success","Login success!");
        }
        return new UserAuth("fail","Wrong password or username!");
    }
}
