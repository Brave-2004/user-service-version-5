package Application.service;

import Application.dto.CompleteProfileRequest;

import java.time.LocalDate;
import java.util.Map;

public interface KeycloakService {
    String createUserAndGetId(CompleteProfileRequest req);
    String createKeycloakUser(String username, String password, String email, String firstName, String lastName);
    Map<String, Object> login(String username, String password);
    String getAdminAccessToken();
    String fetchKeycloakUserIdByUsername(String username, String adminToken);
    void assignUserRole(String userId);
    String getClientUuid(String clientId, String adminToken);
    void createUser(String username, String password,
                    String email, String firstName, String lastName,
                    String passport,  LocalDate birthDate);
}
