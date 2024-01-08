package com.outing.auth.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto implements Serializable {
    private String id;
    private String username;
    private String email;
}
