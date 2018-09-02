package org.asu.group8.impl;

import org.asu.group8.config.Constants;
import org.asu.group8.entity.SbsUser;
import org.asu.group8.repo.UserRepository;
import org.asu.group8.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean createAdmin(String username, String name, String email, String phoneNumber, String billingAddr, String mailingAddr, String password) {
        if (userRepository.findByUsername(username) == null) {
            SbsUser sbsUser = new SbsUser();
            sbsUser.setUsername(username);
            sbsUser.setUserType(Constants.USER_ADMIN);
            sbsUser.setName(name);
            sbsUser.setEmail(email);
            sbsUser.setPhoneNumber(phoneNumber);
            sbsUser.setBillingAddr(billingAddr);
            sbsUser.setMailingAddr(mailingAddr);
            sbsUser.setPassword(bCryptPasswordEncoder.encode(password));
            sbsUser.setModifyRequest(false);
            sbsUser.setAllowT1AccountAccess(false);

            sbsUser.setCanCreateExternalUser(true);
            sbsUser.setCanCreateInternalUser(true);
            sbsUser.setCanDeleteExternalUser(true);
            sbsUser.setCanDeleteInternalUser(true);
            sbsUser.setCanModifyExternalUser(true);
            sbsUser.setCanModifyInternalUser(true);
            sbsUser.setCanViewExternalUser(true);
            sbsUser.setCanViewInternalUser(true);

            sbsUser.setCanPerformTechAccess(true);

            sbsUser.setCanViewAccount(true);
            sbsUser.setCanCreateAccount(true);
            sbsUser.setCanModifyAccount(true);
            sbsUser.setCanDeleteAccount(true);
            sbsUser.setCanModifyT1AccountAccess(true);

            sbsUser.setCanAuthorizeCritical(true);
            sbsUser.setCanAuthorizeNonCritical(true);

            sbsUser.setCanAccessLogs(true);
            sbsUser.setCanAccessPii(true);

            sbsUser.setCanAuthorizeExternalModify(true);
            sbsUser.setCanAuthorizeInternalModify(true);

            sbsUser.setCanTransferCredit(false);

            sbsUser.setReqCount(0);
            sbsUser.setReqDate(new Date());

            userRepository.save(sbsUser);

            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean createTier1(String username, String name, String email, String phoneNumber, String billingAddr, String mailingAddr, String password) {
        if (userRepository.findByUsername(username) == null) {
            SbsUser sbsUser = new SbsUser();
            sbsUser.setUsername(username);
            sbsUser.setUserType(Constants.USER_TIER1);
            sbsUser.setName(name);
            sbsUser.setEmail(email);
            sbsUser.setPhoneNumber(phoneNumber);
            sbsUser.setBillingAddr(billingAddr);
            sbsUser.setMailingAddr(mailingAddr);
            sbsUser.setPassword(bCryptPasswordEncoder.encode(password));
            sbsUser.setModifyRequest(false);
            sbsUser.setAllowT1AccountAccess(false);

            sbsUser.setCanCreateExternalUser(false);
            sbsUser.setCanCreateInternalUser(false);
            sbsUser.setCanDeleteExternalUser(false);
            sbsUser.setCanDeleteInternalUser(false);
            sbsUser.setCanModifyExternalUser(false);
            sbsUser.setCanModifyInternalUser(false);
            sbsUser.setCanViewExternalUser(false);
            sbsUser.setCanViewInternalUser(false);

            sbsUser.setCanPerformTechAccess(false);

            sbsUser.setCanViewAccount(false);
            sbsUser.setCanCreateAccount(false);
            sbsUser.setCanModifyAccount(false);
            sbsUser.setCanDeleteAccount(false);
            sbsUser.setCanModifyT1AccountAccess(false);

            sbsUser.setCanAuthorizeCritical(false);
            sbsUser.setCanAuthorizeNonCritical(false);

            sbsUser.setCanAccessLogs(false);
            sbsUser.setCanAccessPii(false);

            sbsUser.setCanAuthorizeExternalModify(true);
            sbsUser.setCanAuthorizeInternalModify(false);

            sbsUser.setCanTransferCredit(false);

            sbsUser.setReqCount(0);
            sbsUser.setReqDate(new Date());

            userRepository.save(sbsUser);

            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean createTier2(String username, String name, String email, String phoneNumber, String billingAddr, String mailingAddr, String password) {
        if (userRepository.findByUsername(username) == null) {
            SbsUser sbsUser = new SbsUser();
            sbsUser.setUsername(username);
            sbsUser.setUserType(Constants.USER_TIER2);
            sbsUser.setName(name);
            sbsUser.setEmail(email);
            sbsUser.setPhoneNumber(phoneNumber);
            sbsUser.setBillingAddr(billingAddr);
            sbsUser.setMailingAddr(mailingAddr);
            sbsUser.setPassword(bCryptPasswordEncoder.encode(password));
            sbsUser.setModifyRequest(false);
            sbsUser.setAllowT1AccountAccess(false);

            sbsUser.setCanCreateExternalUser(true);
            sbsUser.setCanCreateInternalUser(false);
            sbsUser.setCanDeleteExternalUser(true);
            sbsUser.setCanDeleteInternalUser(false);
            sbsUser.setCanModifyExternalUser(true);
            sbsUser.setCanModifyInternalUser(true);
            sbsUser.setCanViewExternalUser(true);
            sbsUser.setCanViewInternalUser(true);

            sbsUser.setCanPerformTechAccess(false);

            sbsUser.setCanViewAccount(true);
            sbsUser.setCanCreateAccount(true);
            sbsUser.setCanModifyAccount(true);
            sbsUser.setCanDeleteAccount(true);
            sbsUser.setCanModifyT1AccountAccess(true);

            sbsUser.setCanAuthorizeCritical(true);
            sbsUser.setCanAuthorizeNonCritical(true);

            sbsUser.setCanAccessLogs(false);
            sbsUser.setCanAccessPii(false);

            sbsUser.setCanAuthorizeExternalModify(true);
            sbsUser.setCanAuthorizeInternalModify(false);

            sbsUser.setCanTransferCredit(false);

            sbsUser.setReqCount(0);
            sbsUser.setReqDate(new Date());

            userRepository.save(sbsUser);

            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean createInd(String username, String name, String email, String phoneNumber, String billingAddr, String mailingAddr, String password) {
        if (userRepository.findByUsername(username) == null) {
            SbsUser sbsUser = new SbsUser();
            sbsUser.setUsername(username);
            sbsUser.setUserType(Constants.USER_IND);
            sbsUser.setName(name);
            sbsUser.setEmail(email);
            sbsUser.setPhoneNumber(phoneNumber);
            sbsUser.setBillingAddr(billingAddr);
            sbsUser.setMailingAddr(mailingAddr);
            sbsUser.setPassword(bCryptPasswordEncoder.encode(password));
            sbsUser.setModifyRequest(false);
            sbsUser.setAllowT1AccountAccess(false);

            sbsUser.setCanCreateExternalUser(false);
            sbsUser.setCanCreateInternalUser(false);
            sbsUser.setCanDeleteExternalUser(false);
            sbsUser.setCanDeleteInternalUser(false);
            sbsUser.setCanModifyExternalUser(false);
            sbsUser.setCanModifyInternalUser(false);
            sbsUser.setCanViewExternalUser(false);
            sbsUser.setCanViewInternalUser(false);

            sbsUser.setCanPerformTechAccess(false);

            sbsUser.setCanViewAccount(false);
            sbsUser.setCanCreateAccount(false);
            sbsUser.setCanModifyAccount(false);
            sbsUser.setCanDeleteAccount(false);
            sbsUser.setCanModifyT1AccountAccess(false);

            sbsUser.setCanAuthorizeCritical(false);
            sbsUser.setCanAuthorizeNonCritical(false);

            sbsUser.setCanAccessLogs(false);
            sbsUser.setCanAccessPii(false);

            sbsUser.setCanAuthorizeExternalModify(false);
            sbsUser.setCanAuthorizeInternalModify(false);

            sbsUser.setCanTransferCredit(false);

            sbsUser.setReqCount(0);
            sbsUser.setReqDate(new Date());

            userRepository.save(sbsUser);

            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean createOrg(String username, String name, String email, String phoneNumber, String billingAddr, String mailingAddr, String password) {
        if (userRepository.findByUsername(username) == null) {
            SbsUser sbsUser = new SbsUser();
            sbsUser.setUsername(username);
            sbsUser.setUserType(Constants.USER_ORG);
            sbsUser.setName(name);
            sbsUser.setEmail(email);
            sbsUser.setPhoneNumber(phoneNumber);
            sbsUser.setBillingAddr(billingAddr);
            sbsUser.setMailingAddr(mailingAddr);
            sbsUser.setPassword(bCryptPasswordEncoder.encode(password));
            sbsUser.setModifyRequest(false);
            sbsUser.setAllowT1AccountAccess(false);

            sbsUser.setCanCreateExternalUser(false);
            sbsUser.setCanCreateInternalUser(false);
            sbsUser.setCanDeleteExternalUser(false);
            sbsUser.setCanDeleteInternalUser(false);
            sbsUser.setCanModifyExternalUser(false);
            sbsUser.setCanModifyInternalUser(false);
            sbsUser.setCanViewExternalUser(false);
            sbsUser.setCanViewInternalUser(false);

            sbsUser.setCanPerformTechAccess(false);

            sbsUser.setCanViewAccount(false);
            sbsUser.setCanCreateAccount(false);
            sbsUser.setCanModifyAccount(false);
            sbsUser.setCanDeleteAccount(false);
            sbsUser.setCanModifyT1AccountAccess(false);

            sbsUser.setCanAuthorizeCritical(false);
            sbsUser.setCanAuthorizeNonCritical(false);

            sbsUser.setCanAccessLogs(false);
            sbsUser.setCanAccessPii(false);

            sbsUser.setCanAuthorizeExternalModify(false);
            sbsUser.setCanAuthorizeInternalModify(false);

            sbsUser.setCanTransferCredit(true);

            sbsUser.setReqCount(0);
            sbsUser.setReqDate(new Date());

            userRepository.save(sbsUser);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void modifyUser(SbsUser sbsUserToModify, SbsUser modifyRequest) {
        // perform modification
        if (modifyRequest.getName() != null && !modifyRequest.getName().isEmpty())
            sbsUserToModify.setName(modifyRequest.getName());
        if (modifyRequest.getEmail() != null && !modifyRequest.getEmail().isEmpty())
            sbsUserToModify.setEmail(modifyRequest.getEmail());
        if (modifyRequest.getPhoneNumber() != null && !modifyRequest.getPhoneNumber().isEmpty())
            sbsUserToModify.setPhoneNumber(modifyRequest.getPhoneNumber());
        if (modifyRequest.getBillingAddr() != null && !modifyRequest.getBillingAddr().isEmpty())
            sbsUserToModify.setBillingAddr(modifyRequest.getBillingAddr());
        if (modifyRequest.getMailingAddr() != null && !modifyRequest.getMailingAddr().isEmpty())
            sbsUserToModify.setMailingAddr(modifyRequest.getMailingAddr());
    }

}
