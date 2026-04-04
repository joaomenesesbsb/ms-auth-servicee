package com.meneses.auth.features.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    @Schema(description = "Digite seu novo email", example = "admin@email.com")
    private String email;
    @Schema(description = "Lista de roles do usuario",
            example = "[\"ROLE_ADMIN\", \"ROLE_USER\"]")
    private List<String> roles;
}
