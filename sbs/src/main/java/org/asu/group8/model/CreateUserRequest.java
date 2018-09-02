package org.asu.group8.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class CreateUserRequest {

    @NotNull(message = "User type cannot be null")
    @Pattern(regexp = "^(admin|tier1|tier2|org|ind)$", message = "User type must be admin or tier1 or tier2 or org or ind")
    private String userType;

    @NotNull(message = "Username cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9]{3,40}$", message = "Username must be 3 to 40 letters or numbers")
    private String username;

    @NotNull(message = "Name cannot be null")
    @Pattern(regexp = "^[a-zA-Z.\\s]{3,40}$", message = "Please enter a valid name")
    private String name;

    @NotNull(message = "Email cannot be null")
    @Pattern(regexp = "^[a-z0-9._%-]+@[a-z0-9.-]+\\.[a-z]{2,4}$", message = "Please enter a valid email")
    private String email;

    @NotNull(message = "Phone number cannot be null")
    @Pattern(regexp = "^[0-9]{10}$", message = "Please enter a valid phone number")
    private String phoneNumber;

    @NotNull(message = "Billing address cannot be null")
    @Pattern(regexp = "^[0-9a-zA-Z \\-.,#]{2,70}$", message = "Please enter a valid billing address")
    private String billingAddr;

    @NotNull(message = "Mailing address cannot be null")
    @Pattern(regexp = "^[0-9a-zA-Z \\-.,#]{2,70}$", message = "Please enter a valid mailing address")
    private String mailingAddr;

    @NotNull(message = "Password cannot be null")
    @Pattern(regexp = "^.{5,50}$", message = "Password must be 5 to 50 characters")
    private String password;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBillingAddr() {
        return billingAddr;
    }

    public void setBillingAddr(String billingAddr) {
        this.billingAddr = billingAddr;
    }

    public String getMailingAddr() {
        return mailingAddr;
    }

    public void setMailingAddr(String mailingAddr) {
        this.mailingAddr = mailingAddr;
    }
}
