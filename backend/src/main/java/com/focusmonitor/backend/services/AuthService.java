package com.focusmonitor.backend.services;

import com.focusmonitor.backend.security.JwtUtil;
import com.focusmonitor.backend.dto.AuthResponse;
import com.focusmonitor.backend.dto.LoginRequest;
import com.focusmonitor.backend.dto.RegisterRequest;
import com.focusmonitor.backend.model.User;
import com.focusmonitor.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor

public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setTimezone("Europe/Bratislava");
        user.setCreatedAt(Instant.now());
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId().toString(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Používateľ neexistuje"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Zlé heslo");

        }
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId().toString(),user.getEmail());
    }
}
