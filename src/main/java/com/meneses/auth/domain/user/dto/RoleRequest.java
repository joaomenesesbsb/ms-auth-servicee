package com.meneses.auth.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
    @Schema(description = "Nome do role a ser adicionado", example = "ROLE_ADMIN")
    private String roleName;
}
