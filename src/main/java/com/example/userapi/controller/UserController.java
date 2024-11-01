package com.example.userapi.controller;

import com.example.userapi.dto.AuthRequestDto;
import com.example.userapi.dto.AuthResponseDto;
import com.example.userapi.dto.UploadResultDto;
import com.example.userapi.model.User;
import com.example.userapi.service.UserService;
import com.example.userapi.util.JwtUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.github.javafaker.Faker;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final Faker faker = new Faker();

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/generate")
    public ResponseEntity<ByteArrayResource> generateUsers(@RequestParam int count) {
        List<User> users = new ArrayList<>();

        for (int i = 0; i<count; i++) {
            User user = new User();
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setBirthDate(faker.date().birthday().toString());
            user.setCity(faker.address().city());
            user.setCountry(faker.address().countryCode());
            user.setAvatar(faker.internet().avatar());
            user.setCompany(faker.company().name());
            user.setJobPosition(faker.job().position());
            user.setMobilePhone(faker.phoneNumber().phoneNumber());
            user.setUsername(faker.name().username());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(faker.internet().password(6,10));
            user.setRole(i % 2 == 0 ? "user" : "admin");
            users.add(user);
        }

        // Convert to a json
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] data = objectMapper.writeValueAsString(users).getBytes(StandardCharsets.UTF_8);
            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "Attachment;filename=usersFile.json")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadUsers(@RequestParam("file") MultipartFile file) {
     if (file.isEmpty()) {
         return ResponseEntity.badRequest().body("The uploaded file is empty");
     }

     try {
         ObjectMapper objectMapper = new ObjectMapper();
         objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

         byte[] bytes = file.getBytes();
         System.out.println("Received records: " + new String(bytes));

         User[] usersArray = objectMapper.readValue(bytes, User[].class);
         List<User> users = List.of(usersArray);

         UploadResultDto result = userService.uploadUsers(users);
         return ResponseEntity.ok(result);
     } catch (IOException exception) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something wrong during processing the file");
     }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Verifying if the user is authenticated or not
        if(auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

       String email = auth.getName();
       System.out.println("Authenticated email: " + email);
       Optional<User> user = userService.findByUsernameOrEmail(email);
       return user.map(ResponseEntity::ok)
               .orElseGet(() -> {
                   System.out.println("User not found: " + email);
                   return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
               });
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
