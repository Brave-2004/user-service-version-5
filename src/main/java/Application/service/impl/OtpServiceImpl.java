package Application.service.impl;

import Application.model.Otp;
import Application.repository.OtpRepository;
import Application.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;

    // Step 1: Generate OTP
    @Override
    public void generateOtp(String phoneNumber) {
        String code = String.format("%04d", new Random().nextInt(10000)); // 4-digit OTP

        Otp otp = new Otp();
        otp.setPhoneNumber(phoneNumber);
        otp.setCode(code);
        otp.setExpiry(LocalDateTime.now().plusMinutes(2)); // expires in 5 minutes
        otp.setVerified(false);

        otpRepository.save(otp);

        // TODO: integrate SMS provider here
        System.out.println("OTP for " + phoneNumber + " is: " + code);
    }

    // Step 2: Verify OTP
    @Override
    public void verifyOtp(String phoneNumber, String code) {
        Otp otp = otpRepository.findByPhoneNumberAndVerifiedFalse(phoneNumber)
                .orElseThrow(() -> new RuntimeException("No pending OTP found for this phone"));

        if (otp.getExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!otp.getCode().equals(code)) {
            throw new RuntimeException("Invalid OTP");
        }

        otp.setVerified(true);
        otpRepository.save(otp);
    }

    @Override
    // Step 3: Check if phone is verified (for registration)
    public boolean isPhoneVerified(String phoneNumber) {
        return otpRepository.findByPhoneNumberAndVerifiedFalse(phoneNumber).isEmpty();
    }
}
