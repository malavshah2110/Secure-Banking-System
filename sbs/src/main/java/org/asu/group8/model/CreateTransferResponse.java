package org.asu.group8.model;

public class CreateTransferResponse {

    private Boolean requested;
    private Boolean completed;
    private Boolean unauthorized;
    private Boolean badAccounts;
    private Boolean checkOtp;

    public Boolean getRequested() {
        return requested;
    }

    public void setRequested(Boolean requested) {
        this.requested = requested;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getUnauthorized() {
        return unauthorized;
    }

    public void setUnauthorized(Boolean unauthorized) {
        this.unauthorized = unauthorized;
    }

    public Boolean getBadAccounts() {
        return badAccounts;
    }

    public void setBadAccounts(Boolean badAccounts) {
        this.badAccounts = badAccounts;
    }

    public Boolean getCheckOtp() {
        return checkOtp;
    }

    public void setCheckOtp(Boolean checkOtp) {
        this.checkOtp = checkOtp;
    }
}
