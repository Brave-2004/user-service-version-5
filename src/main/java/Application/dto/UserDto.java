package Application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import Application.unum.StatusEnum;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private UUID id;
    private String keycloakId;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String passport;
    private LocalDate birthDate;  // <-- добавлено
    private StatusEnum status;
    private Instant createdAt;
    private Instant updatedAt;
}
