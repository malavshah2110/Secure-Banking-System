package org.asu.group8.model;

public class CreateUserResponse {

    private Boolean created;
    private Boolean exists;
    private Boolean unauthorized;

    public Boolean getCreated() {
        return created;
    }

    public void setCreated(Boolean created) {
        this.created = created;
    }

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    public Boolean getUnauthorized() {
        return unauthorized;
    }

    public void setUnauthorized(Boolean unauthorized) {
        this.unauthorized = unauthorized;
    }
}
