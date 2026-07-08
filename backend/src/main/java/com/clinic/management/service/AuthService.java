package com.clinic.management.service;

import com.clinic.management.dto.AuthRequest;
import com.clinic.management.dto.AuthResponse;
import com.clinic.management.dto.RegisterRequest;
import com.clinic.management.entity.User;
import com.clinic.management.exception.DuplicateResourceException;
import com.clinic.management.repository.UserRepository;
import com.clinic.management.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        var user = User.builder()
                .userId(UUID.randomUUID().toString())
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .active(true)
                .build();

        userRepository.save(user);
        var jwtToken = jwtUtil.generateToken(user);
        
        logger.info("AUDIT LOG: User registered successfully. Username: {}", user.getUsername());

        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(); // We know it exists if authentication passes
                
        var jwtToken = jwtUtil.generateToken(user);
        
        logger.info("AUDIT LOG: User login successful. Username: {}", user.getUsername());

        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .build();
    }
}
