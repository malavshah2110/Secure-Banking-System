package org.asu.group8.model;

public class UserRequestDetails {

    private Long requestId;
    private String username;
    private String name;
    private String email;
    private String phoneNumber;
    private String billingAddr;
    private String mailingAddr;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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
