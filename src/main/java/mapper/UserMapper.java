package mapper;

import dto.CreateUserRequest;
import dto.UpdateUserRequest;
import dto.UserDto;
import model.User;
import unum.StatusEnum;

import java.time.Instant;

public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setKeycloakId(user.getKeycloakId());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPassport(user.getPassport());
        dto.setBirthDate(user.getBirthDate());  // <-- добавлено
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    public static User fromCreateRequest(String keycloakId, CreateUserRequest req) {
        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setEmail(req.getEmail());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setPassport(req.getPassport());
        user.setBirthDate(req.getBirthDate()); // <-- добавлено
        user.setStatus(StatusEnum.VERIFIED);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return user;
    }

    public static void updateEntity(User user, UpdateUserRequest req) {
        if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
        if (req.getLastName() != null) user.setLastName(req.getLastName());
        if (req.getPhoneNumber() != null) user.setPhoneNumber(req.getPhoneNumber());
        if (req.getPassport() != null) user.setPassport(req.getPassport());
        if (req.getBirthDate() != null) user.setBirthDate(req.getBirthDate()); // <-- добавлено
        user.setUpdatedAt(Instant.now());
    }
}
