package com.outing.auth.api.controller;

import com.outing.auth.api.dto.UserDto;
//import com.outing.auth.api.Response;
import com.outing.commons.api.response.OutingResponse;
//import com.outing.commons.api.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(url = "http://localhost:8080",value = "auth-service")
public interface UserController {
    @GetMapping("/service/users/search")
    ResponseEntity<List<UserDto>> searchUsers(@RequestBody List<String> id);

    @GetMapping(value = "/service/users/search/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserDto> getUserByUserId(@PathVariable("userId") String userId);

    @GetMapping(value = "/service/name/search/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserDto> getUserByName(@PathVariable("name") String name);

    @GetMapping(value = "/service/email/search/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String email);
}
