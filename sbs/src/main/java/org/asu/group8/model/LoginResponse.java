package org.asu.group8.model;

public class LoginResponse {

    private Boolean success;
    private Boolean untrusted;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getUntrusted() {
        return untrusted;
    }

    public void setUntrusted(Boolean untrusted) {
        this.untrusted = untrusted;
    }
}
