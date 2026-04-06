package com.meneses.auth.featues.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meneses.auth.features.auth.dto.LoginRequestDTO;
import com.meneses.auth.features.auth.dto.LoginResponseDTO;
import com.meneses.auth.features.auth.dto.RegisterRequestDTO;
import com.meneses.auth.features.auth.service.AuthService;
import com.meneses.auth.features.user.dto.UserResponseDTO;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("Deve realizar login com sucesso e retornar 200")
    void loginSucesso() throws Exception {

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
    @DisplayName("Deve registrar um usuário com sucesso e retornar 201")
    void registerSucesso() throws Exception {

        RegisterRequestDTO request = new RegisterRequestDTO("novo@email.com", "senha123");
        UserResponseDTO response = new UserResponseDTO("novo@email.com");

        Mockito.when(authService.register(Mockito.any(RegisterRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("novo@email.com"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando o corpo da requisição de login for inválido")
    void loginBadRequest() throws Exception {
        // Simula envio de corpo vazio
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }
}
