package org.asu.group8.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class LoginRequest {

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]{3,40}$", message = "Please enter a valid username")
    private String username;

    @NotNull(message = "Password cannot be null")
    @Pattern(regexp = "^.{5,50}$", message = "Password must be 5 to 50 characters")
    private String password;

    @Pattern(regexp = "^[0-9]{6}$", message = "Please enter a valid OTP")
    private String otp;                     // One-Time Password

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

}
