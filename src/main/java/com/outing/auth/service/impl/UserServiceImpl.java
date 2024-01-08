package com.outing.auth.service.impl;

//import com.outing.auth.api.exception.AuthException;
import com.outing.commons.api.exception.OutingException;
import com.outing.auth.api.dto.UserDto;
import com.outing.auth.api.enums.Constants;
import com.outing.auth.model.AuthUserModel;
import com.outing.auth.repository.UserRepository;
import com.outing.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public List<UserDto> searchUsers(List<String> id) {
        List<AuthUserModel> authUserModels = (userRepository.findByIdIn(id));
        List<UserDto> userDtos = new ArrayList<>();
        for(AuthUserModel authUserModel : authUserModels){
            UserDto userDto = new UserDto(authUserModel.getId(), authUserModel.getName(), authUserModel.getEmail());
            userDtos.add(userDto);
        }
        return userDtos;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        Optional<AuthUserModel> authUser = userRepository.findById(userId);
        if(authUser==null || authUser.isEmpty()){
            return null;
        }
        UserDto userDto = new UserDto(authUser.get().getId(),authUser.get().getName(), authUser.get().getEmail());
        return userDto;
    }
//    AuthUser authUser = userRepository.findById(userId).orElseThrow(()->new AuthException(Constants.USER_NOTFOUND, HttpStatus.NOT_FOUND));
//        System.out.println(authUser);
//    UserModelDto userModelDto = new UserModelDto(authUser.getId(),authUser.getName(), authUser.getEmail());

    @Override
    public UserDto getUserByName(String name) {
        AuthUserModel authUserModel = userRepository.findByName(name);
        if(authUserModel ==null){
            throw new OutingException(Constants.USER_NOTFOUND,HttpStatus.NOT_FOUND);
        }
        UserDto userDto = new UserDto(authUserModel.getId(), authUserModel.getName(), authUserModel.getEmail());
        return userDto;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        AuthUserModel authUserModel = userRepository.findByEmail(email);
        System.out.println(authUserModel);
        if(authUserModel ==null){
            return null;
//            throw new AuthException(Constants.USER_NOTFOUND,HttpStatus.NOT_FOUND);
        }
        UserDto userDto = new UserDto(authUserModel.getId(), authUserModel.getName(), authUserModel.getEmail());
        return userDto;
    }
}
