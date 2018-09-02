package org.asu.group8.service;

import org.asu.group8.entity.SbsUser;

public interface OtpService {

    boolean validateOtp(SbsUser sbsUser, String otp);

    void createOtp(SbsUser sbsUser);

}
