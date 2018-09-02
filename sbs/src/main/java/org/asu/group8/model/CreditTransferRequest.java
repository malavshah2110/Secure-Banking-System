package org.asu.group8.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class CreditTransferRequest {

    @NotNull
    @Pattern(regexp = "^[0-9]{16}$", message = "Please enter a valid card number")
    private String cardNumber;

    @NotNull
    @Pattern(regexp = "^[0-9]{3}$", message = "Please enter a valid CVV")
    private String cvv;

    @NotNull
    private Long to = null;

    @NotNull(message = "Transfer amount cannot be null")
    @Min(value = 0, message = "Transfer amount cannot be negative")
    @Max(value = 10000, message = "10000 maximum amount")
    private Double amount;

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
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
}
