package com.example.userapi.controller;

import com.example.userapi.dto.AuthRequestDto;
import com.example.userapi.dto.AuthResponseDto;
import com.example.userapi.model.User;
import com.example.userapi.service.AuthenticationService;
import com.example.userapi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, AuthenticationService authenticationService) {
        this.authenticationManager = authenticationManager;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<?> authUser(@RequestBody AuthRequestDto authRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            // Generate tokens
            User authenticatedUser = (User) auth.getPrincipal();
            String token = JwtUtil.generateToken(authenticatedUser.getEmail());
            System.out.println("Generated Token: " + token);

            return ResponseEntity.ok(new AuthResponseDto(token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password, please try again! ");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong during Authentication process!");
        }
    }
}
