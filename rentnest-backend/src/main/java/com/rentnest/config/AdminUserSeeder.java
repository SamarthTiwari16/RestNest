package com.rentnest.config;

import com.rentnest.entity.Role;
import com.rentnest.entity.User;
import com.rentnest.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String adminEmail = "admin@rentnest.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User(
                    "System Admin",
                    adminEmail,
                    "+910000000000",
                    passwordEncoder.encode("Admin!Pass1"),
                    Role.ROLE_ADMIN
            );
            userRepository.save(admin);
            log.info("Seeded default admin user: email={}, password=Admin!Pass1", adminEmail);
        }
    }
}
