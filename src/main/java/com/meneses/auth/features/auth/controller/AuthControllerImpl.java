package com.meneses.auth.features.auth.controller;

import com.meneses.auth.features.auth.service.AuthService;
import com.meneses.auth.features.auth.dto.LoginRequestDTO;
import com.meneses.auth.features.auth.dto.LoginResponseDTO;
import com.meneses.auth.features.auth.dto.RegisterRequestDTO;
import com.meneses.auth.features.user.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticacao", description = "Endpoints de login e registro")
@RestController
@RequestMapping("/auth")
public class AuthControllerImpl implements AuthController{

    private static final Logger logger = LogManager.getLogger(AuthControllerImpl.class);

    @Autowired
    private AuthService authService;

    @Override
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {

        logger.info("Tentativa de login para o usuário: [{}]", request.getEmail());

        LoginResponseDTO loginResponseDTO = authService.login(request);

        return ResponseEntity.ok(loginResponseDTO);
    }

    @Override
    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        logger.info("Solicitação de registro para o e-mail: [{}]", request.getEmail());

        UserResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
