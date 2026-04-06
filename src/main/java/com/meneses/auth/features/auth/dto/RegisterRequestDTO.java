package com.meneses.auth.features.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    @Schema(description = "Email do usuário", example = "admin@email.com")
    private String email;
    @Schema(description = "Senha do usuário", example = "123456")
    private String password;
}
