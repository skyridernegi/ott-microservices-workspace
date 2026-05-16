package com.ott.subscription.repository;

import com.ott.subscription.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User database operations.
 *
 * Spring automatically provides:
 * - findAll()        → SELECT * FROM users
 * - findById()       → SELECT * FROM users WHERE user_id = ?
 * - save()           → INSERT or UPDATE
 * - deleteById()     → DELETE FROM users WHERE user_id = ?
 *
 * Custom methods below — Spring generates SQL automatically
 * just by reading the method name. No SQL needed!
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);

    // SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // SELECT COUNT(*) > 0 FROM users WHERE email = ?
    boolean existsByEmail(String email);

    // SELECT COUNT(*) > 0 FROM users WHERE username = ?
    boolean existsByUsername(String username);
}