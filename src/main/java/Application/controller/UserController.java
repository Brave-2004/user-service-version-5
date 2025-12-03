package Application.controller;

import Application.dto.CreateUserRequest;
import Application.dto.UpdateUserRequest;
import Application.dto.UserDto;
import Application.mapper.UserMapper;
import Application.model.User;
import Application.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j  // <--- Lombok adds a 'log' field
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/me")
    public UserDto getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        log.info("Fetching current user with Keycloak ID: {}", keycloakId);
        User user = userService.getUserByKeycloakId(keycloakId);
        log.debug("User fetched: {}", user);
        return UserMapper.toDto(user);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> getAllUsers() {
        log.info("Admin requested all users");
        List<UserDto> users = userService.getAllUsers().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
        log.debug("Number of users fetched: {}", users.size());
        return users;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public UserDto createUser(@AuthenticationPrincipal Jwt jwt,
                              @RequestBody CreateUserRequest req) {
        String keycloakId = jwt.getSubject();
        log.info("Creating user for Keycloak ID: {} with username: {}", keycloakId, req.getUsername());
        User user = userService.createUser(keycloakId, req);
        log.debug("User created: {}", user);
        return UserMapper.toDto(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal.subject")
    public UserDto updateUser(@PathVariable UUID id, @RequestBody @Valid UpdateUserRequest req) {
        log.info("Updating user with ID: {}", id);
        User user = userService.updateUser(id, req);
        log.debug("User updated: {}", user);
        return UserMapper.toDto(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable UUID id) {
        log.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        log.debug("User with ID {} deleted successfully", id);
    }

    @GetMapping("/test")
    @PreAuthorize("hasRole('USER')")
    public void Test() {
        log.info("Test endpoint accessed");
        System.out.println("It is working");
    }

}
