package Application.service;

import Application.dto.UpdateUserProfile;
import Application.model.TempUser;
import Application.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(UUID id);

    User getUserByKeycloakId(String keycloakId);


    User updateUser(UUID id, UpdateUserProfile req);

    void deleteUser(UUID id);

    TempUser getOrCreateTempUser(String phoneNumber);

    User updateProfile(String keycloakId,UpdateUserProfile req);

}
