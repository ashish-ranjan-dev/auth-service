package com.outing.auth.service.impl;

import com.outing.auth.model.AuthUserModel;
import com.outing.auth.security.model.UserModel;
import com.outing.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final AuthUserModel authUserModel = userRepository.findByUsername(username);
        if (authUserModel == null) {
            throw new UsernameNotFoundException("username not found");
        }
        if (authUserModel.getActivationId() != null && !authUserModel.getActivationId().isEmpty()) {
            return new UserModel(authUserModel.getId(), username, authUserModel.getPassword(), authUserModel.getEmail(), Collections.emptySet(), false);
        } else {
            return new UserModel(authUserModel.getId(), username, authUserModel.getPassword(), authUserModel.getEmail(), Collections.emptySet(), true);
        }
    }

}
