package com.focusmonitor.backend.services;

import com.focusmonitor.backend.dto.AuthResponse;
import com.focusmonitor.backend.dto.LoginRequest;
import com.focusmonitor.backend.dto.RegisterRequest;
import com.focusmonitor.backend.model.User;
import com.focusmonitor.backend.repository.UserRepository;
import com.focusmonitor.backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_success(){
        String email = "test@email.com";
        User fakeUser = new User();
        fakeUser.setId(UUID.randomUUID());
        fakeUser.setEmail(email);
        fakeUser.setPasswordHash("hashed123");

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword("plainPassword");

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(fakeUser));
        when(passwordEncoder.matches("plainPassword", "hashed123"))
                .thenReturn(Boolean.TRUE);
        when(jwtUtil.generateToken(email))
                .thenReturn("fake-jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("test@email.com", response.getEmail());


    }
    @Test
    void login_fails_whenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("nobody@email.com");
        request.setPassword("anything");

        when(userRepository.findByEmail("nobody@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    void login_fails_whenPasswordIsWrong() {
        User fakeUser = new User();
        fakeUser.setEmail("test@email.com");
        fakeUser.setPasswordHash("hashed123");

        LoginRequest request = new LoginRequest();
        request.setEmail("test@email.com");
        request.setPassword("wrongPassword");

        when(userRepository.findByEmail("test@email.com"))
                .thenReturn(Optional.of(fakeUser));
        when(passwordEncoder.matches("wrongPassword", "hashed123"))
                .thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });
    }
    @Test
    void register_success(){
        String mail = "email12@email.com";
        String username = "username123";
        String password = "password123@";

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setEmail(mail);
        savedUser.setUsername(username);
        savedUser.setPasswordHash("encodedPassword");


        RegisterRequest request = new RegisterRequest();
        request.setEmail(mail);
        request.setUsername(username);
        request.setPassword(password);

        when(userRepository.existsByEmail(mail))
                .thenReturn(false);

        when(userRepository.existsByUsername(username))
                .thenReturn(false);

        when(passwordEncoder.encode(password))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);
        when(jwtUtil.generateToken(mail))
                .thenReturn("fake-jwt-token");
        AuthResponse response = authService.register(request);

        assertEquals("fake-jwt-token", response.getToken());
        assertEquals(mail, response.getEmail());
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals(mail) &&
                        user.getUsername().equals(username) &&
                        user.getPasswordHash().equals("encodedPassword")
        ));
    }
    @Test
    void register_fails_whenUserAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("email@email.com");
        request.setUsername("username123");

        when(userRepository.existsByEmail("email@email.com"))
                .thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            authService.register(request);
        });

        assertEquals("Email already exists", ex.getMessage());
    }
    @Test
    void register_fails_whenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("email@email.com");
        request.setUsername("username123");

        when(userRepository.existsByEmail("email@email.com"))
                .thenReturn(false);

        when(userRepository.existsByUsername("username123"))
                .thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            authService.register(request);
        });
    }

}
