package org.asu.group8.controller;

import org.asu.group8.model.*;
import org.asu.group8.service.BankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/* BankingSystem
 * This class holds the API endpoints for the Secure Banking System. These endpoints call functions defined in the
 * BankingService.
 *
 */

@RestController
@RequestMapping("/api")
public class BankingSystem {

    @Autowired
    private BankingService bankingService;

    // root check to see if the API is running
    @RequestMapping("/")
    public String root() {
        return "Secure Banking System is ready!";
    }

    // login to the Secure Banking System
    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @Validated @RequestBody LoginRequest request, HttpServletResponse response,
            @CookieValue(name = "sbs_device_code", defaultValue = "") String sbsDeviceCode) {

        return bankingService.login(request, response, sbsDeviceCode);
    }

    // technical access login to the system
    @PostMapping("/login_tech")
    public ResponseEntity<Object> techLogin(
            @Validated @RequestBody TechLoginRequest request) {
        return bankingService.techLogin(request);
    }

    @GetMapping("/misc/statements")
    public ModelAndView downloadStatements() {
        return bankingService.downloadStatements();
    }

    @GetMapping("/misc/logs")
    public ResponseEntity<Object> emailLogs() {
        return bankingService.emailLogs();
    }

    @PutMapping("/misc/set_allow_t1_account_access")
    public void setAllowT1AccountAccess(
            @Validated @RequestBody SetAllowT1AccountAccessRequest request) {
        bankingService.setAllowT1AccountAccess(request.getAllowed());
    }

    @PutMapping("/misc/set_t1_account_access")
    public ResponseEntity<Object> setT1AccountAccess(
            @Validated @RequestBody SetT1AccountAccessRequest setT1AccountAccessRequest) {
        return bankingService.setT1AccountAccess(setT1AccountAccessRequest);
    }

    @PutMapping("misc/advice_test")
    public void adviceTest() throws Exception{
        throw new NullPointerException();
    }

    // gets the current username
    @GetMapping("/username")
    public String username() {
        return bankingService.username();
    }


    @PostMapping("/user/create")
    public ResponseEntity<Object> createUser(
            @Validated @RequestBody CreateUserRequest request) {
        return bankingService.createUser(request);
    }

    @GetMapping("/user/view/{username}")
    public ResponseEntity<Object> viewUser(
            @Validated @PathVariable("username") String username) {
        return bankingService.viewUser(username);
    }

    @GetMapping("/user/view")
    public ResponseEntity<Object> viewUser() {
        return bankingService.viewUser(null);
    }

    @PutMapping("/user/modify/{username}")
    public ResponseEntity<Object> modifyUser(
            @Validated @PathVariable("username") String username,
            @Validated @RequestBody ModifyUserRequest request) {
        return bankingService.modifyUser(username, request);
    }

    @PutMapping("/user/delete/{username}")
    public ResponseEntity<Object> deleteUser(
            @Validated @PathVariable("username") String username){
        return bankingService.deleteUser(username);
    }

    @GetMapping("/user/request/view")
    public ResponseEntity<Object> viewModifyUsers() {
        return bankingService.viewModifyUsers();
    }

    @PutMapping("/user/request/resolve")
    public ResponseEntity<Object> resolveModifyUsers(
            @Validated @RequestBody ResolveModifyUsersRequest request) {
        return bankingService.resolveModifyUsers(request);
    }

    @PostMapping("/account/create/{username}")
    public ResponseEntity<Object> createAccount(
            @Validated @PathVariable("username") String username,
            @Validated @RequestBody CreateAccountRequest request) {
        return bankingService.createAccount(username,request);
    }

    @GetMapping("/account/view_all/{username}")
    public ResponseEntity<Object> viewAccounts(
            @Validated @Pattern(regexp = "^[a-zA-Z0-9]{3,40}$") @PathVariable("username") String username) {
        return bankingService.viewAccounts(username);
    }

    @GetMapping("account/view/{account_number}")
    public ResponseEntity<Object> viewAccount(
            @Validated @Min(0) @PathVariable("account_number") Long accountNumber){
        return bankingService.viewAccount(accountNumber);
    }

    @PutMapping("account/delete/{account_number}")
    public ResponseEntity<Object> deleteAccount(
            @Validated @Min(0) @PathVariable("account_number") Long accountNumber){
        return bankingService.deleteAccount(accountNumber);
    }

    @PutMapping("account/modify/{account_number}")
    public ResponseEntity<Object> modifyAccount(
            @Validated @Min(0) @PathVariable("account_number") Long accountNumber,
            @Validated @RequestBody ModifyAccountRequest request){
        return bankingService.modifyAccount(accountNumber,request);
    }

    @PostMapping("/account/transfer")
    public ResponseEntity<Object> transferFunds(
            @Validated @RequestBody CreateTransferRequest request) {
        return bankingService.transferFunds(request);
    }

    @PostMapping("/account/transfer/email")
    public ResponseEntity<Object> transferEmail(
            @Validated @RequestBody EmailTransferRequest request) {
        return bankingService.transferEmail(request);
    }

    @PostMapping("/account/transfer/credit")
    public ResponseEntity<Object> transferCredit(
            @Validated @RequestBody CreditTransferRequest request) {
        return bankingService.transferCredit(request);
    }

    @GetMapping("/transaction/view")
    public ResponseEntity<Object> viewTransactions() {
        return bankingService.viewTransactions();
    }

    @PutMapping("/transaction/resolve")
    public ResponseEntity<Object> resolveTransaction(
            @Validated @RequestBody ResolveTransferRequest request) {
        return bankingService.resolveTransaction(request);
    }

}
