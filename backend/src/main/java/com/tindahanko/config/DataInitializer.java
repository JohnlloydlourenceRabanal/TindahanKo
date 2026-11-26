package com.tindahanko.config;

import com.tindahanko.entity.Role;
import com.tindahanko.entity.User;
import com.tindahanko.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Ensure default Store Owner exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User(
                    "admin",
                    passwordEncoder.encode("Password123"),
                    "Store Owner Admin",
                    "09179998888",
                    Role.STORE_OWNER
            );
            userRepository.save(admin);
            logger.info("Initialized default Store Owner: admin / Password123 (Mobile: 09179998888)");
        }

        // Ensure default Customer Ana exists
        if (userRepository.findByUsername("customer_ana").isEmpty()) {
            User ana = new User(
                    "customer_ana",
                    passwordEncoder.encode("CustPassword123"),
                    "Ana Reyes Santos",
                    "09205556677",
                    Role.CUSTOMER
            );
            userRepository.save(ana);
            logger.info("Initialized default Customer: customer_ana / CustPassword123 (Mobile: 09205556677)");
        }

        // Ensure test Customer Juan exists
        if (userRepository.findByUsername("customer_juan").isEmpty()) {
            User juan = new User(
                    "customer_juan",
                    passwordEncoder.encode("JuanPassword123"),
                    "Juan Dela Cruz",
                    "09170000000",
                    Role.CUSTOMER
            );
            userRepository.save(juan);
            logger.info("Initialized test Customer: customer_juan / JuanPassword123 (Mobile: 09170000000)");
        }
    }
}
