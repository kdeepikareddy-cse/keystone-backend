package com.keystone.keystone.controller;

import com.keystone.keystone.config.JwtService;
import com.keystone.keystone.dto.LoginRequest;
import com.keystone.keystone.dto.LoginResponse;
import com.keystone.keystone.model.User;
import com.keystone.keystone.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return new LoginResponse(
                    "Invalid email or password",
                    null,
                    null,
                    null
            );
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return new LoginResponse(
                    "Invalid email or password",
                    null,
                    null,
                    null
            );
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );

        return new LoginResponse(
                "Login successful",
                token,
                user.getEmail(),
                user.getRole()
        );
    }
}

