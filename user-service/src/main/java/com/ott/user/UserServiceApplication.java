package com.ott.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



import com.ott.user.model.User;
import com.ott.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
	@Bean
    public CommandLineRunner initializeUsersFromFile(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Only seed if the database table is currently empty
            if (userRepository.count() > 0) {
                System.out.println(">>>> Database already contains user data. Skipping file initialization. <<<<");
                return;
            }

            String fileName = "users.txt";
            ClassPathResource resource = new ClassPathResource(fileName);

            System.out.println(">>>> Reading " + fileName + " to initialize user records... <<<<");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                
                String line;
                int count = 0;
                
                while ((line = reader.readLine()) != null) {
                    // Skip empty lines or comment lines if any
                    if (line.trim().isEmpty() || line.startsWith("#")) {
                        continue;
                    }

                    // Split tokens by comma
                    String[] tokens = line.split(",");
                    if (tokens.length < 6) {
                        System.err.println("Skipping invalid line (insufficient fields): " + line);
                        continue;
                    }

                    // Extract fields based on order: username, email, password, full_name, phone, role
                    String username  = tokens[0].trim();
                    String email     = tokens[1].trim();
                    String rawPwd    = tokens[2].trim();
                    String fullName  = tokens[3].trim();
                    String phone     = tokens[4].trim();
                    String roleStr   = tokens[5].trim().toUpperCase();

                    // Prevent duplicate entries during file processing loop
                    if (userRepository.findByUsername(username).isPresent()) {
                        continue;
                    }

                    // Map values to Entity
                    User user = new User();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setFullName(fullName);
                    user.setPhone(phone);
                    user.setIsActive(true);
                    
                    // Securely encode the password using the native application bean
                    user.setPassword(passwordEncoder.encode(rawPwd));
                    
                    // Map String role to your Enum type
                    user.setRole(User.Role.valueOf(roleStr));

                    // Persist to MySQL
                    userRepository.save(user);
                    count++;
                }

                System.out.println(">>>> Successfully initialized " + count + " users from file! <<<<");

            } catch (Exception e) {
                System.err.println("Error reading initialization file: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
