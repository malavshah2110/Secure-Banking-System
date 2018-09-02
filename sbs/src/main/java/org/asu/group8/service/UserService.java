package org.asu.group8.service;

import org.asu.group8.entity.SbsUser;

public interface UserService {

    boolean createAdmin(String username, String name, String email, String phoneNumber, String billingAddr, String mailingAddr, String password);
    boolean createTier1(String username, String name, String email, String phoneNumber, String billingAddr, String mailingAddr, String password);
    boolean createTier2(String username, String name, String email, String phoneNumber, String billingAddr, String mailingAddr, String password);
    boolean createInd(String username, String name, String email, String phoneNumber, String billingAddr, String mailingAddr, String password);
    boolean createOrg(String username, String name, String email, String phoneNumber, String billingAddr, String mailingAddr, String password);
    void modifyUser(SbsUser sbsUserToModify, SbsUser modifyRequest);

}
