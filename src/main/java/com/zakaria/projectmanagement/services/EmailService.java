package com.zakaria.projectmanagement.services;

import com.zakaria.projectmanagement.utils.EnvLoader;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class EmailService {

    private static final String RECIPIENT_EMAIL = "adilmoukhlik@gmail.com";

    public static void sendEmailWithAttachment(File attachment, String subject, String body) throws MessagingException, IOException {
        // Get email credentials from environment variables
        String senderEmail = EnvLoader.get("APP_EMAIL");
        String senderPassword = EnvLoader.get("APP_EMAIL_PASSWORD");

        // Check if credentials are available
        if (senderEmail == null || senderEmail.isEmpty() ||
                senderPassword == null || senderPassword.isEmpty()) {
            throw new MessagingException("Email credentials not found in environment variables. " +
                    "Please set APP_EMAIL and APP_EMAIL_PASSWORD.");
        }

        // Set up mail server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Create session with authenticator
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        // Create message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(RECIPIENT_EMAIL));
        message.setSubject(subject);

        // Create message body part
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(body);

        // Create attachment part
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(attachment);

        // Create multipart message
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(attachmentPart);

        // Set content
        message.setContent(multipart);

        // Send message
        Transport.send(message);
    }
}