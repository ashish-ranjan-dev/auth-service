package com.outing.auth.controller;

//import com.outing.auth.api.exception.AuthException;
import com.outing.commons.api.exception.OutingException;
import com.outing.auth.api.dto.ResetPasswordDto;
import com.outing.auth.api.dto.SignupDto;
import com.outing.auth.api.enums.Constants;
//import com.outing.auth.api.Response;
import com.outing.commons.api.response.OutingResponse;
//import com.outing.commons.api.response.Response;
import com.outing.auth.service.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/auth")
public class AuthUserRestController{

    @Autowired
    AuthUserService authUserService;

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public OutingResponse<Void> signupUser(@RequestBody SignupDto signupDto) {
        try{
           authUserService.signup(signupDto);
            List<String> messages = new ArrayList<>();
            messages.add(Constants.USER_REGISTERED);
//            Response<Void> response = new Response<>(null, messages);
//            return new ResponseEntity<>(response, HttpStatus.OK);
            return new OutingResponse<>(null,HttpStatus.OK,messages);
        }
        catch (DataIntegrityViolationException ex) {
            throw new OutingException(ex.getMostSpecificCause().getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/signin", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public OutingResponse<String> signinUser(@RequestBody MultiValueMap<String, Object> encodedSigninData) throws UnsupportedEncodingException {
        String accessToken = authUserService.signin(encodedSigninData);
        List<String> messages = new ArrayList<>();
        messages.add(Constants.LOGIN_SUCCESSFULLY);
//        Response<String> response = new Response<>(accessToken, messages);
//        return new ResponseEntity<>(response, HttpStatus.OK);
        return new OutingResponse<>(accessToken,HttpStatus.OK,messages);
    }

    @GetMapping(value = "/signup/{userId}/activate/{activationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public OutingResponse<Object> activateUser(@PathVariable("userId") String userId, @PathVariable("activationId") String activationId) {
        String response = authUserService.activateUser(userId, activationId);
        List<String> messages = new ArrayList<>();
        messages.add(Constants.USER_ACTIVATED);
//        Response<Void> response = new Response<>(null, messages);
//        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("http://localhost:4200/")).build();
//        return new ResponseEntity<>(response, HttpStatus.OK);
//        return new OutingResponse<>(null,HttpStatus.FOUND,URI.create("http://localhost:4200/"));
        return new OutingResponse<>("Hi "+response,HttpStatus.ACCEPTED,messages);
    }

    @PostMapping(value = "/reset-password/initiate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public OutingResponse<Void> initiateResetPassword(@RequestBody String usernameOrEmail) {
        authUserService.initiateResetPassword(usernameOrEmail);
        List<String> messages = new ArrayList<>();
        messages.add("Email have been sent to registered email-address");
//        Response<Void> response = new Response<>(null, messages);
//        return new ResponseEntity<>(response, HttpStatus.OK);
        return new OutingResponse<>(null,HttpStatus.OK,messages);
    }

    @PostMapping(value = "/reset-password/user/{userId}/reset/{resetId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public OutingResponse<Void> resetPassword(@PathVariable("userId") String userId,@PathVariable("resetId") String resetId,@RequestBody ResetPasswordDto resetPasswordDto) {
        authUserService.resetPassword(userId, resetId, resetPasswordDto);
        List<String> messages = new ArrayList<>();
        messages.add("Password Reset Successfully");
//        return new ResponseEntity<>(new Response<>(null, messages), HttpStatus.OK);
        return new OutingResponse<>(null,HttpStatus.OK,messages);
    }



}
