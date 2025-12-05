package Application.service.impl;

import Application.dto.UpdateUserProfile;
import Application.mapper.UserMapper;
import Application.model.TempUser;
import Application.model.User;
import Application.repository.TempUserRepository;
import Application.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Application.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TempUserRepository tempUserRepository;

    public UserServiceImpl(UserRepository userRepository, TempUserRepository tempUserRepository) {
        this.userRepository = userRepository;
        this.tempUserRepository = tempUserRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public User getUserByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    @Transactional
    public User updateUser(UUID id, UpdateUserProfile req) {
        User user = getUserById(id);
        UserMapper.updateEntity(user, req);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public TempUser getOrCreateTempUser(String phoneNumber) {
        return tempUserRepository.findByPhoneNumber(phoneNumber).orElseGet(() -> {
            TempUser user = new TempUser();
            user.setPhoneNumber(phoneNumber);
            return tempUserRepository.save(user);
        });
    }

    @Override
    @Transactional
    public User updateProfile(String keycloakId, UpdateUserProfile req) {
        User user = userRepository.findByKeycloakId(keycloakId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserMapper.updateEntity(user, req);
        return userRepository.save(user);
    }

}
