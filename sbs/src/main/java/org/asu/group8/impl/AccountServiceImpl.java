package org.asu.group8.impl;

import org.asu.group8.config.Constants;
import org.asu.group8.entity.SbsAccount;
import org.asu.group8.entity.SbsUser;
import org.asu.group8.repo.AccountRepository;
import org.asu.group8.repo.UserRepository;
import org.asu.group8.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean createChecking(String username) {
        SbsUser sbsUser = userRepository.findByUsername(username);

        if (sbsUser != null) {
            SbsAccount sbsAccount = new SbsAccount();
            sbsAccount.setAccountType(Constants.ACCOUNT_CHECKING);
            sbsAccount.setAccountBalance(0.0);

            sbsUser.addSbsAccount(sbsAccount);

            sbsAccount = accountRepository.save(sbsAccount);
            sbsAccount.setAccountNumber(getAccountNumber(sbsAccount.getId()));
            accountRepository.save(sbsAccount);

            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean createSavings(String username) {
        SbsUser sbsUser = userRepository.findByUsername(username);

        if (sbsUser != null) {
            SbsAccount sbsAccount = new SbsAccount();
            sbsAccount.setAccountType(Constants.ACCOUNT_SAVINGS);
            sbsAccount.setAccountBalance(0.0);

            sbsUser.addSbsAccount(sbsAccount);
            sbsAccount = accountRepository.save(sbsAccount);
            sbsAccount.setAccountNumber(getAccountNumber(sbsAccount.getId()));
            accountRepository.save(sbsAccount);

            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean createCredit(String username, String cardNumber, String CVV, Double creditLimit, Double latePayment, Double creditInterest) {
        SbsUser sbsUser = userRepository.findByUsername(username);

        if (sbsUser != null) {
            SbsAccount sbsAccount = new SbsAccount();
            sbsAccount.setAccountType(Constants.ACCOUNT_CREDIT);
            sbsAccount.setAccountBalance(0.0);
            sbsAccount.setCardNumber(cardNumber);
            sbsAccount.setCvv(CVV);
            sbsAccount.setCreditLimit(creditLimit);
            sbsAccount.setLatePayment(latePayment);
            sbsAccount.setCreditInterest(creditInterest);

            sbsUser.addSbsAccount(sbsAccount);
            sbsAccount = accountRepository.save(sbsAccount);
            sbsAccount.setAccountNumber(getAccountNumber(sbsAccount.getId()));
            accountRepository.save(sbsAccount);

            return true;
        } else {
            return false;
        }
    }

    // 30 bits used for account number
    public Long getAccountNumber(Long id) {
        return (Long.reverse(id) >>> (64 - 29)) | (1L << 29);
    }

    @Override
    public boolean t1Control(SbsUser sbsUserInternal, SbsUser sbsUserExternal) {
        return !sbsUserInternal.getUserType().equals(Constants.USER_TIER1) || sbsUserExternal.getAllowT1AccountAccess();
    }


}
