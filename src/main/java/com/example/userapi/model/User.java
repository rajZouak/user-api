package com.example.userapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String birthDate;
    private String city;
    private String country;
    private String avatar;
    private String company;
    private String jobPosition;
    private String mobilePhone;
    @NotBlank
    private String username;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String role;

    public User(String username, String password, String email) {}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String prefix = "ROLE_";
        String prefixedRole = role.startsWith(prefix) ? role : prefix + role;
        return List.of(new SimpleGrantedAuthority(prefixedRole));
    }

}
