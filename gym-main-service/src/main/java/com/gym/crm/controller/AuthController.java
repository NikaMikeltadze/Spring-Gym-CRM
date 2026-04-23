package com.gym.crm.controller;

import com.gym.crm.config.auth.JwtTokenService;
import com.gym.crm.dto.request.ChangeLoginRequest;
import com.gym.crm.dto.request.LoginRequest;
import com.gym.crm.dto.response.ApiErrorResponse;
import com.gym.crm.dto.response.auth.LoginResponse;
import com.gym.crm.exception.UnauthorizedException;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Authentication", description = "Authentication and credential management endpoints")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final GymFacade gymFacade;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credentials are valid and JWT token is issued"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid username or password",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "423", description = "Account is temporarily locked",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Auth login request received for username={}", request.getUsername());
        assertAuthenticated(request.getUsername(), request.getPassword());

        String token = jwtTokenService.generateToken(request.getUsername());
        LoginResponse response = LoginResponse.builder()
                .username(request.getUsername())
                .token(token)
                .tokenType("Bearer")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/login")
    @Operation(summary = "Authenticate user credentials (legacy query-param format)")
    public ResponseEntity<LoginResponse> loginLegacy(@Valid @ModelAttribute LoginRequest request) {
        return login(request);
    }

    @PostMapping("/change_password")
    @Operation(summary = "Change user password")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid username or password",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "423", description = "Account is temporarily locked",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> changeLogin(@Valid @RequestBody ChangeLoginRequest request) {
        assertAuthenticated(request.getUsername(), request.getPassword());
        gymFacade.changeUserPassword(request);
        return ResponseEntity.ok().build();
    }

    private void assertAuthenticated(String username, String password) {
        try {
            if (!authenticationService.authenticate(username, password)) {
                throw new UnauthorizedException("Invalid username or password");
            }
        } catch (IllegalArgumentException ex) {
            throw new UnauthorizedException("Invalid username or password");
        }
    }
}
