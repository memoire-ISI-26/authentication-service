package com.financedomain.auth.dto;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String password; // hash BCrypt
    private String role;
    
    // Pour l'admin
    private String username;
    
    // Pour le client
    private String number;
}
