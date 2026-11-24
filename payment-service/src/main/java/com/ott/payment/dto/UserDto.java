package com.ott.payment.dto;

import java.time.LocalDateTime;

public class UserDto{

    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Role role = Role.USER;
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public enum Role { USER, ADMIN }

    public UserDto() {}
    public Long getUserId() {		return userId;	}
    public void setUserId(Long userId) {		this.userId = userId;	}
    public String getUsername() {		return username;	}
    public void setUsername(String username) {		this.username = username;	}
    public String getEmail() {		return email;	}
    public void setEmail(String email) {		this.email = email;	}
    public String getFullName() {		return fullName;	}
    public void setFullName(String fullName) {		this.fullName = fullName;	}
    public String getPhone() {		return phone;	}
    public void setPhone(String phone) {		this.phone = phone;	}
    public Role getRole() {		return role;	}
    public void setRole(Role role) {		this.role = role;	}
    public Boolean getIsActive() {		return isActive;	}
    public void setIsActive(Boolean isActive) {		this.isActive = isActive;	}
    public LocalDateTime getCreatedAt() {		return createdAt;	}
    public void setCreatedAt(LocalDateTime createdAt) {		this.createdAt = createdAt;	}
    public LocalDateTime getUpdatedAt() {		return updatedAt;	}
    public void setUpdatedAt(LocalDateTime updatedAt) {		this.updatedAt = updatedAt;	}
	@Override
	public String toString() {
		return "UserDto [userId=" + userId + ", username=" + username + ", email=" + email + ", fullName=" + fullName
				+ ", phone=" + phone + ", role=" + role + ", isActive=" + isActive + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + "]";
	}  
}
