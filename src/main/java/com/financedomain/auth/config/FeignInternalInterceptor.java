package com.financedomain.auth.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

/**
 * Adds X-User-Role: INTERNAL to every Feign request made by this service.
 * This tells downstream services (e.g. user-service) that the caller is a
 * trusted internal service, not an end-user, so they skip end-user role checks.
 */
@Configuration
public class FeignInternalInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        template.header("X-User-Role", "INTERNAL");
    }
}
