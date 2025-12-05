package Application.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "temp_users")
public class TempUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    private boolean profileCompleted = false;
}


