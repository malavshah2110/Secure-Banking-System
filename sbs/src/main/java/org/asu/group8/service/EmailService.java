package org.asu.group8.service;

import org.asu.group8.entity.SbsUser;

import javax.activation.DataHandler;
import javax.activation.DataSource;

public interface EmailService {

    void sendEmail(SbsUser sbsUser, String subject, String text);

    void sendEmailWithAttachments(SbsUser sbsUser, String subject, String text, DataSource dataSources[], String fileNames[]);

}
