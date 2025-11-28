package Application.controller;

import Application.dto.CreateUserRequest;
import Application.dto.LoginRequest;
import Application.model.User;
import Application.service.KeycloakAuthService;
import Application.service.UserService;
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

    private final KeycloakAuthService keycloakAuthService;
    private final UserService userService;



    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> tokens = keycloakAuthService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody @Valid CreateUserRequest req) {
        // Создаем пользователя в Keycloak
        String keycloakId = keycloakAuthService.createKeycloakUser(
                req.getUsername(),   // username
                req.getPassword(),   // password
                req.getEmail(),      // email
                req.getFirstName(),  // firstName
                req.getLastName()    // lastName
        );

        // 2️⃣ Назначаем роль "user" по умолчанию
        keycloakAuthService.assignUserRole(keycloakId);
        // Сохраняем пользователя в локальной БД
        User user = userService.createUser(keycloakId, req);

        // Логиним сразу и возвращаем токены
        Map<String, Object> tokens = keycloakAuthService.login(req.getUsername(), req.getPassword());

        return ResponseEntity.ok(tokens);
    }
}
