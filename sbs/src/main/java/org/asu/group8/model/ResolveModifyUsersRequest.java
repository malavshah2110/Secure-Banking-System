package org.asu.group8.model;

import javax.validation.constraints.NotNull;

public class ResolveModifyUsersRequest {

    @NotNull(message = "User ID cannot be null")
    private Long modifyUserId;

    @NotNull(message = "Authorize flag cannot be null")
    private Boolean authorize;

    public Long getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public Boolean getAuthorize() {
        return authorize;
    }

    public void setAuthorize(Boolean authorize) {
        this.authorize = authorize;
    }
}
