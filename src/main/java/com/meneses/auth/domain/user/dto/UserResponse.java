package com.meneses.auth.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserResponse {
    @Schema(description = "Email do usuario", example = "admin@email.com")
    private String email;
    @Schema(description = "Lista de roles do usuario",
            example = "[\"ROLE_ADMIN\", \"ROLE_USER\"]")
    private List<String> roles;
}
