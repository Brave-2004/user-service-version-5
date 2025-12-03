package Application.controller;

import Application.dto.CreateUserRequest;
import Application.dto.LoginRequest;
import Application.service.AuthService;
import Application.service.KeycloakApiService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakApiService keycloakApiService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {

            log.info("Login attempt for user: {}", request.getUsername());

            Map<String, Object> tokens = keycloakApiService.login(
                    request.getUsername(),
                    request.getPassword()
            );

            log.debug("Login successful for user: {}", request.getUsername());
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            log.debug("Login FAILED for user: {} - Reason: {}", request.getUsername(), request.getPassword());
            throw e;
        }
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<Map<String, Object>> register(@RequestBody @Valid CreateUserRequest req) {
        try {
            log.info("Registration attempt for username: {}", req.getUsername());

            Map<String, Object> result = authService.register(req);

            log.debug("User registered successfully: {}", req.getUsername());
            return ResponseEntity.ok(result);
        }catch (Exception e) {
            log.debug("Registration Failed....: Reason: []");
            throw e;
        }
    }

}
