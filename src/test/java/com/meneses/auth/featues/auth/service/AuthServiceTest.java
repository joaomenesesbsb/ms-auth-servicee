package com.meneses.auth.featues.auth.service;

import com.meneses.auth.exceptions.ResourceNotFoundException;
import com.meneses.auth.features.auth.dto.LoginRequestDTO;
import com.meneses.auth.features.auth.dto.LoginResponseDTO;
import com.meneses.auth.features.auth.dto.RegisterRequestDTO;
import com.meneses.auth.features.auth.service.AuthService;
import com.meneses.auth.features.role.entity.Role;
import com.meneses.auth.features.role.repository.RoleRepository;
import com.meneses.auth.features.user.entity.User;
import com.meneses.auth.features.user.repository.UserRepository;
import com.meneses.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthService authService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName("ROLE_USER");

        user = new User();
        user.setEmail("teste@email.com");
        user.setPassword("senhaCriptografada");
        user.getRoles().add(role);
    }

    @Test
    @DisplayName("Deve fazer login com sucesso quando credenciais forem válidas")
    void login_Sucesso() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("teste@email.com", "senha123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("token-jwt");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");

        // Act
        LoginResponseDTO response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("token-jwt", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha estiver incorreta")
    void login_SenhaIncorreta() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("teste@email.com", "senhaErrada");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });

        assertEquals("Senha inválida", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando usuário não existir")
    void login_UsuarioNaoEncontrado() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("naoexiste@email.com", "123");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar e-mail já duplicado")
    void register_EmailDuplicado() {
        // Arrange
        RegisterRequestDTO request = new RegisterRequestDTO("teste@email.com", "senha123");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(request);
        });

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}