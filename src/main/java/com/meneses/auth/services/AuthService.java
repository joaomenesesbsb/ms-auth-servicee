package com.meneses.auth.services;

import com.meneses.auth.dto.LoginRequest;
import com.meneses.auth.dto.LoginResponse;
import com.meneses.auth.dto.RegisterRequest;
import com.meneses.auth.dto.UserResponse;
import com.meneses.auth.entities.Role;
import com.meneses.auth.entities.User;
import com.meneses.auth.exceptions.ResourceNotFoundException;
import com.meneses.auth.repositories.RoleRepository;
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

    @Autowired
    private RoleRepository roleRepository;

    public LoginResponse login(LoginRequest request) {

        // Buscar usuario
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // Busca roles do usuario
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        // Validar senha
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        // Gerar token JWT
        String token = jwtService.generateToken(user);

        // Gerar refresh token
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponse(token, refreshToken, roles);
    }

    public UserResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada"));
        user.getRoles().add(role);

        userRepository.save(user);

        UserResponse dto = new UserResponse(user.getEmail(),
                user.getRoles().stream().map(Role::getName).toList());
        return dto;
    }
}
