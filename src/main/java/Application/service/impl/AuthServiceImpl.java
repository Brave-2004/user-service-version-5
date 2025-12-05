package Application.service.impl;

import Application.dto.CompleteProfileRequest;
import Application.dto.LoginRequest;
import Application.exception.InvalidTemporaryTokenException;
import Application.model.TempUser;
import Application.model.User;
import Application.repository.TempUserRepository;
import Application.repository.UserRepository;
import Application.service.AuthService;
import Application.service.KeycloakService;
import Application.service.TemporaryTokenService;
import Application.service.UserService;
import Application.unum.StatusEnum;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final KeycloakService keycloakService;
    private final UserService userService;
    private final TemporaryTokenService tempTokenService; // generates JWT for temp users
    private final UserRepository userRepository;
    private final TempUserRepository tempUserRepository;

    @Override
    // Step 1: Issue temporary token after OTP verification
    public Map<String, Object> issueTemporaryToken(String phoneNumber) {
        // Check if temp user exists in DB, create if not
        TempUser userTemp = userService.getOrCreateTempUser(phoneNumber);

        // Generate temporary JWT token valid for profile completion
        return tempTokenService.generateToken(userTemp.getId());
    }

    @Override
    // Step 2: Complete profile
    @Transactional
    public Map<String, Object> completeProfileAndLogin(String tempToken, CompleteProfileRequest req) {

        // 1️⃣ Validate temporary token and get phone number from it
        String userId;
        try {
            userId = tempTokenService.validateToken(tempToken);
        } catch (RuntimeException e) {
            throw new InvalidTemporaryTokenException("Temporary token is not valid or has expired");
        }
        TempUser tempUser = tempUserRepository.findById(userId).orElseThrow(() -> new InvalidTemporaryTokenException("User not found"));

        // 2️⃣ Create new Keycloak user
        keycloakService.createUser(
                req.getUsername(),
                req.getPassword(),
                req.getEmail(),
                req.getFirstName(),
                req.getLastName(),
                req.getPassport(),
                req.getBirthDate()
        );

        // 3️⃣ Fetch Keycloak-generated user ID
        String adminToken = keycloakService.getAdminAccessToken();
        String keycloakUserId = keycloakService.fetchKeycloakUserIdByUsername(req.getUsername(), adminToken);

        // 4️⃣ Assign default USER role
        keycloakService.assignUserRole(keycloakUserId);

        // 5️⃣ Save local User entity with phone number and Keycloak ID
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(req.getPassword());
        user.setKeycloakId(keycloakUserId);
        user.setEmail(req.getEmail());
        user.setPhoneNumber(tempUser.getPhoneNumber());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setPassport(req.getPassport());
        user.setStatus(StatusEnum.VERIFIED);
        user.setBirthDate(req.getBirthDate());

        userRepository.save(user);
        // so then just tempUser update field completeProfile
        tempUser.setProfileCompleted(true);

        // 6️⃣ Immediately login the new user to get tokens
        return keycloakService.login(req.getUsername(), req.getPassword());
    }

    @Override
    // Normal login
    public Map<String, Object> login(LoginRequest req) {
        return keycloakService.login(req.getUsername(), req.getPassword());
    }

}
