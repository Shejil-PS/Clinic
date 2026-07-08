package com.clinic.management.config;

import com.clinic.management.entity.User;
import com.clinic.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PasswordEncoderInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        List<User> users = userRepository.findAll();
        boolean updated = false;
        
        for (User user : users) {
            // Check if password is not already BCrypt encoded
            // BCrypt hashes start with $2a$, $2b$, or $2y$ and are 60 chars long
            if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
                updated = true;
            }
        }
        
        if (updated) {
            System.out.println("Successfully migrated plain text passwords to BCrypt.");
        }
    }
}
