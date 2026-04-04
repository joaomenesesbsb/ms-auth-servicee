package com.meneses.auth.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    @Schema(description = "Token JWT. Use no header: Authorization: Bearer {token}",
            example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
    @Schema(description = "Token para renovação do JWT",
            example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshtoken;
    @Schema(description = "Lista de roles do usuario",
            example = "[\"ROLE_ADMIN\", \"ROLE_USER\"]")
    private List<String> roles;
}
