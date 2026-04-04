package com.meneses.auth.features.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestDTO {
    @Schema(description = "Nome do role a ser adicionado", example = "ROLE_ADMIN")
    private String roleName;
}
