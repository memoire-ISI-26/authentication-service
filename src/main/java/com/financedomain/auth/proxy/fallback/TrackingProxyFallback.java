package com.financedomain.auth.proxy.fallback;

import com.financedomain.auth.dto.TrackingEvent;
import com.financedomain.auth.proxy.TrackingProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TrackingProxyFallback implements TrackingProxy {

    @Override
    public ResponseEntity<?> collectEvent(TrackingEvent event, String xUserRole) {
        System.err.println("[Fallback] tracking-service est indisponible. Événement de tracking ignoré : " + event.getEventType());
        // Retourne un succès fictif pour ne pas bloquer le flux d'authentification principal
        return ResponseEntity.ok().build();
    }
}
