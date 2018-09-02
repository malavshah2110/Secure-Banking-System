package org.asu.group8.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class ModifyAccountRequest {

    @Min(value = 0, message = "Account balance cannot be negative")
    @Max(value = 1000000000, message = "1000000000 maximum amount")
    private Double accountBalance;

    @Min(value = 0, message = "Credit limit cannot be negative")
    @Max(value = 50000, message = "50000 maximum credit limit")
    private Double creditLimit;

    @Min(value = 0, message = "Negative late payment fee")
    @Max(value = 10000, message = "10000 maximum late payment fee")
    private Double latePayment;

    @Min(value = 0, message = "Negative interest rate")
    @Max(value = 1, message = "100% maximum credit interest")
    private Double creditInterest;

    public void setAccountBalance(Double accountBalance){ this.accountBalance = accountBalance; }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setLatePayment(Double latePayment) { this.latePayment = latePayment; }

    public Double getLatePayment() { return latePayment; }

    public void setCreditInterest(Double creditInterest) { this.creditInterest = creditInterest; }

    public Double getCreditInterest() { return creditInterest; }
}
