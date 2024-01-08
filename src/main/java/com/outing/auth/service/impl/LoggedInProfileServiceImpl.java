package com.outing.auth.service.impl;

//import com.outing.auth.api.exception.AuthException;
import com.outing.commons.api.exception.OutingException;
import com.outing.auth.api.dto.ProfileDto;
import com.outing.auth.api.enums.Constants;
import com.outing.auth.model.AuthUserModel;
import com.outing.auth.repository.UserRepository;
import com.outing.auth.service.LoggedInUserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class LoggedInProfileServiceImpl implements LoggedInUserProfileService {

    @Autowired
    private UserRepository userRepository;

    public ProfileDto getUserProfile(String id) {
        AuthUserModel authUserModel = userRepository.findById(id)
                .orElseThrow(() -> new OutingException(Constants.USER_NOTFOUND, HttpStatus.INTERNAL_SERVER_ERROR));
        return new ProfileDto(authUserModel.getId(), authUserModel.getUsername(), authUserModel.getEmail());
    }
}
