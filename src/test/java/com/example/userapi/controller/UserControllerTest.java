package com.example.userapi.controller;

import com.example.userapi.dto.UploadResultDto;
import com.example.userapi.model.User;
import com.example.userapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication auth;

    private User user;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email@email.com");

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGenerateUsers() {
        ResponseEntity<ByteArrayResource> response = userController.generateUsers(3);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Attachment;filename=usersFile.json", response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).get(0));
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
    }

    @Test
    void testUploadUsers_SuccessUpload() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String json = "[{\"username\":\"username\", \"password\":\"password\", \"email\":\"email@email.com\"}]";

        when(file.getBytes()).thenReturn(json.getBytes(StandardCharsets.UTF_8));
        when(file.isEmpty()).thenReturn(false);

        UploadResultDto uploadResultDto = new UploadResultDto();
        when(userService.uploadUsers(any())).thenReturn(uploadResultDto);

        ResponseEntity<?> response = userController.uploadUsers(file);


        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testUploadUsers_EmptyFile() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        ResponseEntity<?> response = userController.uploadUsers(file);

        assertEquals("The uploaded file is empty", response.getBody());
    }

    @Test
    void testGetMyProfile_Unauthorized() {
        when(securityContext.getAuthentication()).thenReturn(null);

        ResponseEntity<?> response = userController.getMyProfile();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getUserProfile() {
        String username = "testUser";
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userController.getUserProfile(username);
        assertEquals(200, response.getStatusCodeValue());
    }
}