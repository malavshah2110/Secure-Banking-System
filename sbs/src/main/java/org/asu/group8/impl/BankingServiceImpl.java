package org.asu.group8.impl;

import org.asu.group8.config.Constants;
import org.asu.group8.entity.SbsAccount;
import org.asu.group8.entity.SbsTransaction;
import org.asu.group8.entity.SbsUser;
import org.asu.group8.model.*;
import org.asu.group8.repo.AccountRepository;
import org.asu.group8.repo.DeviceRepository;
import org.asu.group8.repo.TransactionRepository;
import org.asu.group8.repo.UserRepository;
import org.asu.group8.service.*;
import org.asu.group8.view.StatementPdfView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class BankingServiceImpl implements BankingService {

    @Autowired
    private SafeService safeService;

    @Autowired
    private LogService logService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /*** Helper Functions ***/

    @Transactional(isolation = Isolation.SERIALIZABLE)
    private SbsUser getLoginUser(String username) {
        SbsUser loginUser = userRepository.findByUsername(username);

        if (loginUser == null) {
            return null;
        } else {
            return checkRequestCount(loginUser);
        }

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    private SbsUser getCurrentUser() {

        SbsUser sbsUser =
                userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        return checkRequestCount(sbsUser);

    }

    private SbsUser checkRequestCount(SbsUser sbsUser) {
        boolean blocked = false;

        Calendar past = Calendar.getInstance();
        past.add(Calendar.MINUTE, -5);

        Calendar current = Calendar.getInstance();

        int reqCount = sbsUser.getReqCount();

        if (sbsUser.getReqDate().getTime() > past.getTime().getTime()) {
            // request within 5 minutes
            if (sbsUser.getReqCount() >= 25) {
                blocked = true;
            }
        } else {
            sbsUser.setReqDate(current.getTime());
            reqCount = 0;
            System.out.println("Resetting requests");
        }

        reqCount++;
        sbsUser.setReqCount(reqCount);

        if (blocked) {
            System.out.println("Blocked");
            return null;
        } else {
            System.out.println(sbsUser.getReqCount());
            return userRepository.save(sbsUser);
        }

    }

    private Boolean isInternal(String userType) {
        return userType.equals(Constants.USER_TIER1) ||
                userType.equals(Constants.USER_TIER2) ||
                userType.equals(Constants.USER_ADMIN);
    }

    /*** APIs ***/

    private Boolean isExternal(String userType) {
        return userType.equals(Constants.USER_IND) ||
                userType.equals(Constants.USER_ORG);
    }

    @Override
    public String username() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> login(LoginRequest request, HttpServletResponse response, String sbsDeviceCode) {

        SbsUser sbsLoginUser = getLoginUser(request.getUsername());
        if (sbsLoginUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // check if we can login
        LoginResponse loginResponse = safeService.login(request, response, sbsDeviceCode);

        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> techLogin(TechLoginRequest request) {
        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        TechLoginResponse response = new TechLoginResponse();

        if (sbsCurrentUser.getCanPerformTechAccess()) {
            boolean success = safeService.techLogin(request.getUsername());

            response.setSuccess(success);
            response.setUnauthorized(false);
        } else {
            response.setSuccess(false);
            response.setUnauthorized(true);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> createUser(CreateUserRequest request) {

        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        String userType = request.getUserType();

        // construct API response
        CreateUserResponse response = new CreateUserResponse();

        // check permissions
        if ((sbsCurrentUser.getCanCreateExternalUser() && isExternal(userType)) ||
                (sbsCurrentUser.getCanCreateInternalUser() && isInternal(userType))) {
            response.setUnauthorized(false);

            // check if the user exists
            if (userRepository.findByUsername(request.getUsername()) == null) {
                // authorized and does not exist
                response.setExists(false);

                if (userType.equals(Constants.USER_TIER1)) {
                    userService.createTier1(request.getUsername(),
                            request.getName(), request.getEmail(), request.getPhoneNumber(),
                            request.getBillingAddr(), request.getMailingAddr(), request.getPassword());
                } else if (userType.equals(Constants.USER_TIER2)) {
                    userService.createTier2(request.getUsername(),
                            request.getName(), request.getEmail(), request.getPhoneNumber(),
                            request.getBillingAddr(), request.getMailingAddr(), request.getPassword());
                } else if (userType.equals(Constants.USER_ADMIN)) {
                    userService.createAdmin(request.getUsername(),
                            request.getName(), request.getEmail(), request.getPhoneNumber(),
                            request.getBillingAddr(), request.getMailingAddr(), request.getPassword());
                } else if (userType.equals(Constants.USER_ORG)) {
                    userService.createOrg(request.getUsername(),
                            request.getName(), request.getEmail(), request.getPhoneNumber(),
                            request.getBillingAddr(), request.getMailingAddr(), request.getPassword());
                } else if (userType.equals(Constants.USER_IND)) {
                    userService.createInd(request.getUsername(),
                            request.getName(), request.getEmail(), request.getPhoneNumber(),
                            request.getBillingAddr(), request.getMailingAddr(), request.getPassword());
                }

                response.setCreated(true);
            } else {
                // authorized but exists
                response.setExists(true);
                response.setCreated(false);
            }

        } else {
            // unauthorized
            response.setUnauthorized(true);
            response.setCreated(false);
            response.setExists(false);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> viewUser(String username) {

        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

            // view current user if username is null
        if (username == null) username = sbsCurrentUser.getUsername();

        SbsUser sbsUserToView = userRepository.findByUsername(username);

        ViewUserResponse response = new ViewUserResponse();

        if (sbsUserToView == null) {
            logService.log(sbsCurrentUser, "User " + username + " to view does not exist.");
            response.setSuccess(false);
        } else {
            String userType = sbsUserToView.getUserType();

            if ((sbsCurrentUser.getCanViewExternalUser() && isExternal(userType)) ||
                    (sbsCurrentUser.getCanViewInternalUser() && isInternal(userType)) ||
                    sbsCurrentUser.getUsername().equals(username)) {

                logService.log(sbsCurrentUser, "User " + username + " was viewed.");

                response.setUsername(sbsUserToView.getUsername());
                response.setUserType(sbsUserToView.getUserType());
                response.setName(sbsUserToView.getName());
                response.setEmail(sbsUserToView.getEmail());
                response.setAllowT1AccountAccess(sbsUserToView.getAllowT1AccountAccess());

                // PII is allowed if there are permissions or it is the current user
                if (sbsCurrentUser.getCanAccessPii() || sbsCurrentUser.getUsername().equals(username)) {
                    response.setPhoneNumber(sbsUserToView.getPhoneNumber());
                    response.setBillingAddr(sbsUserToView.getBillingAddr());
                    response.setMailingAddr(sbsUserToView.getMailingAddr());
                }

                response.setCanViewInternalUser(sbsUserToView.getCanViewInternalUser());
                response.setCanViewExternalUser(sbsUserToView.getCanViewExternalUser());
                response.setCanModifyInternalUser(sbsUserToView.getCanModifyInternalUser());
                response.setCanModifyExternalUser(sbsUserToView.getCanModifyExternalUser());
                response.setCanDeleteInternalUser(sbsUserToView.getCanDeleteInternalUser());
                response.setCanDeleteExternalUser(sbsUserToView.getCanDeleteExternalUser());
                response.setCanCreateInternalUser(sbsUserToView.getCanCreateInternalUser());
                response.setCanCreateExternalUser(sbsUserToView.getCanCreateExternalUser());
                response.setCanViewAccount(sbsUserToView.getCanViewAccount());
                response.setCanCreateAccount(sbsUserToView.getCanCreateAccount());
                response.setCanModifyAccount(sbsUserToView.getCanModifyAccount());
                response.setCanDeleteAccount(sbsUserToView.getCanDeleteAccount());
                response.setCanModifyT1AccountAccess(sbsUserToView.getCanModifyT1AccountAccess());
                response.setCanAuthorizeCritical(sbsUserToView.getCanAuthorizeCritical());
                response.setCanAuthorizeNonCritical(sbsUserToView.getCanAuthorizeNonCritical());
                response.setCanAccessLogs(sbsUserToView.getCanAccessLogs());
                response.setCanAccessPii(sbsUserToView.getCanAccessPii());
                response.setCanPerformTechAccess(sbsUserToView.getCanPerformTechAccess());
                response.setCanAuthorizeInternalModify(sbsUserToView.getCanModifyInternalUser());
                response.setCanAuthorizeExternalModify(sbsUserToView.getCanModifyExternalUser());
                response.setCanTransferCredit(sbsUserToView.getCanTransferCredit());

                response.setSuccess(true);
            } else {

                logService.log(sbsCurrentUser, "User " + username + " cannot be viewed from lack of permission.");

                response.setSuccess(false);
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> modifyUser(String username, ModifyUserRequest request) {
        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        SbsUser sbsUserToModify = userRepository.findByUsername(username);

        // construct API response
        ModifyUserResponse response = new ModifyUserResponse();

        if (sbsUserToModify == null) {
            // user does not exist
            logService.log(sbsCurrentUser, "User " + username + " to modify does not exist.");
            response.setModified(false);
            response.setRequested(false);

        } else {

            SbsUser modifyRequest = new SbsUser();

            modifyRequest.setModifyRequest(true);
            modifyRequest.setUsername(sbsUserToModify.getUsername());
            modifyRequest.setUserType(sbsUserToModify.getUserType());
            // perform modification
            modifyRequest.setName(request.getName() == null ? "" : request.getName());
            modifyRequest.setEmail(request.getEmail() == null ? "" : request.getEmail());
            modifyRequest.setPhoneNumber(request.getPhoneNumber() == null ? "" : request.getPhoneNumber());
            modifyRequest.setBillingAddr(request.getBillingAddr() == null ? "" : request.getBillingAddr());
            modifyRequest.setMailingAddr(request.getMailingAddr() == null ? "" : request.getMailingAddr());


            // grab the user type
            String userType = sbsUserToModify.getUserType();

            // check permissions
            if ((sbsCurrentUser.getCanModifyInternalUser() && isInternal(userType)) ||
                    (sbsCurrentUser.getCanModifyExternalUser() && isExternal(userType))) {

                // save the modified user
                userService.modifyUser(sbsUserToModify, modifyRequest);
                userRepository.save(sbsUserToModify);

                response.setRequested(false);
                response.setModified(true);
                logService.log(sbsCurrentUser, "User " + username + " was modified");
            } else if (sbsCurrentUser.getUsername().equals(username)) {
                // save the new modify request
                userRepository.save(modifyRequest);

                logService.log(sbsCurrentUser, "User " + username + " modify request saved");
                response.setRequested(true);
                response.setModified(false);
            } else {
                response.setRequested(false);
                response.setModified(false);
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> deleteUser(String username){
        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        SbsUser sbsUserToDelete = userRepository.findByUsername(username);

        DeleteUserResponse response = new DeleteUserResponse();

        if (sbsUserToDelete == null) {
            logService.log(sbsCurrentUser, "User " + username + " to delete does not exist.");
            response.setDeleted(false);
        } else {

            // find the user type to delete
            String userType = sbsUserToDelete.getUserType();

            // check the permissions
            if ((sbsCurrentUser.getCanDeleteInternalUser() && isInternal(userType)) ||
                    (sbsCurrentUser.getCanDeleteExternalUser() && isExternal(userType))) {

                // check if the user to delete exists

                userRepository.delete(sbsUserToDelete);
                response.setDeleted(true);
                logService.log(sbsCurrentUser, "User " + username + " deleted");

            } else {
                logService.log(sbsCurrentUser, "User " + username + " cannot be deleted because of lack of permission");
                response.setDeleted(false);
            }

        }

        // return the response
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ModelAndView downloadStatements() {
        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return null;
        }

        logService.log(sbsCurrentUser, "Banking statements downloaded.");
        // download own statements
        Set<SbsAccount> sbsAccountsSet = sbsCurrentUser.getSbsAccountList();
        List<SbsAccount> sbsAccountsList = new ArrayList<>();
        List<List<SbsTransaction>> sbsTransactions = new ArrayList<>();

        for (SbsAccount sbsAccount : sbsAccountsSet) {
            sbsAccountsList.add(sbsAccount);
            sbsTransactions.add(transactionRepository.findCompletedByAccount(sbsAccount.getAccountNumber()));
        }

        HashMap<String, Object> model = new HashMap<>();
        model.put("sbsUser", sbsCurrentUser);
        model.put("sbsAccounts", sbsAccountsList);
        model.put("sbsTransactions", sbsTransactions);
        return new ModelAndView(new StatementPdfView(), model);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> emailLogs() {
        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        EmailLogsResponse emailLogsResponse = new EmailLogsResponse();

        // check if we can access logs
        if (sbsCurrentUser.getCanAccessLogs()) {
            logService.log(sbsCurrentUser, "System logs are being emailed.");
            emailLogsResponse.setSuccess(true);
            logService.emailLogs(sbsCurrentUser);
        } else {
            logService.log(sbsCurrentUser, "Attempted access to system logs.");
            emailLogsResponse.setSuccess(false);
        }

        return new ResponseEntity<>(emailLogsResponse, HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void setAllowT1AccountAccess(boolean allow) {
        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return;
        }

        logService.log(sbsCurrentUser, "Tier1 account access allowed set to: " + allow);
        sbsCurrentUser.setAllowT1AccountAccess(allow);
        userRepository.save(sbsCurrentUser);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> setT1AccountAccess(SetT1AccountAccessRequest request) {
        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        SetT1AccountAccessResponse response = new SetT1AccountAccessResponse();

        // check if we are authorized
        if (!sbsCurrentUser.getCanModifyT1AccountAccess()) {
            // not authorized
            logService.log(sbsCurrentUser, "User not authorized");
            response.setSuccess(false);
            response.setUnauthorized(true);
            response.setDoesNotExist(false);
            response.setNotT1User(false);
        } else {
            SbsUser sbsT1UserToModify = userRepository.findByUsername(request.getUsername());

            // check if the username exists
            if (sbsT1UserToModify == null) {
                // authorized but does not exist
                logService.log(sbsCurrentUser, "User " + request.getUsername() + " does not exist.");
                response.setSuccess(false);
                response.setUnauthorized(false);
                response.setDoesNotExist(true);
                response.setNotT1User(false);
            } else if (!sbsT1UserToModify.getUserType().equals(Constants.USER_TIER1)) {
                logService.log(sbsCurrentUser, "User " + request.getUsername() + " cannot be modified because not tier1");
                response.setSuccess(false);
                response.setUnauthorized(false);
                response.setDoesNotExist(false);
                response.setNotT1User(true);
            } else {
                // authorized and exists

                sbsT1UserToModify.setCanViewAccount(request.getCanAccess());
                sbsT1UserToModify.setCanCreateAccount(request.getCanAccess());
                sbsT1UserToModify.setCanModifyAccount(request.getCanAccess());
                sbsT1UserToModify.setCanAuthorizeNonCritical(request.getCanAccess());

                userRepository.save(sbsT1UserToModify);
                logService.log(sbsCurrentUser, "User " + request.getUsername() + " modified");

                response.setSuccess(true);
                response.setUnauthorized(false);
                response.setDoesNotExist(false);
                response.setNotT1User(false);
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> viewModifyUsers() {
        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        ViewModifyUsersResponse response = new ViewModifyUsersResponse();

        List<SbsUser> sbsUserRequests = userRepository.findModifyRequests();

        for (SbsUser sbsUserRequest : sbsUserRequests) {
            String userType = sbsUserRequest.getUserType();

            if ((isInternal(userType) && sbsCurrentUser.getCanAuthorizeInternalModify()) ||
                    (isExternal(userType) && sbsCurrentUser.getCanAuthorizeExternalModify())) {

                UserRequestDetails userRequest = new UserRequestDetails();

                userRequest.setRequestId(sbsUserRequest.getId());
                userRequest.setUsername(sbsUserRequest.getUsername());
                userRequest.setName(sbsUserRequest.getName());
                userRequest.setEmail(sbsUserRequest.getEmail());
                userRequest.setPhoneNumber(sbsUserRequest.getPhoneNumber());
                userRequest.setBillingAddr(sbsUserRequest.getBillingAddr());
                userRequest.setMailingAddr(sbsUserRequest.getMailingAddr());

                response.getRequests().add(userRequest);
            }

        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> resolveModifyUsers(ResolveModifyUsersRequest request) {
        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        ResolveModifyUsersResponse response = new ResolveModifyUsersResponse();

        // get the user modify request
        SbsUser modifyRequest = userRepository.findModifyRequest(request.getModifyUserId());

        if (modifyRequest == null) {
            logService.log(sbsCurrentUser, "User modify request " + request.getModifyUserId() + " to resolve does not exist");
            response.setResolved(false);
        } else {
            String userType = modifyRequest.getUserType();

            if (!(isInternal(userType) && sbsCurrentUser.getCanAuthorizeInternalModify()) &&
                    !(isExternal(userType) && sbsCurrentUser.getCanAuthorizeExternalModify())) {
                logService.log(sbsCurrentUser, "User modify request " + request.getModifyUserId() + " cannot be resolved because of lack of permission");
                response.setResolved(false);
            } else {
                SbsUser sbsUserToModify = userRepository.findByUsername(modifyRequest.getUsername());

                if (sbsUserToModify == null) {
                    logService.log(sbsCurrentUser, "User " + modifyRequest.getUsername() + " does not exist for resolving modifying");
                    response.setResolved(false);
                } else {

                    // perform modification if it is being authorized
                    if (request.getAuthorize()) {
                        // perform modification
                        userService.modifyUser(sbsUserToModify, modifyRequest);
                        userRepository.save(sbsUserToModify);
                        logService.log(sbsCurrentUser,
                                "Modify Request " + request.getModifyUserId() +
                                        " for " + modifyRequest.getUsername() + " resolved");
                    }

                    // delete the user modify request
                    userRepository.delete(modifyRequest);

                    // the user modify request has been resolved (deleted)
                    response.setResolved(true);
                }
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> createAccount(String username, CreateAccountRequest request) {
        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        SbsUser sbsUserToCreateFor = userRepository.findByUsername(username);

        CreateAccountResponse response = new CreateAccountResponse();

        // check if the user to create the account for exists
        if (sbsUserToCreateFor == null) {
            logService.log(sbsCurrentUser, "Cannot create account because User " + username + " does not exist");
            response.setIsInvalidUser(false);
            response.setCreated(false);
        } else {

            String requestedAccountType = request.getAccountType();

            // credit account fields
            String userCVV = request.getCvv();
            Double userCreditLimit = request.getCreditLimit();
            String userCardNumber = request.getCardNumber();
            Double latePayment = request.getLatePayment();
            Double creditInterest =  request.getCreditInterest();

            boolean canCreate = sbsCurrentUser.getCanCreateAccount();
            if (canCreate && accountService.t1Control(sbsCurrentUser, sbsUserToCreateFor)) {

                if (sbsUserToCreateFor.getUserType().equals(Constants.USER_IND) ||
                        sbsUserToCreateFor.getUserType().equals(Constants.USER_ORG))
                {
                    if (requestedAccountType.equals(Constants.ACCOUNT_SAVINGS)) {
                        logService.log(sbsCurrentUser, "Savings Account for User " + username + " created.");
                        accountService.createSavings(username);
                        response.setCreated(true);
                    } else if (requestedAccountType.equals(Constants.ACCOUNT_CHECKING)){
                        logService.log(sbsCurrentUser, "Checking Account for User " + username + " created.");
                        accountService.createChecking(username);
                        response.setCreated(true);
                    } else {
                        if (userCardNumber == null || userCVV == null || userCreditLimit == null ||
                                latePayment == null || creditInterest == null) {
                            logService.log(sbsCurrentUser, "Credit account for user " + username + " not created because of null values.");
                            response.setCreated(false);
                        } else {
                            SbsAccount existingCreditAccount = accountRepository.findBycardNumber(request.getCardNumber());
                            if (existingCreditAccount == null) {
                                accountService.createCredit(username, userCardNumber, userCVV, userCreditLimit,latePayment,creditInterest);
                                logService.log(sbsCurrentUser,"Credit account for user " + username + " created.");
                                response.setCreated(true);

                            } else {
                                logService.log(sbsCurrentUser, "Card number already exists.");
                                response.setCreated(false);
                            }
                        }
                    }

                    response.setIsInvalidUser(false);
                } else {
                    logService.log(sbsCurrentUser, "Cannot create account because user " + username + " is not a valid user");
                    response.setIsInvalidUser(true);
                    response.setCreated(false);
                }
            } else {
                response.setIsInvalidUser(false);
                response.setCreated(false);
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> viewAccount(Long accountNumber) {
        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        SbsAccount accountToView = accountRepository.findByAccountNumber(accountNumber);

        ViewAccountResponse response = new ViewAccountResponse();

        if (accountToView == null) {
            logService.log(sbsCurrentUser,"Account " + accountNumber + " to view does not exist.");
            response.setSuccess(false);
        } else {
            if (sbsCurrentUser.getUsername().equals(accountToView.getSbsUser().getUsername()) ||
                    (sbsCurrentUser.getCanViewAccount() &&
                            accountService.t1Control(sbsCurrentUser, accountToView.getSbsUser()))) {
                response.setAccountBalance(accountToView.getAccountBalance());
                response.setAccountNumber(accountToView.getAccountNumber());
                response.setAccountType(accountToView.getAccountType());
                response.setCardNumber(accountToView.getCardNumber());
                response.setCreditLimit(accountToView.getCreditLimit());
                response.setCvv(accountToView.getCvv());
                response.setLatePayment(accountToView.getLatePayment());
                response.setCreditInterest(accountToView.getCreditInterest());
                logService.log(sbsCurrentUser, "Account " + accountNumber + " was viewed.");
                response.setSuccess(true);

            } else {
                logService.log(sbsCurrentUser, "Account " + accountNumber + " cannot be viewed because of lack of permission.");
                response.setSuccess(false);
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> viewAccounts(String username) {

        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        SbsUser sbsUserToView = userRepository.findByUsername(username);

        // construct response
        ViewAccountsResponse response = new ViewAccountsResponse();

        // check if the user to view exists
        if (sbsUserToView == null) {
            logService.log(sbsCurrentUser,"User " + username + " does not exist for viewing accounts.");
            response.setSuccess(false);
        } else {
            // check permissions
            if (sbsCurrentUser.getUsername().equals(username) ||
                    (sbsCurrentUser.getCanViewAccount() && accountService.t1Control(sbsCurrentUser, sbsUserToView))) {

                // get the list of accounts
                Set<SbsAccount> sbsAccounts = sbsUserToView.getSbsAccountList();

                // add all accounts to response
                for (SbsAccount sbsAccount : sbsAccounts) {
                    // construct new AccountDetails for response
                    AccountDetails details = new AccountDetails();
                    details.setAccountNumber(sbsAccount.getAccountNumber());

                    response.getAccounts().add(details);
                }
                logService.log(sbsCurrentUser,"User " + username + " viewed the accounts.");
                response.setSuccess(true);
            } else {
                logService.log(sbsCurrentUser, "User " + username + " cannot view the accounts because of lack of permission");
                response.setSuccess(false);
            }
        }

        // return the response
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> deleteAccount(Long accountNumber) {

        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        SbsAccount accountToDelete = accountRepository.findByAccountNumber(accountNumber);

        DeleteAccountResponse response = new DeleteAccountResponse();

        if (accountToDelete == null) {
            logService.log(sbsCurrentUser, "Account: " + accountNumber + " to delete does not exist.");
            response.setDeleted(false);
        } else {

            if (sbsCurrentUser.getCanDeleteAccount()) {
                SbsUser userWithAccountToDelete = accountToDelete.getSbsUser();
                userWithAccountToDelete.removeSbsAccount(accountToDelete);
                userRepository.save(userWithAccountToDelete);
                logService.log(sbsCurrentUser,"Account: " + accountNumber + " was deleted.");
                response.setDeleted(true);
            } else {
                logService.log(sbsCurrentUser, "Account: " + accountNumber + " cannot be deleted because of lack of permission.");
                response.setDeleted(false);
            }
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> modifyAccount(Long accountNumber, ModifyAccountRequest request) {

        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        SbsAccount sbsAccountToModify = accountRepository.findByAccountNumber(accountNumber);

        // construct API response
        ModifyAccountResponse response = new ModifyAccountResponse();

        if (sbsAccountToModify == null) {
            // account does not exist
            logService.log(sbsCurrentUser, "Account: " + accountNumber + " to modify does not exist.");
            response.setModified(false);

        } else {
            boolean canModify = sbsCurrentUser.getCanModifyAccount();
            if (canModify && accountService.t1Control(sbsCurrentUser, sbsAccountToModify.getSbsUser())) {

                if (request.getAccountBalance() != null)
                    sbsAccountToModify.setAccountBalance(request.getAccountBalance());

                // credit only modifications
                if (sbsAccountToModify.getAccountType().equals(Constants.ACCOUNT_CREDIT)) {
                    if (request.getCreditLimit() != null) {
                        sbsAccountToModify.setCreditLimit(request.getCreditLimit());
                    }
                    if(request.getLatePayment() != null){
                        sbsAccountToModify.setLatePayment(request.getLatePayment());
                    }
                    if(request.getCreditInterest() != null){
                        sbsAccountToModify.setCreditInterest(request.getCreditInterest());
                    }
                }

                // save the modified user
                accountRepository.save(sbsAccountToModify);
                logService.log(sbsCurrentUser, "Account: " + accountNumber + " was modified.");
                response.setModified(true);
            } else {
                logService.log(sbsCurrentUser,"Account: " + accountNumber + " cannot be modified because of lack of permission.");
                response.setModified(false);
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> transferFunds(CreateTransferRequest request) {

        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        CreateTransferResponse createTransferResponse = new CreateTransferResponse();

        boolean isCritical = false;

        SbsAccount fromAccount = accountRepository.findByAccountNumber(request.getSource());
        SbsAccount toAccount = accountRepository.findByAccountNumber(request.getDestination());
        Long fromAccountNumber = fromAccount == null ? null : fromAccount.getAccountNumber();
        Long toAccountNumber = toAccount == null ? null : toAccount.getAccountNumber();

        // check initial authorization
        if (transactionService.checkTransactionAuth(sbsCurrentUser, fromAccount, toAccount, request.getType())) {
            createTransferResponse.setUnauthorized(false);
            if (fromAccount != null)
                isCritical = transactionService.isCritical(fromAccount, request.getAmount());

            // check the transaction accounts and balances
            if (transactionService.checkTransaction(fromAccount, toAccount, request.getType(), request.getAmount())) {
                createTransferResponse.setBadAccounts(false);

                if (!isCritical || otpService.validateOtp(sbsCurrentUser, request.getOtp())) {
                    createTransferResponse.setCheckOtp(false);

                    SbsTransaction sbsTransaction;

                    // check if we have permission to complete the transaction
                    if ((!isCritical && sbsCurrentUser.getCanAuthorizeNonCritical() &&
                            (toAccount == null || accountService.t1Control(sbsCurrentUser, toAccount.getSbsUser())) &&
                            (fromAccount == null || accountService.t1Control(sbsCurrentUser, fromAccount.getSbsUser()))) ||
                            (isCritical && sbsCurrentUser.getCanAuthorizeCritical())) {

                        // transfers should be approved by the recipient so do not complete
                        if (request.getType().equals(Constants.TRANSACTION_TRANSFER)) {
                            sbsTransaction =
                                    transactionService.createTransaction(sbsCurrentUser.getUsername(), request.getType(),
                                            fromAccountNumber, toAccountNumber,
                                    request.getAmount(), isCritical, false, true, false);
                            createTransferResponse.setRequested(true);
                            createTransferResponse.setCompleted(false);
                            logService.log(sbsCurrentUser, "Successful " + request.getType() + " request placed.");
                        } else {
                            sbsTransaction =
                                    transactionService.createTransaction(sbsCurrentUser.getUsername(), request.getType(),
                                            fromAccountNumber, toAccountNumber,
                                    request.getAmount(), isCritical, false, false, true);
                            transactionService.authorizeTransaction(sbsTransaction);
                            createTransferResponse.setRequested(false);
                            createTransferResponse.setCompleted(true);
                            logService.log(sbsCurrentUser, "Request " + request.getType() + " processed successfully.");
                        }
                    } else {
                        sbsTransaction =
                                transactionService.createTransaction(sbsCurrentUser.getUsername(), request.getType(),
                                        fromAccountNumber, toAccountNumber,
                                request.getAmount(), isCritical, true,
                                (request.getType().equals(Constants.TRANSACTION_TRANSFER)), false);

                        createTransferResponse.setRequested(true);
                        createTransferResponse.setCompleted(false);
                        logService.log(sbsCurrentUser, "Successful " + request.getType() + " request placed");
                    }

                    // send an email notifying if critical
                    if (isCritical) transactionService.notifyCritical(
                            fromAccount, sbsTransaction.getTimestamp(), sbsTransaction.getAmount());

                } else {
                    otpService.createOtp(sbsCurrentUser);
                    System.out.println(sbsCurrentUser.getOtp());
                    createTransferResponse.setCheckOtp(true);
                    createTransferResponse.setCompleted(false);
                    createTransferResponse.setRequested(false);
                    logService.log(sbsCurrentUser,"OTP validation for critical fund transfer.");
                }
            }
            else
            {
                createTransferResponse.setBadAccounts(true);
                createTransferResponse.setCheckOtp(false);
                createTransferResponse.setCompleted(false);
                createTransferResponse.setRequested(false);
                logService.log(sbsCurrentUser, "Request " + request.getType() + " could not be processed because of insufficient funds.");
            }
        }
        else
        {
            createTransferResponse.setUnauthorized(true);
            createTransferResponse.setBadAccounts(false);
            createTransferResponse.setCheckOtp(false);
            createTransferResponse.setCompleted(false);
            createTransferResponse.setRequested(false);
            logService.log(sbsCurrentUser, "Request " + request.getType() + " could not be processed because of lack of authorization.");
        }

        return new ResponseEntity<>(createTransferResponse, HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> transferEmail(EmailTransferRequest request) {
        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        CreateTransferRequest createTransferRequest = new CreateTransferRequest();
        CreateTransferResponse transferResponse = new CreateTransferResponse();

        List<SbsUser> sbsUsers = userRepository.getUsersByEmail(request.getEmail());

        if (sbsUsers.size() == 1) {
            SbsAccount sbsToAccount = null;

            // try to find checking
            if (sbsToAccount == null) {
                for (SbsAccount sbsAccount : sbsUsers.get(0).getSbsAccountList()) {
                    if (sbsAccount.getAccountType().equals(Constants.ACCOUNT_CHECKING)) {
                        sbsToAccount = sbsAccount;
                        break;
                    }
                }
            }

            // try to find savings
            if (sbsToAccount == null) {
                for (SbsAccount sbsAccount : sbsUsers.get(0).getSbsAccountList()) {
                    if (sbsAccount.getAccountType().equals(Constants.ACCOUNT_SAVINGS)) {
                        sbsToAccount = sbsAccount;
                        break;
                    }
                }
            }

            // try to find credit
            if (sbsToAccount == null) {
                for (SbsAccount sbsAccount : sbsUsers.get(0).getSbsAccountList()) {
                    if (sbsAccount.getAccountType().equals(Constants.ACCOUNT_CREDIT)) {
                        sbsToAccount = sbsAccount;
                        break;
                    }
                }
            }

            if (sbsToAccount != null) {
                createTransferRequest.setSource(request.getFrom());
                createTransferRequest.setDestination(sbsToAccount.getAccountNumber());
                createTransferRequest.setAmount(request.getAmount());
                createTransferRequest.setOtp(request.getOtp());
                createTransferRequest.setType(Constants.TRANSACTION_TRANSFER);

                // call transferFunds
                logService.log(sbsCurrentUser,"Request for transferring funds to " + sbsToAccount.getAccountNumber() + " placed successfully.");
                return transferFunds(createTransferRequest);


            }
            else
            {
                transferResponse.setBadAccounts(true);
                transferResponse.setCheckOtp(false);
                transferResponse.setCompleted(false);
                transferResponse.setRequested(false);
                transferResponse.setUnauthorized(false);
                logService.log(sbsCurrentUser,"Request for transferring funds to the given email address failed because no account exists. ");
            }

        } else {
            // place some response
            transferResponse.setBadAccounts(true);
            transferResponse.setCheckOtp(false);
            transferResponse.setCompleted(false);
            transferResponse.setRequested(false);
            transferResponse.setUnauthorized(false);
            logService.log(sbsCurrentUser, "Request for transferring funds failed because multiple users with same email address.");
        }

        return new ResponseEntity<>(transferResponse, HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> resolveTransaction(ResolveTransferRequest request)
    {

        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        ResolveTransferResponse response = new ResolveTransferResponse();

        // check if we have authorization
        SbsTransaction sbsTransaction = transactionRepository.findOne(request.getTransactionNumber());

        if (sbsTransaction != null && !sbsTransaction.getCompleted()) {

            boolean access = false;

            // try to take away hold internal
            boolean isCritical = sbsTransaction.getCritical();

            SbsAccount toAccount = accountRepository.findByAccountNumber(sbsTransaction.getToAccount());
            SbsAccount fromAccount = accountRepository.findByAccountNumber(sbsTransaction.getFromAccount());

            if ((!isCritical && sbsCurrentUser.getCanAuthorizeNonCritical() &&
                    (toAccount == null || accountService.t1Control(sbsCurrentUser, toAccount.getSbsUser())) &&
                    (fromAccount == null || accountService.t1Control(sbsCurrentUser, fromAccount.getSbsUser()))) ||
                (isCritical && sbsCurrentUser.getCanAuthorizeCritical())) {

                sbsTransaction.setHoldInternal(false);
                access = true;
            }

            // try to take away hold transfer
            if (!sbsTransaction.getHoldInternal() && sbsTransaction.getHoldTransfer() && toAccount != null &&
                    toAccount.getSbsUser().getUsername().equals(sbsCurrentUser.getUsername())) {
                sbsTransaction.setHoldTransfer(false);
                access = true;
            }

            // try to complete the transaction
            if (access) {
                response.setAccessed(true);

                if (!request.isAuthorize()) {
                    transactionRepository.delete(sbsTransaction);
                    response.setResolved(true);
                    logService.log(sbsCurrentUser, "Transaction for user " + sbsTransaction.getUsername() + " declined.");

                } else if (!sbsTransaction.getHoldInternal() && !sbsTransaction.getHoldTransfer()) {
                    transactionService.authorizeTransaction(sbsTransaction);
                    sbsTransaction.setCompleted(true);
                    response.setResolved(true);
                    transactionRepository.save(sbsTransaction);
                    logService.log(sbsCurrentUser,"Transaction for user " + sbsTransaction.getUsername() + " completed.");

                } else {
                    response.setResolved(false);
                    logService.log(sbsCurrentUser,"External User's authorization pending.");
                }
            } else {
                response.setAccessed(false);
                response.setResolved(false);
            }
        }
        else {
            response.setAccessed(false);
            response.setResolved(false);
            logService.log(sbsCurrentUser,"Transaction to resolve does not exist.");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> viewTransactions() {

        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<SbsTransaction> sbsTransactions = transactionRepository.findAll();
        ViewTransactionsResponse response = new ViewTransactionsResponse();

        for (SbsTransaction sbsTransaction : sbsTransactions)
        {
            if (!sbsTransaction.getCompleted()) {

                boolean flag = false;
                // check for access to hold internal
                boolean isCritical = sbsTransaction.getCritical();

                SbsAccount toAccount = accountRepository.findByAccountNumber(sbsTransaction.getToAccount());
                SbsAccount fromAccount = accountRepository.findByAccountNumber(sbsTransaction.getFromAccount());

                if (sbsTransaction.getHoldInternal() &&
                        ((!isCritical && sbsCurrentUser.getCanAuthorizeNonCritical() &&
                            (toAccount == null || accountService.t1Control(sbsCurrentUser, toAccount.getSbsUser())) &&
                            (fromAccount == null || accountService.t1Control(sbsCurrentUser, fromAccount.getSbsUser()))) ||
                        (isCritical && sbsCurrentUser.getCanAuthorizeCritical()))) {
                    flag = true;
                }

                // check for access to hold transfer
                if (!sbsTransaction.getHoldInternal() && sbsTransaction.getHoldTransfer() && toAccount != null &&
                        toAccount.getSbsUser().getUsername().equals(sbsCurrentUser.getUsername())) {
                    flag = true;
                }

                if (flag) {
                    TransactionDetails transactionDetails = new TransactionDetails();
                    transactionDetails.setUsername(sbsTransaction.getUsername());
                    transactionDetails.setAmount(sbsTransaction.getAmount());
                    transactionDetails.setTime(sbsTransaction.getTimestamp());
                    transactionDetails.setTransactionId(sbsTransaction.getId());
                    transactionDetails.setType(sbsTransaction.getType());
                    if(sbsTransaction.getFromAccount() != null)
                        transactionDetails.setFromAccountNumber(sbsTransaction.getFromAccount());
                    if(sbsTransaction.getToAccount() != null)
                        transactionDetails.setToAccountNumber(sbsTransaction.getToAccount());

                    response.getTransactions().add(transactionDetails);
                    logService.log(sbsCurrentUser, "Transaction with ID " + sbsTransaction.getId() + " was viewed.");
                }
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Object> transferCredit(CreditTransferRequest request) {

        SbsUser sbsCurrentUser = getCurrentUser();
        if (sbsCurrentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        CreateTransferResponse response = new CreateTransferResponse();

        // this API never completes a transaction or sends OTP
        response.setCompleted(false);
        response.setCheckOtp(false);

        // check permission to create credit transaction transfer request
        if (sbsCurrentUser.getCanTransferCredit()) {
            response.setUnauthorized(false);

            // grab the from and to accounts
            SbsAccount sbsFromAccount = accountRepository.findBycardNumber(request.getCardNumber());
            SbsAccount sbsToAccount = accountRepository.findByAccountNumber(request.getTo());

            // check that account information is valid
            if (transactionService.checkTransaction(sbsFromAccount, sbsToAccount, Constants.TRANSACTION_TRANSFER, request.getAmount()) &&
                    sbsFromAccount.getCvv() != null &&
                    sbsFromAccount.getCvv().equals(request.getCvv())) {

                response.setBadAccounts(false);
                response.setRequested(true);

                // add a transfer request for internal authorization
                transactionService.createTransaction(sbsCurrentUser.getUsername(), Constants.TRANSACTION_TRANSFER,
                        sbsFromAccount.getAccountNumber(), sbsToAccount.getAccountNumber(),
                        request.getAmount(), false, true, false, false);
                logService.log(sbsCurrentUser,"Transfer Credit Request placed successfully.");
            } else {
                response.setBadAccounts(true);
                response.setRequested(false);
                logService.log(sbsCurrentUser,"CVV invalid or insufficient funds.");
            }

        } else {
            response.setUnauthorized(true);
            response.setBadAccounts(false);
            response.setRequested(false);
            logService.log(sbsCurrentUser,"Transfer Credit failed because of lack of permission.");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
