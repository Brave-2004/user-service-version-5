package Application.model;

import jakarta.persistence.*;
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
@Entity
@Table(name = "users")
public class User extends UuidMappedClass {

    @Column(unique = true)
    private String username;

    private String password;

    private String keycloakId;

    private String email;

    private String phoneNumber;

    private String firstName;

    private String lastName;

    private String passport;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private LocalDate birthDate;
}
