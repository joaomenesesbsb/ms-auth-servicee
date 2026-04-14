package com.meneses.auth.features.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meneses.auth.features.auth.dto.LoginRequestDTO;
import com.meneses.auth.features.auth.dto.LoginResponseDTO;
import com.meneses.auth.features.auth.dto.RegisterRequestDTO;
import com.meneses.auth.features.auth.service.AuthService;
import com.meneses.auth.features.user.dto.UserResponseDTO;
import com.meneses.auth.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Nested
    class Login {

        @Test
        void shouldReturn200_whenLoginIsSuccessful() throws Exception {

            LoginRequestDTO request = new LoginRequestDTO("test@email.com", "password123");
            LoginResponseDTO response = new LoginResponseDTO("token-jwt-fake", "refresh-token-fake");

            Mockito.when(authService.login(Mockito.any(LoginRequestDTO.class))).thenReturn(response);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("token-jwt-fake"))
                    .andExpect(jsonPath("$.refreshToken").value("refresh-token-fake"));
        }

        @Test
        void shouldReturn400_whenLoginRequestIsInvalid() throws Exception {

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(""))
                    .andExpect(status().isBadRequest());
        }

    }

    @Nested
    class Register{

        @Test
        void shouldReturn201_whenUserIsRegistered() throws Exception {

            RegisterRequestDTO request = new RegisterRequestDTO("new@email.com", "password123");
            UserResponseDTO response = new UserResponseDTO("new@email.com");

            Mockito.when(authService.register(Mockito.any(RegisterRequestDTO.class))).thenReturn(response);

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value("new@email.com"));
        }
    }

    @Nested
    class Logout {

        @Test
        void shouldReturn204_whenLogoutIsSuccessful() throws Exception {
            String token = "valid-jwt-token";
            String authHeader = "Bearer " + token;

            Mockito.when(jwtService.extractToken(Mockito.any(HttpServletRequest.class)))
                    .thenReturn(token);

            mockMvc.perform(post("/auth/logout")
                            .header("Authorization", authHeader))
                    .andExpect(status().isNoContent());

            Mockito.verify(authService, Mockito.times(1)).logout(token);
        }

        @Test
        void shouldReturn204_whenTokenIsNull() throws Exception {
            Mockito.when(jwtService.extractToken(Mockito.any(HttpServletRequest.class)))
                    .thenReturn(null);

            mockMvc.perform(post("/auth/logout"))
                    .andExpect(status().isNoContent());

            Mockito.verify(authService, Mockito.times(1)).logout(null);
        }
    }


}
