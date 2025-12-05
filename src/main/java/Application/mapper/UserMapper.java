package Application.mapper;

import Application.dto.UpdateUserProfile;
import Application.dto.UserDto;
import Application.model.User;

import java.time.Instant;

public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setPassport(user.getPassport());
        dto.setBirthDate(user.getBirthDate());  // <-- добавлено
        return dto;
    }

    public static void updateEntity(User user, UpdateUserProfile req) {
        if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
        if (req.getLastName() != null) user.setLastName(req.getLastName());
        if (req.getPhoneNumber() != null) user.setPhoneNumber(req.getPhoneNumber());
        if (req.getPassport() != null) user.setPassport(req.getPassport());
        if (req.getBirthDate() != null) user.setBirthDate(req.getBirthDate()); // <-- добавлено
        user.setUpdatedAt(Instant.now());
    }
}
