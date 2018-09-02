package org.asu.group8.impl;

import org.asu.group8.entity.SbsDevice;
import org.asu.group8.entity.SbsUser;
import org.asu.group8.model.LoginRequest;
import org.asu.group8.model.LoginResponse;
import org.asu.group8.repo.DeviceRepository;
import org.asu.group8.repo.UserRepository;
import org.asu.group8.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SafeServiceImpl implements SafeService {

    /*** Services ***/
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private LogService logService;

    @Autowired
    private DeviceService deviceService;

    /*** Repositories ***/
    @Autowired
    private DeviceRepository deviceRepository;

    /*** Other ***/
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response, String sbsDeviceCode) {
        LoginResponse loginResponse = new LoginResponse();

        // grab the user details
        SbsUser sbsUser = userRepository.findByUsername(loginRequest.getUsername());

        if (sbsUser == null) {
            // not untrusted, user just was not found
            loginResponse.setUntrusted(false);
            loginResponse.setSuccess(false);
            System.out.println("User not found");
        } else {

            //Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            //new User(loginRequest.getUsername(), sbsUser.getPassword(), grantedAuthorities);

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

            // grab the authentication token
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(userDetails, loginRequest.getPassword(), userDetails.getAuthorities());

            try {
                // check if hashed passes match
                // this automatically hashes pass and compares to that in userDetails
                authenticationManager.authenticate(token);

                if (token.isAuthenticated()) {
                    // look for recognized device
                    SbsDevice sbsDevice = deviceRepository.findByCode(sbsUser, sbsDeviceCode, new Date());

                    // device is not recognized, look for OTP
                    if (sbsDevice == null) {

                        // check if the OTP matches
                        if (otpService.validateOtp(sbsUser, loginRequest.getOtp())) {

                            logService.log(sbsUser, "Device not recognized but OTP matches. Authenticated.");

                            // OTP matches so trust the device
                            String code = deviceService.createDevice(sbsUser.getUsername());

                            // send the secure cookie
                            Cookie sbsDeviceCookie = new Cookie("sbs_device_code", code);
                            sbsDeviceCookie.setSecure(true);
                            sbsDeviceCookie.setMaxAge(60 * 60 * 24 * 7); // 60 secs, 60 minutes, 24 hours, 7 days
                            response.addCookie(sbsDeviceCookie);

                            // unrecognized device but OTP matched
                            loginResponse.setUntrusted(false);
                            loginResponse.setSuccess(true);
                        } else {
                            logService.log(sbsUser, "Device not recognized and OTP does not match.");

                            otpService.createOtp(sbsUser);
                            System.out.println("Emailing OTP: " + sbsUser.getOtp());

                            // unrecognized device and OTP did not match
                            loginResponse.setUntrusted(true);
                            loginResponse.setSuccess(false);
                        }

                    } else {
                        logService.log(sbsUser, "Device recognized. Authenticated.");

                        // recognized device
                        loginResponse.setUntrusted(false);
                        loginResponse.setSuccess(true);
                    }

                } else {
                    logService.log(sbsUser, "Authentication unsuccessful");
                    // failed authentication
                    loginResponse.setUntrusted(false);
                    loginResponse.setSuccess(false);
                }

                // authenticate if successful
                if (loginResponse.getSuccess()) {
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
            } catch (BadCredentialsException exception) {
                // failed authentication with some exception
                loginResponse.setUntrusted(false);
                loginResponse.setSuccess(false);
            }

        }

        // return the response
        return loginResponse;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    public boolean techLogin(String username) {
        // grab the user details
        SbsUser sbsUser = userRepository.findByUsername(username);

        if (sbsUser == null) {
            return false;
        } else {
            // grab the user details
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            UserDetails userDetails = new User(username, sbsUser.getPassword(), grantedAuthorities);

            // grab the authentication token
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            logService.log(sbsUser, "Authentication successful (tech)");
            SecurityContextHolder.getContext().setAuthentication(token);

            return true;
        }
    }
}
