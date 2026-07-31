package com.financedomain.auth.service;

import com.financedomain.auth.dto.LoginRequest;
import com.financedomain.auth.dto.LoginResponse;
import com.financedomain.auth.exception.BadFormatAuthenticationException;
import com.financedomain.auth.proxy.UserProxy;
import com.financedomain.auth.dto.UserDto;
import com.financedomain.auth.proxy.TrackingProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserProxy userProxy;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TrackingProxy trackingProxy;

    public LoginResponse login(LoginRequest request) {
        if (request == null || request.getIdentifier() == null || request.getPassword() == null) {
            throw new BadFormatAuthenticationException("Identifiant et mot de passe sont obligatoires.");
        }

        UserDto user = null;
        
        // 1. Tenter de trouver un admin par username
        try {
            user = userProxy.getAdminByUsername(request.getIdentifier()).getBody();
        } catch (Exception e) {
            log.warn("Recherche Admin pour {}: {}", request.getIdentifier(), e.getMessage());
            // Ignorer, peut-être que c'est un client
        }

        // 2. Si pas admin, tenter de trouver un client par number
        if (user == null) {
            try {
                user = userProxy.getClientByNumber(request.getIdentifier()).getBody();
            } catch (Exception e) {
                log.warn("Recherche Client pour {}: {}", request.getIdentifier(), e.getMessage());
                // Utilisateur introuvable
            }
        }

        if (user == null) {
            log.warn("=> Utilisateur {} introuvable en base de données.", request.getIdentifier());
            throw new BadFormatAuthenticationException("Identifiant ou mot de passe incorrect");
        }

        log.info("=> Utilisateur {} trouvé !", request.getIdentifier());

        // 3. Vérifier le mot de passe
        boolean isPasswordValid = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!isPasswordValid) {
            throw new BadFormatAuthenticationException("Identifiant ou mot de passe incorrect");
        }

        // 4. Générer le token JWT
        String token = jwtService.generateToken(user.getId(), request.getIdentifier(), user.getRole());

        // 5. Retourner la réponse
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .role(user.getRole())
                .build();
    }
}
