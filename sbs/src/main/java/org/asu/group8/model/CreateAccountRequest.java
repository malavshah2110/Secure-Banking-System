package org.asu.group8.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class CreateAccountRequest {

    @NotNull
    @Pattern(regexp = "^(checking|savings|credit)$", message = "Account type must be checking or savings or credit")
    private String accountType;

    @Pattern(regexp = "^[0-9]{16}$", message = "Please enter a valid card number")
    private String cardNumber;

    @Pattern(regexp = "^[0-9]{3}$", message = "Please enter a valid CVV")
    private String cvv;

    @Min(value = 0, message = "Negative credit limit")
    @Max(value = 50000, message = "50000 maximum credit limit")
    private Double creditLimit;

    @Min(value = 0, message = "Negative late payment fee")
    @Max(value = 10000, message = "10000 maximum late payment fee")
    private Double latePayment;

    @Min(value = 0, message = "Negative interest rate")
    @Max(value = 1, message = "100% maximum credit interest")
    private Double creditInterest;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public void setCardNumber(String cardNumber){
        this.cardNumber = cardNumber;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public void setLatePayment(Double latePayment) { this.latePayment = latePayment; }

    public Double getLatePayment() { return latePayment; }

    public void setCreditInterest(Double creditInterest) { this.creditInterest = creditInterest; }

    public Double getCreditInterest() { return creditInterest; }
}
