package Application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CompleteProfileRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @Size(min = 3, max = 255,message = "the firstName must be between 3 and 255")
    private String firstName;

    @NotBlank
    @Size(min = 3, max = 255,message = "the lastName must be between 3 and 255")
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @Pattern(
            regexp = "^[A-Z]{2}[0-9]{7}$",
            message = "Passport must be in format AA1234567"
    )
    private String passport;

    private LocalDate birthDate;
}
