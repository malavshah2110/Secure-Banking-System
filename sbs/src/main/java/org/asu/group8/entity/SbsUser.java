package org.asu.group8.entity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "sbs_user")
public class SbsUser {

    public SbsUser() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // information
    private String username;                    // not PII (cannot be changed after added to table)
    private String userType;                    // not PII (cannot be changed after added to table)
    private String name;                        // not PII
    private String email;                       // not PII
    private Boolean allowT1AccountAccess;       // not PII

    private String phoneNumber;             // PII
    private String billingAddr;             // PII
    private String mailingAddr;             // PII

    private String password;                // cannot be changed after added to table
    private Boolean modifyRequest;        // cannot be changed after added to table

    // One-Time Password and otpExp
    private String otp;
    private Date otpExp;

    // API request limit
    private Date reqDate;
    private Integer reqCount;

    // link to accounts
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "sbsUser")
    private Set<SbsAccount> sbsAccountList = new HashSet<SbsAccount>();

    // link to trusted devices
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "sbsUser")
    private Set<SbsDevice> sbsDeviceList = new HashSet<>();

    // permissions

    // user access
    private Boolean canViewInternalUser;
    private Boolean canViewExternalUser;
    private Boolean canModifyInternalUser;
    private Boolean canModifyExternalUser;
    private Boolean canDeleteInternalUser;
    private Boolean canDeleteExternalUser;
    private Boolean canCreateInternalUser;
    private Boolean canCreateExternalUser;

    // accounts for external users
    private Boolean canViewAccount;
    private Boolean canCreateAccount;
    private Boolean canModifyAccount;
    private Boolean canDeleteAccount;
    private Boolean canModifyT1AccountAccess;       // modifying tier1 ability to change accounts

    // authorizing transactions
    private Boolean canAuthorizeCritical;
    private Boolean canAuthorizeNonCritical;

    // miscellaneous access
    private Boolean canAccessLogs;
    private Boolean canAccessPii;
    private Boolean canPerformTechAccess;

    // authorizing modify user requests
    private Boolean canAuthorizeInternalModify;
    private Boolean canAuthorizeExternalModify;

    // transferring using credit card number and CVV
    private Boolean canTransferCredit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<SbsAccount> getSbsAccountList() {
        return sbsAccountList;
    }

    public void setSbsAccountList(Set<SbsAccount> sbsAccountList) {
        this.sbsAccountList = sbsAccountList;
    }

    public void addSbsAccount(SbsAccount sbsAccount) {
        getSbsAccountList().add(sbsAccount);
        sbsAccount.setSbsUser(this);
    }

    void addSbsAccount(SbsAccount sbsAccount, boolean set) {
        if (sbsAccount != null) {
            if (!getSbsAccountList().contains(sbsAccount)) {
                getSbsAccountList().add(sbsAccount);
            }
            if (set) {
                sbsAccount.setSbsUser(this, false);
            }
        }
    }

    public void removeSbsAccount(SbsAccount sbsAccount) {
        getSbsAccountList().remove(sbsAccount);
        sbsAccount.setSbsUser(null);
    }

    public Set<SbsDevice> getSbsDeviceList() {
        return sbsDeviceList;
    }

    public void setSbsDeviceList(Set<SbsDevice> sbsDeviceList) {
        this.sbsDeviceList = sbsDeviceList;
    }

    public void addSbsDevice(SbsDevice sbsDevice) {
        getSbsDeviceList().add(sbsDevice);
        sbsDevice.setSbsUser(this);
    }

    void addSbsDevice(SbsDevice sbsDevice, boolean set) {
        if (sbsDevice != null) {
            if (!getSbsDeviceList().contains(sbsDevice)) {
                getSbsDeviceList().add(sbsDevice);
            }
            if (set) {
                sbsDevice.setSbsUser(this, false);
            }
        }
    }

    public void removeSbsDevice(SbsDevice sbsDevice) {
        getSbsDeviceList().remove(sbsDevice);
        sbsDevice.setSbsUser(null);
    }

    public String getName() {
        return name;
    }

    public Boolean getModifyRequest() {
        return modifyRequest;
    }

    public void setModifyRequest(Boolean modifyRequest) {
        this.modifyRequest = modifyRequest;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBillingAddr() {
        return billingAddr;
    }

    public void setBillingAddr(String billingAddr) {
        this.billingAddr = billingAddr;
    }

    public String getMailingAddr() {
        return mailingAddr;
    }

    public void setMailingAddr(String mailingAddr) {
        this.mailingAddr = mailingAddr;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getCanViewInternalUser() {
        return canViewInternalUser;
    }

    public void setCanViewInternalUser(Boolean canViewInternalUser) {
        this.canViewInternalUser = canViewInternalUser;
    }

    public Boolean getCanViewExternalUser() {
        return canViewExternalUser;
    }

    public void setCanViewExternalUser(Boolean canViewExternalUser) {
        this.canViewExternalUser = canViewExternalUser;
    }

    public Boolean getCanModifyInternalUser() {
        return canModifyInternalUser;
    }

    public void setCanModifyInternalUser(Boolean canModifyInternalUser) {
        this.canModifyInternalUser = canModifyInternalUser;
    }

    public Boolean getCanModifyExternalUser() {
        return canModifyExternalUser;
    }

    public void setCanModifyExternalUser(Boolean canModifyExternalUser) {
        this.canModifyExternalUser = canModifyExternalUser;
    }

    public Boolean getCanDeleteInternalUser() {
        return canDeleteInternalUser;
    }

    public void setCanDeleteInternalUser(Boolean canDeleteInternalUser) {
        this.canDeleteInternalUser = canDeleteInternalUser;
    }

    public Boolean getCanDeleteExternalUser() {
        return canDeleteExternalUser;
    }

    public void setCanDeleteExternalUser(Boolean canDeleteExternalUser) {
        this.canDeleteExternalUser = canDeleteExternalUser;
    }

    public Boolean getCanCreateInternalUser() {
        return canCreateInternalUser;
    }

    public void setCanCreateInternalUser(Boolean canCreateInternalUser) {
        this.canCreateInternalUser = canCreateInternalUser;
    }

    public Boolean getCanCreateExternalUser() {
        return canCreateExternalUser;
    }

    public void setCanCreateExternalUser(Boolean canCreateExternalUser) {
        this.canCreateExternalUser = canCreateExternalUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Boolean getCanPerformTechAccess() {
        return canPerformTechAccess;
    }

    public void setCanPerformTechAccess(Boolean canPerformTechAccess) {
        this.canPerformTechAccess = canPerformTechAccess;
    }

    public Boolean getCanViewAccount() {
        return canViewAccount;
    }

    public void setCanViewAccount(Boolean canViewAccount) {
        this.canViewAccount = canViewAccount;
    }

    public Boolean getCanCreateAccount() {
        return canCreateAccount;
    }

    public void setCanCreateAccount(Boolean canCreateAccount) {
        this.canCreateAccount = canCreateAccount;
    }

    public Boolean getCanModifyAccount() {
        return canModifyAccount;
    }

    public void setCanModifyAccount(Boolean canModifyAccount) {
        this.canModifyAccount = canModifyAccount;
    }

    public Boolean getCanDeleteAccount() {
        return canDeleteAccount;
    }

    public void setCanDeleteAccount(Boolean canDeleteAccount) {
        this.canDeleteAccount = canDeleteAccount;
    }

    public Boolean getAllowT1AccountAccess() {
        return allowT1AccountAccess;
    }

    public void setAllowT1AccountAccess(Boolean allowT1AccountAccess) {
        this.allowT1AccountAccess = allowT1AccountAccess;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Date getOtpExp() {
        return otpExp;
    }

    public void setOtpExp(Date otpExp) {
        this.otpExp = otpExp;
    }

    public Boolean getCanAccessLogs() {
        return canAccessLogs;
    }

    public void setCanAccessLogs(Boolean canAccessLogs) {
        this.canAccessLogs = canAccessLogs;
    }

    public Boolean getCanAuthorizeCritical() {
        return canAuthorizeCritical;
    }

    public void setCanAuthorizeCritical(Boolean canAuthorizeCritical) {
        this.canAuthorizeCritical = canAuthorizeCritical;
    }

    public Boolean getCanAuthorizeNonCritical() {
        return canAuthorizeNonCritical;
    }

    public void setCanAuthorizeNonCritical(Boolean canAuthorizeNonCritical) {
        this.canAuthorizeNonCritical = canAuthorizeNonCritical;
    }

    public Boolean getCanAccessPii() {
        return canAccessPii;
    }

    public void setCanAccessPii(Boolean canAccessPii) {
        this.canAccessPii = canAccessPii;
    }

    public Boolean getCanModifyT1AccountAccess() {
        return canModifyT1AccountAccess;
    }

    public void setCanModifyT1AccountAccess(Boolean canModifyT1AccountAccess) {
        this.canModifyT1AccountAccess = canModifyT1AccountAccess;
    }

    public Boolean getCanAuthorizeInternalModify() {
        return canAuthorizeInternalModify;
    }

    public void setCanAuthorizeInternalModify(Boolean canAuthorizeInternalModify) {
        this.canAuthorizeInternalModify = canAuthorizeInternalModify;
    }

    public Boolean getCanAuthorizeExternalModify() {
        return canAuthorizeExternalModify;
    }

    public void setCanAuthorizeExternalModify(Boolean canAuthorizeExternalModify) {
        this.canAuthorizeExternalModify = canAuthorizeExternalModify;
    }

    public Boolean getCanTransferCredit() {
        return canTransferCredit;
    }

    public void setCanTransferCredit(Boolean canTransferCredit) {
        this.canTransferCredit = canTransferCredit;
    }

    public Date getReqDate() {
        return reqDate;
    }

    public void setReqDate(Date reqDate) {
        this.reqDate = reqDate;
    }

    public Integer getReqCount() {
        return reqCount;
    }

    public void setReqCount(Integer reqCount) {
        this.reqCount = reqCount;
    }
}
