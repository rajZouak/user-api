package com.example.userapi.service;

import com.example.userapi.dto.UploadResultDto;
import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }

    public UploadResultDto uploadUsers(List<User> users) {
        int success = 0;
        int duplicate = 0;

        for (User user : users) {
            if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByEmail(user.getEmail())) {
                duplicate++;
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
                success++;
            }
        }

        return new UploadResultDto(success, duplicate);
    }

    public User getUserDetailsByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User with email" + email + " not found"));
    }
}
