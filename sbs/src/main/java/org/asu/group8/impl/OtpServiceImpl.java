package org.asu.group8.impl;

import org.asu.group8.entity.SbsUser;
import org.asu.group8.repo.UserRepository;
import org.asu.group8.service.EmailService;
import org.asu.group8.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean validateOtp(SbsUser sbsUser, String otp) {
        return sbsUser.getOtp() != null && otp != null && sbsUser.getOtp().equals(otp) &&
                sbsUser.getOtpExp() != null && sbsUser.getOtpExp().getTime() > new Date().getTime();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createOtp(SbsUser sbsUser) {
        // grab OTP
        String otp = "";
        for (int i = 0; i < 6; i++) {
            otp += Integer.toString((int) Math.floor(Math.random() * 10));
        }
        sbsUser.setOtp(otp);

        // set the expiration for 5 minutes
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        sbsUser.setOtpExp(calendar.getTime());
        System.out.println("OTP expires: " + calendar.getTime());

        // save the current user
        userRepository.save(sbsUser);

        // now send the OTP
        emailService.sendEmail(sbsUser, "One-Time Password",
                "Hello " + sbsUser.getName() + ",\n\n" +
                        "Somebody tried to perform an operation on your user account that requires an OTP. " +
                        "If you do not recognize this activity, please change your password immediately! " +
                        "If you do recognize this activity, please enter your One-Time Password: " + sbsUser.getOtp() +
                        " in your browser to continue. " +
                        "Your OTP will expire on " + calendar.getTime() + ".\n\n" +
                        "Thank you!");

    }

}
