package Application.service;

import Application.dto.CreateUserRequest;
import Application.dto.UpdateUserRequest;
import Application.mapper.UserMapper;
import Application.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Application.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) { this.userRepository = userRepository; }

    @Override
    public List<User> getAllUsers() { return userRepository.findAll(); }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public User getUserByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    @Transactional
    public User createUser(String keycloakId, CreateUserRequest req) {
        User user = UserMapper.fromCreateRequest(keycloakId, req);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(UUID id, UpdateUserRequest req) {
        User user = getUserById(id);
        UserMapper.updateEntity(user, req);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) { userRepository.deleteById(id); }
}
