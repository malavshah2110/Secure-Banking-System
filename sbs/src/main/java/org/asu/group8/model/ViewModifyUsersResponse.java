package org.asu.group8.model;

import java.util.ArrayList;
import java.util.List;

public class ViewModifyUsersResponse {

    private List<UserRequestDetails> requests = new ArrayList<>();

    public List<UserRequestDetails> getRequests() {
        return requests;
    }

    public void setRequests(List<UserRequestDetails> requests) {
        this.requests = requests;
    }
}
