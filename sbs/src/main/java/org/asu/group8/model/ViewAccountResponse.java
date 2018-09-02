package org.asu.group8.model;

public class ViewAccountResponse {

    private Boolean success;

    private Long accountNumber;
    private String accountType;
    private double accountBalance;

    private String cardNumber;
    private String cvv;

    private Double creditLimit;
    private Double latePayment;
    private Double creditInterest;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Long getAccountNumber() {

        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setCreditInterest(Double creditInterest) { this.creditInterest = creditInterest; }

    public Double getCreditInterest() { return creditInterest; }

    public void setLatePayment(Double latePayment) { this.latePayment = latePayment; }

    public Double getLatePayment() { return latePayment; }
}
