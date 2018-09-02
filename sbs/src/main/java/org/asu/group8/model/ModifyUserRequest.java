package org.asu.group8.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

public class ModifyUserRequest {

    @Pattern(regexp = "^[a-zA-Z.\\s]{3,40}$", message = "Please enter a valid name")
    private String name;                // not PII

    @Pattern(regexp = "^[a-z0-9._%-]+@[a-z0-9.-]+\\.[a-z]{2,4}$", message = "Please enter a valid email")
    private String email;               // not PII

    @Pattern(regexp = "^[0-9]{10}$", message = "Please enter a valid phone number")
    private String phoneNumber;         // PII

    @Pattern(regexp = "^[0-9a-zA-Z \\-.,#]{2,70}$", message = "Please enter a valid billing address")
    private String billingAddr;         // PII

    @Pattern(regexp = "^[0-9a-zA-Z \\-.,#]{2,70}$", message = "Please enter a valid mailing address")
    private String mailingAddr;         // PII

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
