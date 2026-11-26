package com.tindahanko.config;

import com.tindahanko.entity.Role;
import com.tindahanko.entity.User;
import com.tindahanko.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
        Optional<User> adminOpt = userRepository.findByUsername("admin");
        if (adminOpt.isEmpty()) {
            User admin = new User(
                    "admin",
                    passwordEncoder.encode("Password123"),
                    "John Lloyd Lourence Rabanal",
                    "09914731143",
                    "akosirabanal@gmail.com",
                    Role.STORE_OWNER
            );
            userRepository.save(admin);
            logger.info("Initialized default Store Owner: admin / Password123 (Mobile: 09914731143, Email: akosirabanal@gmail.com)");
        } else {
            User admin = adminOpt.get();
            if (admin.getEmail() == null || admin.getEmail().isEmpty()) {
                admin.setEmail("akosirabanal@gmail.com");
                userRepository.save(admin);
            }
        }

        // Ensure default Customer Ana exists
        Optional<User> anaOpt = userRepository.findByUsername("customer_ana");
        if (anaOpt.isEmpty()) {
            User ana = new User(
                    "customer_ana",
                    passwordEncoder.encode("CustPassword123"),
                    "Ana Reyes Santos",
                    "09205556677",
                    "ana.reyes@gmail.com",
                    Role.CUSTOMER
            );
            userRepository.save(ana);
            logger.info("Initialized default Customer: customer_ana / CustPassword123 (Mobile: 09205556677, Email: ana.reyes@gmail.com)");
        } else {
            User ana = anaOpt.get();
            if (ana.getEmail() == null || ana.getEmail().isEmpty()) {
                ana.setEmail("ana.reyes@gmail.com");
                userRepository.save(ana);
            }
        }

        // Ensure test Customer Juan exists
        Optional<User> juanOpt = userRepository.findByUsername("customer_juan");
        if (juanOpt.isEmpty()) {
            User juan = new User(
                    "customer_juan",
                    passwordEncoder.encode("JuanPassword123"),
                    "Juan Dela Cruz",
                    "09170000000",
                    "juan.delacruz@gmail.com",
                    Role.CUSTOMER
            );
            userRepository.save(juan);
            logger.info("Initialized test Customer: customer_juan / JuanPassword123 (Mobile: 09170000000, Email: juan.delacruz@gmail.com)");
        } else {
            User juan = juanOpt.get();
            if (juan.getEmail() == null || juan.getEmail().isEmpty()) {
                juan.setEmail("juan.delacruz@gmail.com");
                userRepository.save(juan);
            }
        }
    }
}
