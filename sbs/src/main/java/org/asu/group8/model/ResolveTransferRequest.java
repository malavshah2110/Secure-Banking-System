package org.asu.group8.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ResolveTransferRequest {

    @NotNull
    private Long transactionNumber;

    @NotNull
    private boolean authorize;

    public Long getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(Long transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public boolean isAuthorize() {
        return authorize;
    }

    public void setAuthorize(boolean authorize) {
        this.authorize = authorize;
    }

}
