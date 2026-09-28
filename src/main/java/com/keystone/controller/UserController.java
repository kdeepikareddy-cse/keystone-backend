 
package com.keystone.keystone.controller;

import com.keystone.keystone.dto.UserResponse;
import com.keystone.keystone.model.User;
import com.keystone.keystone.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
private final BCryptPasswordEncoder passwordEncoder;

public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = new BCryptPasswordEncoder();
}
    // Register new user
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {

        // New users are CUSTOMER by default
        if (user.getRole() == null ||
                user.getRole().isBlank() ||
                user.getRole().equalsIgnoreCase("USER")) {

            user.setRole("CUSTOMER");
        }

   

user.setPassword(
        passwordEncoder.encode(user.getPassword())
);

return ResponseEntity.ok(
        userRepository.save(user)
); }

    // Get logged-in user's profile
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }

    // Get all users
    // Admin access will be enforced in SecurityConfig
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()
                ))
                .toList();

        return ResponseEntity.ok(users);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser) {

        User user = userRepository.findById(id)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        if (updatedUser.getPassword() != null &&
        !updatedUser.getPassword().isBlank()) {

    user.setPassword(
            passwordEncoder.encode(updatedUser.getPassword())
    );
}

        // Allow Admin to change roles later
        if (updatedUser.getRole() != null &&
                !updatedUser.getRole().isBlank()) {

            user.setRole(
                    updatedUser.getRole().toUpperCase()
            );
        }

        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );

        return ResponseEntity.ok(response);
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userRepository.delete(user);

        return ResponseEntity.ok(
                "User deleted successfully"
        );
    }
}

