package Application.controller;

import Application.dto.CreateUserRequest;
import Application.dto.UpdateUserRequest;
import Application.dto.UserDto;
import Application.mapper.UserMapper;
import Application.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import Application.service.UserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserDto getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        User user = userService.getUserByKeycloakId(keycloakId);
        return UserMapper.toDto(user);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('admin')")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public UserDto createUser(@AuthenticationPrincipal Jwt jwt,
                              @RequestBody CreateUserRequest req) {
        String keycloakId = jwt.getSubject();
        User user = userService.createUser(keycloakId, req);
        return UserMapper.toDto(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin') or #id.toString() == authentication.principal.subject")
    public UserDto updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequest req) {
        User user = userService.updateUser(id, req);
        return UserMapper.toDto(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }

    @GetMapping("/test")
    @PreAuthorize("hasRole('admin')")
    public void Test() {
        System.out.println("It is working");
    }

}
