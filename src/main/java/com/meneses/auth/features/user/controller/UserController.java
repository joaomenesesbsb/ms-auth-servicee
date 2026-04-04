package com.meneses.auth.features.user.controller;

import com.meneses.auth.features.user.dto.RoleRequestDTO;
import com.meneses.auth.features.user.dto.UserRequestDTO;
import com.meneses.auth.features.user.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;


public interface UserController {

    @Operation(summary = "Busca usuário por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/{id}")
    ResponseEntity<UserResponseDTO> findById(@PathVariable Long id);

    @Operation(summary = "Atualiza dados do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucesso na atualização"),
            @ApiResponse(responseCode = "400", description = "Erro na requisição (JSON malformado)"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping(value = "/{id}")
    ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @RequestBody UserRequestDTO request);

    @Operation(summary = "Lista usuários paginados")

    @GetMapping
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista recuperada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class) // Indica que o retorno é uma página
                    )
            ),
            @ApiResponse(responseCode = "403", description = "Acesso negado - Requer ROLE_ADMIN")
    })
    ResponseEntity<Page<UserResponseDTO>> findAll(@RequestParam String email, Pageable pageable);

    @Operation(summary = "Adiciona Role ao usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucesso na atualização"),
            @ApiResponse(responseCode = "400", description = "Erro na requisição (JSON malformado)"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PostMapping("/{id}/roles")
    ResponseEntity<Void> addRole(@PathVariable Long id, @RequestBody RoleRequestDTO request);

    @Operation(summary = "Remover usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito: Usuário possui vínculos e não pode ser excluído")
    })
    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);
}
