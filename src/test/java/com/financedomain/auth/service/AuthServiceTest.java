package com.financedomain.auth.service;

import com.financedomain.auth.dto.LoginRequest;
import com.financedomain.auth.dto.LoginResponse;
import com.financedomain.auth.dto.UserDto;
import com.financedomain.auth.exception.BadFormatAuthenticationException;
import com.financedomain.auth.proxy.TrackingProxy;
import com.financedomain.auth.proxy.UserProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserProxy userProxy;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TrackingProxy trackingProxy;

    @InjectMocks
    private AuthService authService;

    private UserDto mockAdminUser;
    private UserDto mockClientUser;

    @BeforeEach
    void setUp() {
        mockAdminUser = UserDto.builder()
                .id(1L)
                .username("admin")
                .password("$2a$10$encodedPasswordAdmin")
                .role("ADMINISTRATOR")
                .build();

        mockClientUser = UserDto.builder()
                .id(2L)
                .number("771234567")
                .password("$2a$10$encodedPasswordClient")
                .role("CLIENT")
                .build();
    }

    @Test
    @DisplayName("Devrait authentifier un administrateur avec succès")
    void shouldAuthenticateAdminSuccessfully() {
        LoginRequest request = new LoginRequest("admin", "adminPass");

        when(userProxy.getAdminByUsername("admin")).thenReturn(ResponseEntity.ok(mockAdminUser));
        when(passwordEncoder.matches("adminPass", mockAdminUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(1L, "admin", "ADMINISTRATOR")).thenReturn("mock-admin-jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock-admin-jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals("ADMINISTRATOR", response.getRole());

        verify(userProxy).getAdminByUsername("admin");
        verify(passwordEncoder).matches("adminPass", mockAdminUser.getPassword());
        verify(jwtService).generateToken(1L, "admin", "ADMINISTRATOR");
    }

    @Test
    @DisplayName("Devrait authentifier un client avec succès si ce n'est pas un admin")
    void shouldAuthenticateClientSuccessfully() {
        LoginRequest request = new LoginRequest("771234567", "clientPass");

        when(userProxy.getAdminByUsername("771234567")).thenThrow(new RuntimeException("Admin non trouvé"));
        when(userProxy.getClientByNumber("771234567")).thenReturn(ResponseEntity.ok(mockClientUser));
        when(passwordEncoder.matches("clientPass", mockClientUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(2L, "771234567", "CLIENT")).thenReturn("mock-client-jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock-client-jwt-token", response.getToken());
        assertEquals("CLIENT", response.getRole());

        verify(userProxy).getAdminByUsername("771234567");
        verify(userProxy).getClientByNumber("771234567");
        verify(passwordEncoder).matches("clientPass", mockClientUser.getPassword());
    }

    @Test
    @DisplayName("Devrait lever BadFormatAuthenticationException si l'utilisateur est introuvable")
    void shouldThrowExceptionWhenUserNotFound() {
        LoginRequest request = new LoginRequest("unknown", "password");

        when(userProxy.getAdminByUsername("unknown")).thenThrow(new RuntimeException("Not found"));
        when(userProxy.getClientByNumber("unknown")).thenThrow(new RuntimeException("Not found"));

        BadFormatAuthenticationException exception = assertThrows(
                BadFormatAuthenticationException.class,
                () -> authService.login(request)
        );

        assertEquals("Identifiant ou mot de passe incorrect", exception.getMessage());
    }

    @Test
    @DisplayName("Devrait lever BadFormatAuthenticationException si le mot de passe est incorrect")
    void shouldThrowExceptionWhenPasswordInvalid() {
        LoginRequest request = new LoginRequest("admin", "wrongPassword");

        when(userProxy.getAdminByUsername("admin")).thenReturn(ResponseEntity.ok(mockAdminUser));
        when(passwordEncoder.matches("wrongPassword", mockAdminUser.getPassword())).thenReturn(false);

        BadFormatAuthenticationException exception = assertThrows(
                BadFormatAuthenticationException.class,
                () -> authService.login(request)
        );

        assertEquals("Identifiant ou mot de passe incorrect", exception.getMessage());
        verify(jwtService, never()).generateToken(any(), any(), any());
    }

    @Test
    @DisplayName("Devrait lever BadFormatAuthenticationException si la requête ou ses champs sont nuls")
    void shouldThrowExceptionWhenRequestOrFieldsNull() {
        LoginRequest nullIdentifierRequest = new LoginRequest(null, "pass");
        LoginRequest nullPasswordRequest = new LoginRequest("user", null);

        assertThrows(BadFormatAuthenticationException.class, () -> authService.login(null));
        assertThrows(BadFormatAuthenticationException.class, () -> authService.login(nullIdentifierRequest));
        assertThrows(BadFormatAuthenticationException.class, () -> authService.login(nullPasswordRequest));
    }
}
