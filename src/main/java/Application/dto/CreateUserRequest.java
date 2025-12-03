package Application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
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

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    @Column(unique = true)
    private String password;

    @Email(message = "Invalid email format")
    @NotBlank
    @Column(unique = true)
    private String email;

    @Pattern(
            regexp = "^\\+998([0-9]{9})$",
            message = "Phone number must be in format +998XXXXXXXXX"
    )
    @Column(unique = true)
    private String phoneNumber;

    @NotBlank
    @Size(min = 3, max = 255,message = "the length of firstName should be between 3 and 255")
    private String firstName;

    @NotBlank
    @Size(min = 3, max = 255,message = "the length of lastName should be between 3 and 255")
    private String lastName;

    @Pattern(
            regexp = "^[A-Z]{2}[0-9]{7}$",
            message = "Passport must be in format AA1234567"
    )
    @Column(unique = true)
    private String passport;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate; // <-- добавлено
}
