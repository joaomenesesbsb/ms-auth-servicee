package com.meneses.auth.features.auth.controller;

import com.meneses.auth.features.auth.dto.LoginRequestDTO;
import com.meneses.auth.features.auth.dto.LoginResponseDTO;
import com.meneses.auth.features.auth.dto.RegisterRequestDTO;
import com.meneses.auth.features.user.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthController {

    @Operation(summary = "Login user",
            description = "Log in, validate password, generate JWT token, and generate refresh token."
    )
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "login successful",
                    content = @Content( mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse( responseCode = "401",  description = "Invalid credentials",
                    content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse( responseCode = "400",  description = "Invalid data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse( responseCode = "500",  description = "Internal server error",
                    content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request);

    @Operation(summary = "Register new user", description = "Creates a new user with the default USER role.")
    @ApiResponses({
            @ApiResponse( responseCode = "201", description = "User successfully registered",
                    content = @Content( mediaType = "application/json",  schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse( responseCode = "400", description = "Invalid data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse( responseCode = "409", description = "Email already register",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse( responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    ResponseEntity<UserResponseDTO> register(@RequestBody RegisterRequestDTO request);

    @Operation(summary = "User logout",
            description = "Invalidates the current JWT token, adding in to the blacklist in Redis.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logout success"),
            @ApiResponse(responseCode = "401", description = "Invalid or missing token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request);
}
