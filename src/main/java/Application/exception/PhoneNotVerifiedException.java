package Application.exception;

public class PhoneNotVerifiedException extends RuntimeException {
    public PhoneNotVerifiedException(String phone) {
        super("Phone number not verified via OTP: " + phone);
    }
}
