package Application.controller;

import Application.dto.CreateUserRequest;
import Application.dto.LoginRequest;
import Application.dto.OtpVerifyRequest;
import Application.service.AuthService;
import Application.service.KeycloakApiService;
import Application.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OtpService otpService;
    private final KeycloakApiService keycloakApiService;
    private final AuthService authService;

    // Step 1: Send OTP
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody OtpVerifyRequest request) {
        otpService.generateOtp(request.getPhoneNumber());
        log.info("OTP sent to phone: {}", request.getPhoneNumber());
        return ResponseEntity.ok("OTP sent successfully");
    }

    // Step 2: Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerifyRequest  request) {
        otpService.verifyOtp(request.getPhoneNumber(), request.getOtp());
        log.info("OTP verified for phone: {}", request.getPhoneNumber());
        return ResponseEntity.ok("OTP verified successfully");
    }

    // Step 3: Register profile (phone already verified)
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<Map<String, Object>> register(@RequestBody @Valid CreateUserRequest req) {
        try {
            log.info("Registration attempt for username: {}", req.getUsername());

            Map<String, Object> result = authService.register(req);

            log.info("User registered successfully: {}", req.getUsername());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.warn("Registration failed for username {}: {}", req.getUsername(), e.getMessage());
            throw e;
        }
    }

    // Step 4: Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
            log.info("Login attempt for user: {}", request.getUsername());
            Map<String, Object> tokens = keycloakApiService.login(request.getUsername(), request.getPassword());
            log.info("Login successful for user: {}", request.getUsername());
            return ResponseEntity.ok(tokens);

    }
}
