package com.meneses.auth.features.user.controller;

import com.meneses.auth.features.user.dto.RoleRequestDTO;
import com.meneses.auth.features.user.dto.UserRequestDTO;
import com.meneses.auth.features.user.dto.UserResponseDTO;
import com.meneses.auth.features.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        UserResponseDTO response = userService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO request){
        UserResponseDTO response = userService.update(id,request);
        return ResponseEntity.ok(response);
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
        userService.addRoleToUser(id, request.getRoleName());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
