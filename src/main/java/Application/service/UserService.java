package Application.service;

import Application.dto.CreateUserRequest;
import Application.dto.UpdateUserRequest;
import Application.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(UUID id);

    User getUserByKeycloakId(String keycloakId);

    User createUser(String keycloakId, CreateUserRequest req);

    User updateUser(UUID id, UpdateUserRequest req);

    void deleteUser(UUID id);
}
