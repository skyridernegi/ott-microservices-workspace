package com.ott.user.dto;
import jakarta.validation.constraints.NotBlank;

/** What ESPN UI sends: {"username":"john.doe","password":"password123"} */
public class LoginRequest {
    @NotBlank private String username;
    @NotBlank private String password;
    public String getUsername() { return username; }
    public void setUsername(String v) { username = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { password = v; }
	@Override
	public String toString() {
		return "LoginRequest [username=" + username + ", password=" + password + "]";
	}
    
    
}