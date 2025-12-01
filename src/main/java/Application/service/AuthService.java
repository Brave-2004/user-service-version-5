package Application.service;

import Application.dto.CreateUserRequest;
import Application.dto.LoginRequest;
import Application.exception.InvalidCredentialsException;
import Application.model.User;
import Application.service.KeycloakApiService;
import Application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KeycloakApiService keycloakApiService;
    private final UserService userService;
    private final LoginAttemptService loginAttemptService;

    public Map<String, Object> register(CreateUserRequest req) {

        // Создаем пользователя в Keycloak
        String keycloakId = keycloakApiService.createKeycloakUser(
                req.getUsername(),   // username
                req.getPassword(),   // password
                req.getEmail(),      // email
                req.getFirstName(),  // firstName
                req.getLastName()    // lastName
        );

        // 2️⃣ Назначаем роль "user" по умолчанию
        keycloakApiService.assignUserRole(keycloakId);
        // Сохраняем пользователя в локальной БД
        User user = userService.createUser(keycloakId, req);

        // Логиним сразу и возвращаем токены
        Map<String, Object> tokens = keycloakApiService.login(req.getUsername(), req.getPassword());

        return ResponseEntity.ok(tokens).getBody();
    }

    public Map<String, Object> login(LoginRequest req) {
        String username = req.getUsername();
        String password = req.getPassword();

        if (loginAttemptService.isBlocked(username)) {
            throw new InvalidCredentialsException("Account locked. Try again later.");
        }

        try {
            Map<String, Object> tokens = keycloakApiService.login(username, password);
            loginAttemptService.loginSucceeded(username);
            return tokens;
        } catch (Exception e) {
            loginAttemptService.loginFailed(username);
            throw new InvalidCredentialsException("Invalid username or password.");
        }
    }
}
