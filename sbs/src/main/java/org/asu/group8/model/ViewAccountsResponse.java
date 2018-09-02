package org.asu.group8.model;

import java.util.ArrayList;
import java.util.List;

public class ViewAccountsResponse {

    private Boolean success;
    private List<AccountDetails> accounts = new ArrayList<>();

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<AccountDetails> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountDetails> accounts) {
        this.accounts = accounts;
    }
}
