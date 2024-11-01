package com.example.userapi.service;

import com.example.userapi.model.User;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthenticationService implements UserDetailsService {

    private final UserService userService;

    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
