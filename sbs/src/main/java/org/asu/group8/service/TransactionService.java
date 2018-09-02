package org.asu.group8.service;

import org.asu.group8.entity.SbsAccount;
import org.asu.group8.entity.SbsTransaction;
import org.asu.group8.entity.SbsUser;

import java.util.Date;

public interface TransactionService {

    SbsTransaction createTransaction(String username, String type, Long from, Long to, Double amount, Boolean critical, Boolean holdInternal, Boolean holdTransfer, Boolean completed);

    void authorizeTransaction(SbsTransaction sbsTransaction);

    boolean isCritical(SbsAccount sbsAccount, Double debitAmount);

    boolean checkTransaction(SbsAccount sbsFromAccount, SbsAccount sbsToAccount, String type, Double amount);

    boolean checkTransactionAuth(SbsUser sbsUser, SbsAccount sbsFromAccount, SbsAccount sbsToAccount, String type);

    void notifyCritical(SbsAccount sbsAccount, Date timestamp, Double amount);
}