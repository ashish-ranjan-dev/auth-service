package com.outing.auth.controller;

import com.outing.auth.api.controller.UserController;
import com.outing.auth.api.dto.UserDto;
//import com.outing.auth.api.Response;
//import com.outing.commons.api.response.Response;
import com.outing.auth.service.UserService;
import com.outing.auth.service.impl.UserServiceImpl;
import com.outing.commons.api.response.OutingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserRestController implements UserController {

    @Autowired
    UserService userService;

    @Override
    public ResponseEntity<List<UserDto>> searchUsers(List<String> id) {
        List<UserDto> userDtos = userService.searchUsers(id);
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDto> getUserByUserId(String userId) {
        UserDto userDto = userService.getUserByUserId(userId);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDto> getUserByName(String name) {
        UserDto userDto = userService.getUserByName(name);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDto> getUserByEmail(String email) {
        UserDto userDto = userService.getUserByEmail(email);
//        System.out.println(userDto.getName());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
