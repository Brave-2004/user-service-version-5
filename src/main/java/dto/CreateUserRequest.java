package dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    @Email(message = "Invalid email format")
    @NotBlank
    private String email;

    @Pattern(
            regexp = "^\\+998([0-9]{9})$",
            message = "Phone number must be in format +998XXXXXXXXX"
    )
    private String phoneNumber;

    @NotBlank
    @Size(min = 3, max = 255)
    private String firstName;

    @NotBlank
    @Size(min = 3, max = 255)
    private String lastName;

    @Pattern(
            regexp = "^[A-Z]{2}[0-9]{7}$",
            message = "Passport must be in format AA1234567"
    )
    private String passport;

    private LocalDate birthDate; // <-- добавлено
}
