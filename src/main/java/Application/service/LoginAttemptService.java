package Application.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginAttemptService {

    private final Map<String, LoginAttempt> attempts = new HashMap<>();
    private final int MAX_ATTEMPTS = 3;
    private final int LOCK_MINUTES = 3;

    public void loginSucceeded(String username) {
        attempts.remove(username);
    }

    public void loginFailed(String username) {
        LoginAttempt attempt = attempts.getOrDefault(username, new LoginAttempt());
        attempt.increment();
        attempts.put(username, attempt);
    }

    public boolean isBlocked(String username) {
        LoginAttempt attempt = attempts.get(username);
        if (attempt == null) return false;

        if (attempt.count >= MAX_ATTEMPTS) {
            // Check if lock time passed
            if (LocalDateTime.now().isBefore(attempt.lockUntil)) {
                return true;
            } else {
                attempts.remove(username); // unlock
                return false;
            }
        }
        return false;
    }

    private class LoginAttempt {
        int count = 0;
        LocalDateTime lockUntil = LocalDateTime.now();

        void increment() {
            count++;
            if (count >= MAX_ATTEMPTS) {
                lockUntil = LocalDateTime.now().plusMinutes(LOCK_MINUTES);
            }
        }
    }
}
