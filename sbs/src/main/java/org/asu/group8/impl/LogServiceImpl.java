package org.asu.group8.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.IOUtils;
import org.asu.group8.config.Constants;
import org.asu.group8.entity.SbsLog;
import org.asu.group8.entity.SbsUser;
import org.asu.group8.repo.LogRepository;
import org.asu.group8.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataSource;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private LogRepository logRepository;

    @Value(value = "classpath:public.der")
    private Resource publicAdmin;

    @Override
    public void log(SbsUser sbsUser, String description) {
        int maxLength = 200;

        while (description.length() > 0) {
            String shortDescription = description;

            if (shortDescription.length() > maxLength) {
                shortDescription = description.substring(0, maxLength - 1);
                description = description.substring(maxLength);
            } else {
                description = "";
            }

            logShort(sbsUser, shortDescription);
        }

    }

    private void logShort(SbsUser sbsUser, String shortDescription) {
        // create log entry
        SbsLog sbsLog = new SbsLog();

        // fill in log entry
        sbsLog.setUsername(sbsUser != null ? sbsUser.getUsername() : "system");
        sbsLog.setTime(new Date());
        sbsLog.setDescription(shortDescription);

        // print to the console for debugging
        System.out.println("Logging at " + sbsLog.getTime() + " for " + sbsLog.getUsername() + ": " +
                sbsLog.getDescription());

        // save log entry
        logRepository.save(sbsLog);
    }

    public void emailLogs(SbsUser sbsUser) {

        try {
            PipedOutputStream aesPipedOutputStream = new PipedOutputStream();
            PipedInputStream aesPipedInputStream = new PipedInputStream(aesPipedOutputStream);

            PipedOutputStream rsaPipedOutputStream = new PipedOutputStream();
            PipedInputStream rsaPipedInputStream = new PipedInputStream(rsaPipedOutputStream);

            // start a thread to write to pipes
            new Thread(() -> {
                byte aesKey[] = null;

                try {
                    Cipher aesCipher = Cipher.getInstance("AES");

                    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                    keyGenerator.init(128);
                    SecretKey key = keyGenerator.generateKey();
                    aesKey = key.getEncoded();
                    SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey, "AES");
                    aesCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

                    CipherOutputStream cipherOutputStream = new CipherOutputStream(aesPipedOutputStream, aesCipher);

                    /*** Generate PDF ***/

                    List<SbsLog> sbsLogs = getLogs();

                    Document document = new Document(PageSize.A4);
                    PdfWriter.getInstance(document, cipherOutputStream);
                    document.open();
                    PdfPTable table = new PdfPTable(3);
                    table.setWidthPercentage(100);
                    table.setWidths(new int[] {2, 3, 5});
                    Font headFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 12f);
                    Font cellFont = FontFactory.getFont(FontFactory.TIMES, 10f);

                    // add a header
                    Paragraph header = new Paragraph();
                    header.add(new Paragraph(" "));
                    Paragraph title = new Paragraph("System Logs", headFont);
                    title.setAlignment(Element.ALIGN_CENTER);
                    document.add(title);
                    header.add(new Paragraph(" "));
                    document.add(header);

                    // add a table
                    PdfPCell hcell;
                    hcell = new PdfPCell(new Phrase("Username", headFont));
                    hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(hcell);

                    hcell = new PdfPCell(new Phrase("Timestamp", headFont));
                    hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(hcell);

                    hcell = new PdfPCell(new Phrase("Description", headFont));
                    hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(hcell);

                    for (SbsLog sbsLog : sbsLogs) {

                        hcell = new PdfPCell(new Phrase(sbsLog.getUsername(), cellFont));
                        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(hcell);

                        hcell = new PdfPCell(new Phrase(sbsLog.getTime().toString(), cellFont));
                        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(hcell);

                        hcell = new PdfPCell(new Phrase(sbsLog.getDescription(), cellFont));
                        hcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(hcell);

                    }

                    document.add(table);
                    document.close();

                } catch (DocumentException exception) {
                    exception.printStackTrace();
                } catch (NoSuchAlgorithmException exception) {
                    exception.printStackTrace();
                } catch (NoSuchPaddingException exception) {
                    exception.printStackTrace();
                } catch (InvalidKeyException exception) {
                    exception.printStackTrace();
                }

                try {

                    Cipher rsaCipher = Cipher.getInstance("RSA");
                    InputStream publicKeyInputStream = publicAdmin.getInputStream();

                    // read public key to be used to encrypt the AES key
                    byte[] encodedKey = IOUtils.toByteArray(publicKeyInputStream);

                    // create public key
                    X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedKey);
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

                    // write AES key
                    rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);

                    CipherOutputStream cipherOutputStream = new CipherOutputStream(rsaPipedOutputStream, rsaCipher);
                    cipherOutputStream.write(aesKey);
                    cipherOutputStream.close();

                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                } catch (NoSuchAlgorithmException exception) {
                    exception.printStackTrace();
                } catch (InvalidKeySpecException exception) {
                    exception.printStackTrace();
                } catch (NoSuchPaddingException exception) {
                    exception.printStackTrace();
                } catch (InvalidKeyException exception) {
                    exception.printStackTrace();
                }

            }).start();

            // start a thread to listen to the pipes
            new Thread(() -> {

                List<DataSource> dataSources = new ArrayList<>();
                List<String> fileNames = new ArrayList<>();

                try {
                    ByteArrayDataSource aesByteArrayDataSource = new ByteArrayDataSource(aesPipedInputStream, "application/stream");
                    dataSources.add(aesByteArrayDataSource);
                    fileNames.add("AES_File");

                    ByteArrayDataSource rsaByteArrayDataSource = new ByteArrayDataSource(rsaPipedInputStream, "application/stream");
                    dataSources.add(rsaByteArrayDataSource);
                    fileNames.add("RSA_File");

                    MimeMessage message = javaMailSender.createMimeMessage();

                    try {
                        MimeMessageHelper helper = new MimeMessageHelper(message, true);

                        helper.setFrom(Constants.EMAIL_FROM);
                        helper.setTo(sbsUser.getEmail());
                        helper.setSubject("Admin Logs");
                        helper.setText("See attachments");

                        for (int i = 0; i < dataSources.size(); i++) {
                            helper.addAttachment(fileNames.get(i), dataSources.get(i));
                        }

                    } catch (MessagingException exception) {
                        throw new MailParseException(exception);
                    }

                    System.out.println("Sending");
                    javaMailSender.send(message);
                    System.out.println("Sent");
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

            }).start();

        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    @Transactional(readOnly = true,isolation = Isolation.SERIALIZABLE)
    private List<SbsLog> getLogs() {
        return logRepository.findTopLogs();
    }

}
