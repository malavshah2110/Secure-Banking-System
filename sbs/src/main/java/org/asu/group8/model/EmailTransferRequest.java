package org.asu.group8.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class EmailTransferRequest {

    @NotNull(message = "Transfer email cannot be null")
    @Pattern(regexp = "^[a-z0-9._%-]+@[a-z0-9.-]+\\.[a-z]{2,4}$", message = "Please enter a valid email")
    private String email = null;

    @NotNull(message = "From account number cannot be null")
    private Long from = null;

    @NotNull(message = "Transfer amount cannot be null")
    @Min(value = 0, message = "Transfer amount cannot be negative")
    @Max(value = 10000, message = "10000 maximum amount")
    private Double amount;

    @Pattern(regexp = "^[0-9]{6}$", message = "Please enter a valid OTP")
    private String otp;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
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
