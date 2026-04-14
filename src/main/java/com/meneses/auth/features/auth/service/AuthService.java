package com.meneses.auth.features.auth.service;

import com.meneses.auth.features.auth.dto.LoginRequestDTO;
import com.meneses.auth.features.auth.dto.LoginResponseDTO;
import com.meneses.auth.features.auth.dto.RegisterRequestDTO;
import com.meneses.auth.features.user.dto.UserResponseDTO;
import com.meneses.auth.features.role.entity.Role;
import com.meneses.auth.features.user.entity.User;
import com.meneses.auth.exceptions.ResourceNotFoundException;
import com.meneses.auth.features.role.repository.RoleRepository;
import com.meneses.auth.features.user.repository.UserRepository;
import com.meneses.auth.security.JwtService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RoleRepository roleRepository;

    public LoginResponseDTO login(@NonNull LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Tentativa de login para e-mail não cadastrado: [{}]", request.getEmail());
                    return new ResourceNotFoundException("Usuário não encontrado");
                });

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Senha inválida fornecida para o usuário: [{}]", request.getEmail());
            throw new RuntimeException("Senha inválida");
        }

        logger.info("Usuário autenticado com sucesso: [{}]", request.getEmail());

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponseDTO(token, refreshToken);
    }

    public UserResponseDTO register(RegisterRequestDTO request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Tentativa de registro com e-mail já existente: [{}]", request.getEmail());
            throw new RuntimeException("Email já cadastrado");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> {
                    logger.error("Falha crítica: ROLE_USER não configurada no banco de dados!");
                    return new ResourceNotFoundException("Role não encontrada");
                });

        user.getRoles().add(role);
        userRepository.save(user);

        logger.info("Novo usuário registrado com sucesso: [{}]", user.getEmail());
        return new UserResponseDTO(user.getEmail());
    }
}
