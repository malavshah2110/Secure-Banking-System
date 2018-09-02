package org.asu.group8.model;

import javax.validation.constraints.NotNull;

public class SetAllowT1AccountAccessRequest {

    @NotNull(message = "Allowed flag cannot be null")
    private boolean allowed;

    public boolean getAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }
}
