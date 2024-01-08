package com.outing.auth.controller;


import com.outing.auth.api.dto.ProfileDto;
//import com.outing.auth.api.Response;
//import com.outing.commons.api.response.Response;
import com.outing.auth.service.impl.LoggedInProfileServiceImpl;
import com.outing.auth.repository.UserRepository;
import com.outing.auth.security.util.PrincipalDetails;
import com.outing.commons.api.response.OutingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/auth")
public class LoggedInUserRestController {
    private final LoggedInProfileServiceImpl loggedInProfileService;

    private final PrincipalDetails principalDetails;

    @Autowired
    public UserRepository userRepository;

    public LoggedInUserRestController(LoggedInProfileServiceImpl loggedInUserProfileService, PrincipalDetails principalDetails) {
        this.loggedInProfileService = loggedInUserProfileService;
        this.principalDetails=principalDetails;
    }

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public OutingResponse<ProfileDto> getUserProfile() {
        String userIdFromToken=this.principalDetails.getPrincipalDetails().getId();
        ProfileDto userProfile=this.loggedInProfileService.getUserProfile(userIdFromToken);
//        Response<ProfileDto> response=new Response<>(userProfile,new ArrayList<>());
//        return new ResponseEntity<>(response, HttpStatus.OK);
        return new OutingResponse<>(userProfile);
    }



}
