package org.asu.group8.service;

import org.asu.group8.model.LoginRequest;
import org.asu.group8.model.LoginResponse;

import javax.servlet.http.HttpServletResponse;

public interface SafeService {

    LoginResponse login(LoginRequest loginRequest, HttpServletResponse response, String sbsDeviceCode);

    boolean techLogin(String username);

}
