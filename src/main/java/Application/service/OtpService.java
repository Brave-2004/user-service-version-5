package Application.service;

public interface OtpService {
    void generateOtp(String phoneNumber);
    void verifyOtp(String phoneNumber, String code);
    boolean isPhoneVerified(String phoneNumber);
}
