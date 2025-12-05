package Application.repository;

import Application.model.TempUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TempUserRepository extends JpaRepository<TempUser, String> {
    Optional<TempUser> findByPhoneNumber(String phoneNumber);
}
