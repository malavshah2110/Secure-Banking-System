package org.asu.group8;

import org.asu.group8.entity.SbsAccount;
import org.asu.group8.entity.SbsTransaction;
import org.asu.group8.entity.SbsUser;
import org.asu.group8.job.CreditJobConfig;
import org.asu.group8.repo.AccountRepository;
import org.asu.group8.repo.TransactionRepository;
import org.asu.group8.repo.UserRepository;
import org.asu.group8.service.AccountService;
import org.asu.group8.service.TransactionService;
import org.asu.group8.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.List;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private CreditJobConfig creditJobConfig;

    @Bean
    public CommandLineRunner demo() {
        return (args) -> {
            // uncomment the next line to initialize a database
            //initDatabase();

            List<SbsUser> sbsUsers = userRepository.findAll();

            System.out.println("SbsUsers:");
            for (SbsUser sbsUser : sbsUsers) {
                System.out.println("\t" + sbsUser.getUsername() + ":" + sbsUser.getId());
                for (SbsAccount sbsAccount : sbsUser.getSbsAccountList()) {
                    System.out.println("\t\t" + sbsAccount.getAccountNumber() + ":" + sbsAccount.getAccountType());
                }
            }

        };
    }

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionService transactionService;

    @Transactional
    private void initDatabase() {

        // save a couple of customers

        /*** INDIVIDUAL ***/

        userService.createInd(
                "bob", "Bob Pizza", "bob_invalid@asu.edu",
                "0000000000", "", "", "password");
        accountService.createCredit("bob", "2222333344445555", "123", 1000.0, 20.0, 0.1);
        accountService.createCredit("bob", "4444555566667777", "123", 1000.0, 20.0, 0.1);

        SbsTransaction sbsTransaction = null;
        sbsTransaction = new SbsTransaction();
        sbsTransaction.setType("debit");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -40);
        sbsTransaction.setTimestamp(calendar.getTime());
        sbsTransaction.setFromAccount(accountRepository.findBycardNumber("2222333344445555").getAccountNumber());
        sbsTransaction.setToAccount(null);
        sbsTransaction.setAmount(80.0);
        sbsTransaction.setCritical(false);
        sbsTransaction.setHoldInternal(false);
        sbsTransaction.setHoldTransfer(false);
        sbsTransaction.setCompleted(true);
        sbsTransaction.setUsername("bob");

        sbsTransaction = transactionRepository.save(sbsTransaction);
        transactionService.authorizeTransaction(sbsTransaction);

        sbsTransaction = new SbsTransaction();
        sbsTransaction.setType("credit");
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DATE, -1);
        sbsTransaction.setTimestamp(calendar2.getTime());
        sbsTransaction.setFromAccount(null);
        sbsTransaction.setToAccount(accountRepository.findBycardNumber("2222333344445555").getAccountNumber());
        sbsTransaction.setAmount(70.0);
        sbsTransaction.setCritical(false);
        sbsTransaction.setHoldInternal(false);
        sbsTransaction.setHoldTransfer(false);
        sbsTransaction.setCompleted(true);
        sbsTransaction.setUsername("bob");

        sbsTransaction = transactionRepository.save(sbsTransaction);
        transactionService.authorizeTransaction(sbsTransaction);

        userService.createInd(
                "ind", "Individual User", "individual.user@xyz.edu",
                "0000000000", "300 E University Dr, Tempe, AZ 85281", "300 E University Dr, Tempe, AZ 85281", "UnicornsAreReal!");
        accountService.createSavings("ind");
        accountService.createChecking("ind");
        accountService.createCredit("ind", "1111222233334444", "123", 1000.0, 20.0, 0.1);

        userService.createInd(
                "donald", "Donald Trump", "donald.trump@xyz.edu",
                "0000000000", "1600 Pennsylvania Ave NW, Washington, DC 20500", "1600 Pennsylvania Ave NW, Washington, DC 20500", "soOrange10");
        accountService.createSavings("donald");
        accountService.createChecking("donald");
        accountService.createCredit("donald", "3333444455556666", "123", 1000.0, 20.0, 0.1);

        userService.createInd(
                "barack", "Barack Obama", "barack.obama@xyz.edu",
                "0000000000", "1600 Pennsylvania Ave NW, Washington, DC 20500", "1600 Pennsylvania Ave NW, Washington, DC 20500", "SmokeAJoint@1255");
        accountService.createSavings("barack");
        accountService.createChecking("barack");
        accountService.createCredit("barack", "8888999900001111", "123", 1000.0, 20.0, 0.1);

        userService.createInd(
                "george", "George Bush", "george.bush@xyz.edu",
                "0000000000", "1600 Pennsylvania Ave NW, Washington, DC 20500", "1600 Pennsylvania Ave NW, Washington, DC 20500", "$$GoGreen!!");
        accountService.createSavings("george");
        accountService.createChecking("george");
        accountService.createCredit("george", "6666777788889999", "123", 1000.0, 20.0, 0.1);

        userService.createInd(
                "fred", "Fred Weasley", "fredweasley@xyz.edu",
                "0000000000", "4, The Burrow, London 60097", "4, The Burrow, London 60097", "Georgeiscrazy!!");
        accountService.createCredit("fred", "6758945697885634", "458", 1000.0, 20.0, 0.1);
        accountService.createCredit("fred", "6879453921560934", "124", 1000.0, 20.0, 0.1);
        accountService.createCredit("fred", "9999000011112222", "125", 1000.0, 20.0, 0.1);
        accountService.createCredit("fred", "0000111122223333", "127", 1000.0, 20.0, 0.1);

        /*** ORGANIZATION ***/

        userService.createOrg(
                "org", "Organization User", "organization.user@xyz.edu",
                "0000000000", "", "", "#90@notanorg");
        accountService.createSavings("org");
        accountService.createChecking("org");

        userService.createOrg(
                "amazon", "Amazon Inc", "amazon.inc@xyz.edu",
                "0000000000", "1516 Second Avenue, Seattle, Washington 98101", "1516 Second Avenue, Seattle, Washington 98101", "#BetterThanEbay#");
        accountService.createSavings("amazon");
        accountService.createChecking("amazon");

        userService.createOrg(
                "ebay", "Ebay Inc", "ebay.inc@xyz.edu",
                "0000000000", "2025 Hamilton Avenue San Jose, California 95125", "2025 Hamilton Avenue San Jose, California 95125", "AmazonSucks");
        accountService.createSavings("ebay");
        accountService.createChecking("ebay");

        userService.createOrg(
                "microsoft", "Microsoft Inc", "microsoft.inc@xyz.edu",
                "4258828080", "15010 NE 36 th St. Redmond, WA 98052", "15010 NE 36 th St. Redmond, WA 98052", "$$GoWindows$$");
        accountService.createSavings("microsoft");
        accountService.createChecking("microsoft");
        accountService.createChecking("microsoft");
        accountService.createChecking("microsoft");

        userService.createOrg(
                "Hogwarts", "dumbledore", "dumbledore@hogwarts.edu",
                "0000000000", "Hogwarts School of Witchcraft and Wizardry, London", "Hogwarts School of Witchcraft and Wizardry, London", "Stupefy@MALFOY");
        accountService.createChecking("Hogwarts");

        /*** TIER1 ***/

        userService.createTier1(
                "tier1a", "Tier One A", "tier1a@xyz.edu",
                "0000000000", "123 6th St. Melbourne, FL 32904", "123 6th St. Melbourne, FL 32904", "Ineedaccess;");

        userService.createTier1(
                "tier1b", "Tier One B", "invalid_email@asu.edu",
                "0000000000", "71 Pilgrim Avenue Chevy Chase, MD 20815", "71 Pilgrim Avenue Chevy Chase, MD 20815", "hammock#garden");

        userService.createTier1(
                "tier1c", "Tier One C", "invalid_email@asu.edu",
                "0000000000", "70 Bowman St. South Windsor, CT 06074", "70 Bowman St. South Windsor, CT 06074", "luna!!harry");

        userService.createTier1(
                "tier1d", "Tier One D", "invalid_email@asu.edu",
                "0000000000", "4 Goldfield Rd. Honolulu, HI 96815", "4 Goldfield Rd. Honolulu, HI 96815", "happyhannukah");

        userService.createTier1(
                "tier1e", "Tier One E", "invalid_email@asu.edu",
                "0000000000", "44 Shirley Ave. West Chicago, IL 60185", "44 Shirley Ave. West Chicago, IL 60185", "#StephenisSoCrazy#");

        userService.createTier1(
                "tier1f", "Tier One F", "invalid_email@asu.edu",
                "0000000000", "514 S. Magnolia St. Orlando, FL 32806", "514 S. Magnolia St. Orlando, FL 32806", "SuchitaisTheBest!!");

        /*** TIER2 ***/

        userService.createTier2(
                "tier2a", "Tier Two A", "tier2a@xyz.edu",
                "0000000000", "58 San Juan Road Waltham, MA 02453", "58 San Juan Road Waltham, MA 02453", "@Group8Rocks@");

        userService.createTier2(
                "tier2b", "Tier Two B", "invalid_email@asu.edu",
                "0000000000", "8 West Walnut Rd. Lakeville, MN 55044", "8 West Walnut Rd. Lakeville, MN 55044", "Piglet&pooh");

        userService.createTier2(
                "tier2c", "Tier Two C", "invalid_email@asu.edu",
                "0000000000", "905 N. Union Drive Saint Albans, NY 11412", "905 N. Union Drive Saint Albans, NY 11412", "joeyUchandler");

        userService.createTier2(
                "tier2d", "Tier Two D", "invalid_email@asu.edu",
                "0000000000", "1 Ivy Dr. Malvern, PA 19355", "1 Ivy Dr. Malvern, PA 19355", "Chocolates@relove");

        userService.createTier2(
                "tier2e", "Tier Two E", "invalid_email@asu.edu",
                "0000000000", "8356 Cardinal Ave. Moncks Corner, SC 29461", "8356 Cardinal Ave. Moncks Corner, SC 29461", "GoAway****");

        userService.createTier2(
                "tier2f", "Tier Two F", "invalid_email@asu.edu",
                "0000000000", "71 Water Street Salt Lake City, UT 84119", "71 Water Street Salt Lake City, UT 84119", "highOnlife@Stephen");

        /*** ADMINISTRATOR ***/

        userService.createAdmin(
                "stephen", "Stephen Seidel", "stephen.seidel@asu.edu",
                "0000000000", "", "", "cocomo(DutchBros)");

        userService.createAdmin(
                "rui", "Rui Zhang", "rzhan100@asu.edu",
                "1111111111", "", "", "%lollipos1008%");

        userService.createAdmin(
                "radha", "Radha Kannan", "rkanna11@asu.edu",
                "2222222222", "", "", "bubbles&buttercups");

        userService.createAdmin(
                "malav", "Malav Shah", "mpshah5@asu.edu",
                "3333333333", "", "", "mysterynight@MU");

        userService.createAdmin(
                "suchita", "Suchita Vichare", "ssvichar@asu.edu",
                "4444444444", "", "", "MagicExists!");

        userService.createAdmin(
                "abika", "Abika Santhosh", "asanthos@asu.edu",
                "5555555555", "", "", "talkingPanda@1798");

        userService.createAdmin(
                "chonghao", "Chonghao Cheng", "ccheng55@asu.edu",
                "6666666666", "", "", "sugars$candies$");

        userService.createAdmin(
                "sitanshu", "Sitanshu Mishra", "sitanshu.mishra@asu.edu",
                "7777777777", "", "", "!!DoNotBlameMyCode!!");

        userService.createAdmin(
                "srinidhi", "Srinidhi Sridharan", "ssridh38@asu.edu",
                "8888888888", "", "", "**stars**nights");

        userService.createAdmin(
                "srividhya", "Srividhya Swaminathan", "sswami10@asu.edu",
                "9999999999", "", "", "@@sunny@@mornings");

        creditJobConfig.runCreditJob();

    }

}
