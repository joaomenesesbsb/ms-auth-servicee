package com.meneses.auth.security;

import com.meneses.auth.features.role.entity.Role;
import com.meneses.auth.features.user.entity.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    private final String secretKey = "Zm9ydGUta2V5LXBhcmEtdGVzdGVzLWRlLXNlZ3VyYW5jYS1qYXZhLXNwcmluZw==";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);

        Role role = new Role();
        role.setName("ROLE_USER");

        user = new User();
        user.setEmail("test@email.com");
        user.getRoles().add(role);
    }

    @Nested
    class GenerateToken{
        @Test
        @DisplayName("Deve gerar um token válido contendo as roles do usuário")
        void shouldGenerateValidToken_whenUserIsValid() {

            String token = jwtService.generateToken(user);

            assertNotNull(token);
            assertEquals("test@email.com", jwtService.extractUsername(token));

        }
        @Test
        @DisplayName("Deve gerar um token com data de expiração no futuro")
        void shouldGenerateToken_withFutureExpirationDate() {

            String token = jwtService.generateToken(user);

            Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

            assertNotNull(expiration);
            assertTrue(expiration.after(new Date()), "A data de expiração deve ser posterior ao momento atual");
        }

        @Test
        @DisplayName("Deve gerar um token assinado corretamente que possa ser validado")
        void shouldGenerateToken_thatIsValidForCorrectUser() {

            String token = jwtService.generateToken(user);

            boolean isValid = jwtService.isTokenValid(token, user);

            assertTrue(isValid, "O token gerado deve ser considerado válido para o mesmo usuário");
        }
    }

    @Nested
    class RefresToken {
        @Test
        @DisplayName("Deve gerar um Refresh Token válido com expiração de 24 horas")
        void shouldGenerateRefreshToken_withLongerExpiration() {

            String refreshToken = jwtService.generateRefreshToken(user);

            Date issuedAt = jwtService.extractClaim(refreshToken, Claims::getIssuedAt);
            Date expiration = jwtService.extractClaim(refreshToken, Claims::getExpiration);

            assertNotNull(refreshToken);
            assertEquals("test@email.com", jwtService.extractUsername(refreshToken));

            long diffInSeconds = (expiration.getTime() - issuedAt.getTime()) / 1000;
            assertEquals(86400, diffInSeconds, "A expiração do Refresh Token deve ser de 86400 segundos (24h)");
        }


        @Test
        @DisplayName("Deve validar corretamente o Refresh Token para o usuário dono")
        void shouldValidateRefreshToken_whenUserMatches() {
            // Arrange
            String refreshToken = jwtService.generateRefreshToken(user);

            // Act
            boolean isValid = jwtService.isTokenValid(refreshToken, user);

            // Assert
            assertTrue(isValid, "O Refresh Token deve ser considerado válido para o usuário que o gerou");
        }
    }

    @Nested
    class TokenIsValid {
        @Test
        @DisplayName("Deve retornar true quando o token for válido e corresponder ao usuário")
        void shouldReturnTrue_whenTokenIsValid() {

            String token = jwtService.generateToken(user);

            boolean isValid = jwtService.isTokenValid(token, user);

            assertTrue(isValid);
        }
    }




}