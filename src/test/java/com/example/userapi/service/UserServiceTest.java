package com.example.userapi.service;

import com.example.userapi.dto.UploadResultDto;
import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email@email.com");
    }
    @Test
    void testFindByUsername_Found() {
        String username = "username";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindByEmail_Found() {
        String email = "email@email.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testFindByUsernameOrEmail_FoundByUsername() {
        String username = "username";

        when(userRepository.findByUsernameOrEmail(username, username)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsernameOrEmail(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(userRepository, times(1)).findByUsernameOrEmail(username, username);
    }

    @Test
    void testUploadUsers() {
        User user1 = new User("username1", "password", "email1@email.com");
        User user2 = new User("username2", "password", "email2@email.com");
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.existsByUsername("username1")).thenReturn(false);
        when(userRepository.existsByEmail("email1@email.com")).thenReturn(false);

        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        UploadResultDto result = userService.uploadUsers(users);

        verify(userRepository, times(2)).save(user1);
        verify(userRepository, times(2)).save(user2);
    }

    @Test
    void testGetUserDetailsByEmail_Found() {
        String email = "email@email.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.getUserDetailsByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testGetUserDetailsByEmail_NotFound() {
        String email = "notfound@email.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> userService.getUserDetailsByEmail(email));

        assertEquals("User with email" + email + " not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }
}