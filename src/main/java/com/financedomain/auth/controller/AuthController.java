package com.financedomain.auth.controller;

import com.financedomain.auth.dto.LoginRequest;
import com.financedomain.auth.dto.LoginResponse;
import com.financedomain.auth.exception.BadFormatAuthenticationException;
import com.financedomain.auth.exception.NullAuthentificationException;
import com.financedomain.auth.service.AuthService;
import com.financedomain.auth.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (BadFormatAuthenticationException | NullAuthentificationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        if (jwtService.isTokenValid(token)) {
            return ResponseEntity.ok("Token est valide");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token est invalide ou expiré");
    }
}
