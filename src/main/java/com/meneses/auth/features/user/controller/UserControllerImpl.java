package com.meneses.auth.features.user.controller;

import com.meneses.auth.features.user.dto.RoleRequestDTO;
import com.meneses.auth.features.user.dto.UserRequestDTO;
import com.meneses.auth.features.user.dto.UserResponseDTO;
import com.meneses.auth.features.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuario")
@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class UserControllerImpl implements UserController {

    private static final Logger logger = LogManager.getLogger(UserControllerImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Override
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO request){
        logger.info("Atualizando dados do usuário ID: [{}]", id);
        return ResponseEntity.ok(userService.update(id,request));
    }

    @Override
    public ResponseEntity<Page<UserResponseDTO>> findAll(
            @RequestParam(value = "email", defaultValue = "")
            String email,
            Pageable pageable) {
        Page<UserResponseDTO> list = userService.findAll(email,pageable);
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<Void> addRole(
            @PathVariable Long id,
            @RequestBody RoleRequestDTO request) {
        logger.info("Solicitação para adicionar role [{}] ao usuário ID: [{}]", request.getRoleName(), id);
        userService.addRoleToUser(id, request.getRoleName());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        logger.warn("Solicitação de exclusão do usuário ID: [{}]", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
