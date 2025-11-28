package Application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakAuthService {

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    // 🔹 Создание пользователя в Keycloak через Admin API
    public String createKeycloakUser(String username, String password, String email, String firstName, String lastName) {
        // Получаем admin токен для Keycloak
        String adminToken = getAdminAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);


        Map<String, Object> body = Map.of(
                "username", username,
                "email", email,
                "firstName", firstName,
                "lastName", lastName,
                "emailVerified", true,
                "enabled", true,
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", password,
                        "temporary", false
                ))
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(keycloakUrl + "/admin/realms/" + realm + "/users", request, Void.class);

        // Получаем ID пользователя из Keycloak
        return fetchKeycloakUserIdByUsername(username, adminToken);
    }

    @SuppressWarnings("unchecked")
    // 🔹 Вход пользователя (получение токенов)
    public Map<String, Object> login(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", username);
        body.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                request,
                Map.class
        );

        return response.getBody();
    }

    @SuppressWarnings("unchecked")
    // 🔹 Получение админ токена Keycloak
    private String getAdminAccessToken() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", "admin-cli"); // стандартный клиент Keycloak
        body.add("username", "admin");      // admin login
        body.add("password", "admin"); // admin password

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        Map<String, Object> tokenResponse = restTemplate.postForObject(
                keycloakUrl + "/realms/master/protocol/openid-connect/token",
                request,
                Map.class
        );
        return tokenResponse.get("access_token").toString();
    }

    // 🔹 Получение ID пользователя из Keycloak
    private String fetchKeycloakUserIdByUsername(String username, String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map[]> response = restTemplate.exchange(
                keycloakUrl + "/admin/realms/" + realm + "/users?username=" + username,
                org.springframework.http.HttpMethod.GET,
                request,
                Map[].class
        );

        Map user = response.getBody()[0];
        return user.get("id").toString();
    }


    @SuppressWarnings("unchecked")
    public void assignUserRole(String userId) {
        String adminToken = getAdminAccessToken();
        String clientUuid = getClientUuid(clientId, adminToken);

        // Get the role representation for "user" role in your client
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map[]> rolesResponse = restTemplate.exchange(
                keycloakUrl + "/admin/realms/" + realm + "/clients/" + clientUuid + "/roles",
                org.springframework.http.HttpMethod.GET,
                request,
                Map[].class
        );

        Map<String, Object> userRole = Arrays.stream(rolesResponse.getBody())
                .filter(r -> r.get("name").equals("user"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User role not found"));

        // Assign role to user
        HttpEntity<List<Map<String, Object>>> assignRequest = new HttpEntity<>(List.of(userRole), headers);
        restTemplate.postForEntity(
                keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/clients/" + clientUuid,
                assignRequest,
                Void.class
        );
    }


    private String getClientUuid(String clientId, String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map[]> response = restTemplate.exchange(
                keycloakUrl + "/admin/realms/" + realm + "/clients?clientId=" + clientId,
                org.springframework.http.HttpMethod.GET,
                request,
                Map[].class
        );

        return response.getBody()[0].get("id").toString();
    }


}
