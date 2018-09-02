package org.asu.group8.model;

public class SetT1AccountAccessResponse {

    private Boolean success;
    private Boolean unauthorized;
    private Boolean doesNotExist;
    private Boolean notT1User;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getUnauthorized() {
        return unauthorized;
    }

    public void setUnauthorized(Boolean unauthorized) {
        this.unauthorized = unauthorized;
    }

    public Boolean getDoesNotExist() {
        return doesNotExist;
    }

    public void setDoesNotExist(Boolean doesNotExist) {
        this.doesNotExist = doesNotExist;
    }

    public Boolean getNotT1User() {
        return notT1User;
    }

    public void setNotT1User(Boolean notT1User) {
        this.notT1User = notT1User;
    }
}
