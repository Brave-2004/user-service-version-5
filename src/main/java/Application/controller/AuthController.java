package Application.controller;

import Application.dto.*;
import Application.service.AuthService;
import Application.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OtpService otpService;
    private final AuthService authService;

    // STEP 1 — Send OTP
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody OtpVerifyRequest request) {
        otpService.generateOtp(request.getPhoneNumber());
        log.info("OTP sent to phone: {}", request.getPhoneNumber());
        return ResponseEntity.ok("OTP sent successfully");
    }

    // STEP 2 — Verify OTP and issue temporary token
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody OtpVerifyRequest request) {
        otpService.verifyOtp(request.getPhoneNumber(), request.getOtp());
        log.info("OTP verified for phone: {}", request.getPhoneNumber());

        // Generate temporary JWT linked to temp user
        Map<String, Object> tempToken = authService.issueTemporaryToken(request.getPhoneNumber());

        return ResponseEntity.ok(tempToken);
    }

    // STEP 3 — Complete profile using temporary token
    @PostMapping("/complete-profile")
    public ResponseEntity<Map<String, Object>> completeProfile(
            @RequestHeader(name = "Authorization") String authHeader,
            @RequestBody @Valid CompleteProfileRequest req
    ) {
        // Remove "Bearer " prefix if present
        String tempToken;
        if (authHeader.startsWith("Bearer ")) {
            tempToken = authHeader.substring(7).trim();
        } else {
            tempToken = authHeader.trim();
        }
        Map<String, Object> tokens = authService.completeProfileAndLogin(tempToken, req);
        return ResponseEntity.ok(tokens);
    }


    // STEP 4 — Normal login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        log.info("Login attempt for: {}", request.getUsername());
        Map<String, Object> tokens = authService.login(request);
        log.info("Login successful for: {}", request.getUsername());
        return ResponseEntity.ok(tokens);
    }
}
