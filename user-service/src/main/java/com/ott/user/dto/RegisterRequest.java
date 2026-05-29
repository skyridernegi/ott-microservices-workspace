package com.ott.user.dto;
import jakarta.validation.constraints.*;

/** What ESPN UI sends for new account creation */
public class RegisterRequest {
    @NotBlank private String username;
    @Email @NotBlank private String email;
    @NotBlank @Size(min = 6) private String password;
    @NotBlank private String fullName;
    private String phone;
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
}