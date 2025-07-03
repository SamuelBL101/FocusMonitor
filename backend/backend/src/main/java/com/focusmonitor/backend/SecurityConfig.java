package com.focusmonitor.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig  {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)     // vypnúť CSRF ochranu (bez nej REST API beží ľahšie)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // povoliť prístup bez prihlasovania na tieto endpointy
                        .anyRequest().permitAll()
                )
                .httpBasic(AbstractHttpConfigurer::disable)   // prípadne môžeš použiť formLogin() alebo httpBasic()
        .formLogin(AbstractHttpConfigurer::disable); // povoliť form login, ak je potrebné
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

