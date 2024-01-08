package com.outing.auth.api.dto;
import lombok.*;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {
    private String id;
    private String name;
    private String email;
}
