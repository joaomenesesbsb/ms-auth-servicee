package com.meneses.auth.features.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    @Schema(description = "Email cadastrado no sistema", example = "teste_update@email.com")
    private String email;

}
