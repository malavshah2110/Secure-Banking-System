package org.asu.group8.impl;

import org.asu.group8.config.Constants;
import org.asu.group8.entity.SbsAccount;
import org.asu.group8.entity.SbsTransaction;
import org.asu.group8.entity.SbsUser;
import org.asu.group8.repo.AccountRepository;
import org.asu.group8.repo.TransactionRepository;
import org.asu.group8.repo.UserRepository;
import org.asu.group8.service.AccountService;
import org.asu.group8.service.EmailService;
import org.asu.group8.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public boolean isCritical(SbsAccount sbsAccount, Double debitAmount)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        List<SbsTransaction> transactions =
                transactionRepository.findAllFromAfter(sbsAccount.getAccountNumber(), calendar.getTime());

        Double sum = 0.0;
        for (SbsTransaction transaction : transactions) {
            sum = sum + transaction.getAmount();
        }

        sum += debitAmount;
        return (sum > 500);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SbsTransaction createTransaction(String username, String type, Long from, Long to, Double amount, Boolean critical, Boolean holdInternal, Boolean holdTransfer, Boolean completed) {
        SbsTransaction sbsTransaction = new SbsTransaction();

        sbsTransaction.setType(type);
        sbsTransaction.setTimestamp(new Date());
        sbsTransaction.setFromAccount(from);
        sbsTransaction.setToAccount(to);
        sbsTransaction.setAmount(amount);
        sbsTransaction.setCritical(critical);
        sbsTransaction.setHoldInternal(holdInternal);
        sbsTransaction.setHoldTransfer(holdTransfer);
        sbsTransaction.setCompleted(completed);
        sbsTransaction.setUsername(username);

        return transactionRepository.save(sbsTransaction);
    }

    // check if the amount can come out of the from account right now
    private boolean checkFromAccount(SbsAccount sbsAccount, Double requestAmount) {
        if (sbsAccount == null) {
            return false;
        } else {
            if (sbsAccount.getAccountType().equals(Constants.ACCOUNT_CHECKING) ||
                    sbsAccount.getAccountType().equals(Constants.ACCOUNT_SAVINGS)) {
                return sbsAccount.getAccountBalance() - requestAmount > 0;
            } else if (sbsAccount.getAccountType().equals(Constants.ACCOUNT_CREDIT)) {
                return sbsAccount.getAccountBalance() - requestAmount > -1 * sbsAccount.getCreditLimit();
            } else {
                return false;
            }
        }
    }

    private boolean checkToAccount(SbsAccount sbsAccount) {
        if (sbsAccount == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean checkTransaction(SbsAccount sbsFromAccount, SbsAccount sbsToAccount, String type, Double amount) {
        if (type.equals(Constants.TRANSACTION_DEBIT)) {
            if (sbsFromAccount == null) {
                return false;
            } else {
                return checkFromAccount(sbsFromAccount, amount);
            }
        } else if (type.equals(Constants.TRANSACTION_CREDIT)) {
            if (sbsToAccount == null) {
                return false;
            } else {
                return true;
            }
        } else if (type.equals(Constants.TRANSACTION_TRANSFER)) {
            if (sbsFromAccount == null || sbsToAccount == null) {
                return false;
            } else {
                return checkFromAccount(sbsFromAccount, amount);
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean checkTransactionAuth(SbsUser sbsUser, SbsAccount sbsFromAccount, SbsAccount sbsToAccount, String type) {
        return (sbsUser.getCanModifyAccount() &&
                    (sbsFromAccount == null || accountService.t1Control(sbsUser, sbsFromAccount.getSbsUser())) &&
                    (sbsToAccount == null || accountService.t1Control(sbsUser, sbsToAccount.getSbsUser())) ||
                ((!type.equals(Constants.TRANSACTION_CREDIT) ||
                    sbsToAccount == null ||
                    sbsToAccount.getSbsUser().getUsername().equals(sbsUser.getUsername())) &&
                (!type.equals(Constants.TRANSACTION_DEBIT) ||
                    sbsFromAccount == null ||
                    sbsFromAccount.getSbsUser().getUsername().equals(sbsUser.getUsername())) &&
                (!type.equals(Constants.TRANSACTION_TRANSFER) ||
                    sbsFromAccount == null ||
                    sbsFromAccount.getSbsUser().getUsername().equals(sbsUser.getUsername()))));
    }

    @Override
    public void authorizeTransaction(SbsTransaction sbsTransaction) {

        SbsAccount toAccount = accountRepository.findByAccountNumber(sbsTransaction.getToAccount());
        SbsAccount fromAccount = accountRepository.findByAccountNumber(sbsTransaction.getFromAccount());

        if (sbsTransaction.getType().equals(Constants.TRANSACTION_DEBIT)) {
            if (checkFromAccount(fromAccount, sbsTransaction.getAmount())) {
                fromAccount.setAccountBalance(fromAccount.getAccountBalance() - sbsTransaction.getAmount());

                accountRepository.save(fromAccount);
            }
        } else if (sbsTransaction.getType().equals(Constants.TRANSACTION_CREDIT)) {
            if (checkToAccount(toAccount)) {
                toAccount.setAccountBalance(toAccount.getAccountBalance() + sbsTransaction.getAmount());

                accountRepository.save(toAccount);
            }
        } else if (sbsTransaction.getType().equals(Constants.TRANSACTION_TRANSFER)) {
            if (checkFromAccount(fromAccount,sbsTransaction.getAmount()) &&
                    checkToAccount(toAccount)) {
                fromAccount.setAccountBalance(fromAccount.getAccountBalance() - sbsTransaction.getAmount());
                toAccount.setAccountBalance(toAccount.getAccountBalance() + sbsTransaction.getAmount());

                accountRepository.save(fromAccount);
                accountRepository.save(toAccount);
            }
        }
    }

    @Override
    public void notifyCritical(SbsAccount sbsAccount, Date timestamp, Double amount) {
        if (sbsAccount != null) {
            emailService.sendEmail(sbsAccount.getSbsUser(), "Critical Transaction Alert",
                    "Hello " + sbsAccount.getSbsUser().getName() + ",\n\n" +
                            "A critical transaction was just created from your account (" + sbsAccount.getAccountNumber() + ") " +
                            "on " + timestamp + " with amount $" + amount + ". " +
                            "If you did not create or request this transaction, please call your bank immediately " +
                            "to resolve this critical issue.\n\n" +
                            "Thank you!");
        }
    }

}
