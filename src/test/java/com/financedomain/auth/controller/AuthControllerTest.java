package com.financedomain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financedomain.auth.dto.LoginRequest;
import com.financedomain.auth.dto.LoginResponse;
import com.financedomain.auth.exception.BadFormatAuthenticationException;
import com.financedomain.auth.service.AuthService;
import com.financedomain.auth.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("POST /auth/login - Devrait retourner 200 OK et le token si l'authentification réussit")
    void shouldReturn200AndTokenOnSuccessfulLogin() throws Exception {
        LoginRequest request = new LoginRequest("admin", "adminPass");
        LoginResponse response = LoginResponse.builder()
                .token("valid-jwt-token")
                .type("Bearer")
                .role("ADMINISTRATOR")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("valid-jwt-token"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.role").value("ADMINISTRATOR"));
    }

    @Test
    @DisplayName("POST /auth/login - Devrait retourner 401 Unauthorized si l'authentification échoue")
    void shouldReturn401OnLoginFailure() throws Exception {
        LoginRequest request = new LoginRequest("wrongUser", "wrongPass");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadFormatAuthenticationException("Identifiant ou mot de passe incorrect"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Identifiant ou mot de passe incorrect"));
    }

    @Test
    @DisplayName("GET /auth/validate - Devrait retourner 200 OK quand le token est valide")
    void shouldReturn200WhenTokenIsValid() throws Exception {
        String token = "valid-token-123";
        when(jwtService.isTokenValid(token)).thenReturn(true);

        mockMvc.perform(get("/auth/validate")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string("Token est valide"));
    }

    @Test
    @DisplayName("GET /auth/validate - Devrait retourner 401 Unauthorized quand le token est invalide")
    void shouldReturn401WhenTokenIsInvalid() throws Exception {
        String token = "expired-token-456";
        when(jwtService.isTokenValid(token)).thenReturn(false);

        mockMvc.perform(get("/auth/validate")
                        .param("token", token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Token est invalide ou expiré"));
    }
}
