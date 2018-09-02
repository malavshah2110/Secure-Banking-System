package org.asu.group8.service;

import org.asu.group8.entity.SbsUser;

public interface AccountService {

    boolean createChecking(String username);
    boolean createSavings(String username);
    boolean createCredit(String username, String cardNumber, String CVV, Double creditLimit, Double latePayment, Double creditInterest);

    boolean t1Control(SbsUser sbsUserInternal, SbsUser sbsUserExternal);
}
