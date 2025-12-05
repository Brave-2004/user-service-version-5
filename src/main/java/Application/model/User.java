package Application.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import Application.model.template.UuidMappedClass;
import Application.unum.StatusEnum;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "users")
public class User extends UuidMappedClass {

    // ID пользователя из Keycloak
    @NotBlank
    private String keycloakId;

    @Email(message = "Invalid email format")
    @NotBlank
    private String email;

    @Pattern(regexp = "^\\+998([0-9]{9})$",
            message = "Phone number must be in format +998XXXXXXXXX")
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

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private LocalDate birthDate;  // <-- добавлено
}
