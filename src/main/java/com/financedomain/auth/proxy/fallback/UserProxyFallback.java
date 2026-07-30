package com.financedomain.auth.proxy.fallback;

import com.financedomain.auth.proxy.UserProxy;
import com.financedomain.auth.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserProxyFallback implements UserProxy {

    @Override
    public ResponseEntity<UserDto> getAdminByUsername(String username) {
        log.warn("[Fallback] user-service est indisponible. Impossible de récupérer l'admin : {}", username);
        return ResponseEntity.status(503).build();
    }

    @Override
    public ResponseEntity<UserDto> getClientByNumber(String number) {
        log.warn("[Fallback] user-service est indisponible. Impossible de récupérer le client : {}", number);
        return ResponseEntity.status(503).build();
    }
}
