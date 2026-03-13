package com.gym.crm.controller;

import com.gym.crm.dto.request.ChangeLoginRequest;
import com.gym.crm.dto.request.LoginRequest;
import com.gym.crm.exception.UnauthorizedException;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final AuthenticationService authenticationService;
    private final GymFacade gymFacade;

    @GetMapping("/login")
    public ResponseEntity<Void> login(@Valid @ModelAttribute LoginRequest request) {
        log.info("Auth login request received for username={}", request.getUsername());
        try {
            assertAuthenticated(request.getUsername(), request.getPassword());
            return ResponseEntity.ok().build();
        } catch (UnauthorizedException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping
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
