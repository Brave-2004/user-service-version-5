package Application.controller;

import Application.dto.CreateUserRequest;
import Application.dto.LoginRequest;
import Application.service.AuthService;
import Application.service.KeycloakApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakApiService keycloakApiService;
    private final AuthService authService;



    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        return  ResponseEntity.ok(keycloakApiService.login(request.getUsername(), request.getPassword()));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody @Valid CreateUserRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }
}
