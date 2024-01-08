package com.outing.auth.security.util;

//import com.outing.auth.api.exception.AuthException;
import com.outing.commons.api.exception.OutingException;
import com.outing.auth.api.enums.Constants;
import com.outing.auth.security.model.UserModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PrincipalDetails {

    public UserModel getPrincipalDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return (UserModel) auth.getPrincipal();
        } else {
            throw new OutingException(Constants.AUTH_FAILED_MESSAGE, HttpStatus.FORBIDDEN);
        }
    }
}
