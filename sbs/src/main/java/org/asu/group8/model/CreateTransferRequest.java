package org.asu.group8.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class CreateTransferRequest {

    private Long source;

    private Long destination;

    @NotNull(message = "Transfer type cannot be null")
    @Pattern(regexp = "^(credit|debit|transfer)$", message = "Transfer type must be credit or debit or transfer")
    private String type;

    @NotNull(message = "Transfer amount cannot be null")
    @Min(value = 0, message = "Transfer amount cannot be negative")
    @Max(value = 10000, message = "10000 maximum amount")
    private Double amount;

    @Pattern(regexp = "^[0-9]{6}$")
    private String otp;

    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    public Long getDestination() {
        return destination;
    }

    public void setDestination(Long destination) {
        this.destination = destination;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
