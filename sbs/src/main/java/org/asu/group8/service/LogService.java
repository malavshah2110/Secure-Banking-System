package org.asu.group8.service;

import org.asu.group8.entity.SbsUser;

public interface LogService {

    void log(SbsUser sbsUser, String description);

    void emailLogs(SbsUser sbsUser);

}
