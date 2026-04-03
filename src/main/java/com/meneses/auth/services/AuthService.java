package com.meneses.auth.services;

import com.meneses.auth.dto.LoginRequest;
import com.meneses.auth.dto.LoginResponse;
import com.meneses.auth.entities.User;
import com.meneses.auth.repositories.UserRepository;
import com.meneses.auth.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        // Buscar usuário
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Validar senha
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        // Gerar token JWT
        String token = jwtService.generateToken(user);

        // Gerar refresh token
        String refreshToken = jwtService.generateRefreshToken(user);

        // Retornar resposta
        return new LoginResponse(token, refreshToken, List.of(user.getRole()));
    }
}
