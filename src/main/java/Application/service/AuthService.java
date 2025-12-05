package Application.service;

import Application.dto.CompleteProfileRequest;
import Application.dto.LoginRequest;

import java.util.Map;

public interface AuthService {
    Map<String, Object> issueTemporaryToken(String token);
    Map<String, Object> completeProfileAndLogin(String tempToken, CompleteProfileRequest req);
    Map<String, Object> login(LoginRequest req);
}
