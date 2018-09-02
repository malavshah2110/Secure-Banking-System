package org.asu.group8.model;

import java.util.ArrayList;
import java.util.List;

public class ViewTransactionsResponse {
    private List<TransactionDetails> transactions = new ArrayList<TransactionDetails>();

    public List<TransactionDetails> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDetails> transactions) {
        this.transactions = transactions;
    }
}
