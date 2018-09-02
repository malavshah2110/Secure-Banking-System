package org.asu.group8.model;

public class ResolveTransferResponse {

    private Boolean accessed;
    private Boolean resolved;

    public Boolean getResolved() {
        return resolved;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }

    public Boolean getAccessed() {
        return accessed;
    }

    public void setAccessed(Boolean accessed) {
        this.accessed = accessed;
    }
}
