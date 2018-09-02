package org.asu.group8.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class SetT1AccountAccessRequest {

    @NotNull(message = "Username cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9]{3,40}$", message = "enter a valid username")
    private String username;

    @NotNull(message = "CanAccess flag cannot be null")
    private Boolean canAccess;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getCanAccess() {
        return canAccess;
    }

    public void setCanAccess(Boolean canAccess) {
        this.canAccess = canAccess;
    }
}
