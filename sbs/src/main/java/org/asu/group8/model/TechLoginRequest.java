package org.asu.group8.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class TechLoginRequest {

    @NotNull(message = "Username cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9]{3,40}$", message = "Please enter a valid username")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
