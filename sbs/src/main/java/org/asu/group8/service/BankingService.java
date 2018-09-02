package org.asu.group8.service;

import org.asu.group8.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

public interface BankingService {

    // loginRequest with the specified loginRequest credentials
    ResponseEntity<Object> login(LoginRequest request, HttpServletResponse response, String sbsDeviceCode);

    ResponseEntity<Object> techLogin(TechLoginRequest request);

    String username();

    ResponseEntity<Object> createUser(CreateUserRequest request);

    ResponseEntity<Object> viewUser(String username);

    ResponseEntity<Object> modifyUser(String username, ModifyUserRequest request);

    ResponseEntity<Object> deleteUser(String username);

    ModelAndView downloadStatements();

    ResponseEntity<Object> emailLogs();

    void setAllowT1AccountAccess(boolean allow);

    ResponseEntity<Object> setT1AccountAccess(SetT1AccountAccessRequest setT1AccountAccessRequest);

    ResponseEntity<Object> viewModifyUsers();

    ResponseEntity<Object> resolveModifyUsers(ResolveModifyUsersRequest request);

    ResponseEntity<Object> createAccount(String username, CreateAccountRequest request);

    ResponseEntity<Object> viewAccount(Long accountNumber);

    ResponseEntity<Object> viewAccounts(String username);

    ResponseEntity<Object> deleteAccount(Long accountNumber);

    ResponseEntity<Object> modifyAccount(Long accountNumber, ModifyAccountRequest request);

    ResponseEntity<Object> viewTransactions();

    ResponseEntity<Object> resolveTransaction(ResolveTransferRequest request);

    ResponseEntity<Object> transferFunds(CreateTransferRequest request);

    ResponseEntity<Object> transferEmail(EmailTransferRequest request);

    ResponseEntity<Object> transferCredit(CreditTransferRequest request);

}
