package org.asu.group8.model;

public class TechLoginResponse {

    private Boolean unauthorized;
    private Boolean success;

    public Boolean getUnauthorized() {
        return unauthorized;
    }

    public void setUnauthorized(Boolean unauthorized) {
        this.unauthorized = unauthorized;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
