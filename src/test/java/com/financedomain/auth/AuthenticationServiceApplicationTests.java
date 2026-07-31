package com.financedomain.auth;

import com.financedomain.auth.controller.AuthController;
import com.financedomain.auth.service.AuthService;
import com.financedomain.auth.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
		"jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
		"jwt.expiration=3600000",
		"eureka.client.enabled=false",
		"spring.cloud.config.enabled=false"
})
class AuthenticationServiceApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private AuthController authController;

	@Autowired
	private AuthService authService;

	@Autowired
	private JwtService jwtService;

	@Test
	@DisplayName("Vérifie le chargement du contexte Spring et l'instanciation des beans principaux")
	void contextLoads() {
		// Vérification que le conteneur IoC Spring a démarré
		assertNotNull(applicationContext, "Le contexte Spring ne doit pas être nul.");

		// Vérification de la présence et de l'injection des beans principaux
		assertThat(authController).isNotNull();
		assertThat(authService).isNotNull();
		assertThat(jwtService).isNotNull();
	}
}
