package com.focusmonitor.backend.Controller;

import com.focusmonitor.backend.JwtUtil;
import com.focusmonitor.backend.model.User;
import com.focusmonitor.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(user.getUsername()));
        if (userOptional.isPresent()) {
            return ResponseEntity.badRequest().body("Pouzivatel uz existuje");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Pouzivatel bol uspesne vytvoreny");
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser == null || !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.status(401).body("Nespravne prihlasovacie udaje");
        }
        String token = jwtUtil.generateToken(existingUser.getUsername());
        System.out.println("Vygenerovaný token: " + token);
        return ResponseEntity.ok(Map.of(
                "message", "Prihlásenie úspešné",
                "token", token
        ));    }
    @GetMapping("/getLogins")
    public ResponseEntity<Iterable<User>> getLogins() {
        Iterable<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

}
