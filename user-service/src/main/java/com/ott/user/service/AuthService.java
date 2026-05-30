package com.ott.user.service;
import com.ott.user.dto.*;
import com.ott.user.model.User;
import com.ott.user.repository.UserRepository;
import com.ott.user.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import org.slf4j.*;

/**
 * PURPOSE: Core authentication business logic.
 * LOGIN flow: find user → verify BCrypt password → generate JWT
 * REGISTER flow: validate unique → hash password → save → generate JWT
 *
 * CONNECTS TO:
 * - AuthController calls this
 * - UserRepository for DB access
 * - JwtUtil for token generation
 * - PasswordEncoder for BCrypt
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository r, JwtUtil j, PasswordEncoder p) {
        userRepository = r; jwtUtil = j; passwordEncoder = p;
    }

    /** LOGIN: Validates credentials, returns JWT token */
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
            .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        System.out.println("...incomingUserDetailsVarified by DB:"+user);
        if (!user.getIsActive())
            throw new RuntimeException("Account is deactivated");

        System.out.println(req.getPassword() +"<----- user.getPasswdFromReq    &&&&&    user.getPasswdFromDB:->"+ user.getPassword());
        // BCrypt check: compares raw password with stored hash
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new RuntimeException("Invalid username or password");

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        log.info("Login success for user: {}", user.getUsername());
        return new AuthResponse(token, user.getUserId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }

    /** REGISTER: Creates new user account, returns JWT token */
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername()))
            throw new RuntimeException("Username already taken");
        if (userRepository.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already registered");

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setFullName(req.getFullName());
        user.setPhone(req.getPhone());
        // BCrypt hash password — never store plain text
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(User.Role.USER);

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getUsername(), saved.getRole().name());
        log.info("Registered new user: {}", saved.getUsername());
        return new AuthResponse(token, saved.getUserId(), saved.getUsername(), saved.getEmail(), saved.getRole().name());
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found into DAtabase"));
    }
    public List<User> getUsers() { // for testing()
//        return userRepository.findAll().orElseThrow(() -> new RuntimeException("No User found in DB"));
    	return userRepository.findAll();//.orElseThrow(() -> new RuntimeException("No User found in DB"));
    }
}