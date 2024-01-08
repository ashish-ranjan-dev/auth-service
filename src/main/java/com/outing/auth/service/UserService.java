package com.outing.auth.service;

import com.outing.auth.api.dto.UserDto;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public interface UserService extends Serializable {

    List<UserDto> searchUsers(List<String> id);

    UserDto getUserByUserId(String userId);

    UserDto getUserByName(String name);

    UserDto getUserByEmail(String email);
}
