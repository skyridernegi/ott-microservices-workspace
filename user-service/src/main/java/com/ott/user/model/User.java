package com.ott.user.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * PURPOSE: Stores all ESPN+ users with hashed passwords and roles.
 * CONNECTS TO: AuthService reads this for login validation.
 *              UserRepository saves new registrations.
 */
@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password; // BCrypt hashed — NEVER plain text

    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Role { USER, ADMIN }

    @PrePersist
    protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public User() {}
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long v) { userId = v; }
    public String getUsername() { return username; }
    public void setUsername(String v) { username = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { password = v; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { fullName = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { phone = v; }
    public Role getRole() { return role; }
    public void setRole(Role v) { role = v; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean v) { isActive = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
	@Override
	public String toString() {
		return "User [userId=" + userId + ", username=" + username + ", email=" + email + ", password=" + password
				+ ", fullName=" + fullName + ", phone=" + phone + ", role=" + role + ", isActive=" + isActive
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
    
    
}