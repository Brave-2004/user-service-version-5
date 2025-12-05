package Application.controller;

import Application.dto.UserDto;
import Application.mapper.UserMapper;
import Application.model.User;
import Application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/admins")
@Tag(name = "AdminController",description = "getting info about users and delete if it required")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get current user info",
            description = "Fetches info of the currently logged-in user",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable UUID id) {
        log.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        log.debug("User with ID {} deleted successfully", id);
    }
}
