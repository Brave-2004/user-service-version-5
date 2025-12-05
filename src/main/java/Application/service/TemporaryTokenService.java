package Application.service;

import java.util.Map;

public interface TemporaryTokenService {
    Map<String, Object> generateToken(String userId);
    String validateToken(String token);
}
