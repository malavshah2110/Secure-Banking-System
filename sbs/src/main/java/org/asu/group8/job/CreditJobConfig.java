package org.asu.group8.job;

import org.asu.group8.entity.SbsAccount;
import org.asu.group8.entity.SbsTransaction;
import org.asu.group8.entity.SbsUser;
import org.asu.group8.repo.TransactionRepository;
import org.asu.group8.repo.UserRepository;
import org.asu.group8.service.LogService;
import org.asu.group8.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Configuration
@EnableScheduling
public class CreditJobConfig {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private LogService logService;

    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void runCreditJob() {

        logService.log(null, "Credit job started");

        // time to check all credit accounts for payments
        List<SbsUser> sbsUsers = userRepository.findAll();
        for (SbsUser sbsUser : sbsUsers) {

            // only check for ind and org that are not modify requests
            if (!sbsUser.getModifyRequest()) {

                Set<SbsAccount> sbsAccounts = sbsUser.getSbsAccountList();
                for (SbsAccount sbsAccount : sbsAccounts) {
                    if (sbsAccount.getAccountType().equals("credit")) {

                        List<SbsTransaction> sbsTransactions =
                                transactionRepository.findCompletedByAccount(sbsAccount.getAccountNumber());

                        // sum the debits and credits
                        Double balance = 0.0;
                        for (SbsTransaction sbsTransaction : sbsTransactions) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.MONTH, -1);

                            if (sbsTransaction.getType().equals("debit") &&
                                    sbsTransaction.getTimestamp().getTime() < calendar.getTime().getTime()) {
                                balance -= sbsTransaction.getAmount();
                            }
                            if (sbsTransaction.getType().equals("credit")) {
                                balance += sbsTransaction.getAmount();
                            }
                        }

                        // apply late fee and interest if necessary
                        if (balance < 0) {

                            SbsTransaction sbsTransaction;
                            sbsTransaction = transactionService.createTransaction(sbsUser.getUsername(), "debit",
                                    sbsAccount.getAccountNumber(), null,
                                    sbsAccount.getLatePayment(),
                                    false, false, false, true);
                            transactionService.authorizeTransaction(sbsTransaction);

                            sbsTransaction = transactionService.createTransaction(sbsUser.getUsername(), "debit",
                                    sbsAccount.getAccountNumber(), null,
                                    sbsAccount.getCreditInterest() * -balance,
                                    false, false, false, true);
                            transactionService.authorizeTransaction(sbsTransaction);

                        }
                    }
                }

            }

        }

        logService.log(null, "Credit job finished");

    }

}
