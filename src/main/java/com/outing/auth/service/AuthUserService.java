package com.outing.auth.service;

import com.outing.auth.api.dto.ResetPasswordDto;
import com.outing.auth.api.dto.SignupDto;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

@Service
public interface AuthUserService extends Serializable {

    void signup(SignupDto signupDto);

    String signin(MultiValueMap<String, Object> encodedSigninData) throws UnsupportedEncodingException;

    String activateUser(String userId, String activationId);

    void initiateResetPassword(String usernameOrEmail);

    void resetPassword(String userId, String resetId, ResetPasswordDto resetPasswordDto);
}
