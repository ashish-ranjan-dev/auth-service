package com.outing.auth.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupDto implements Serializable {
    private String username;
    private String email;
    private String name;
    private String password;
    private String confirmPassword;
}
