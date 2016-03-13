package com.fsggs.server.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

// TODO:: Adding to yaml.next config
public class EMail {
    public static void send(String to, String subject, String textMessage, String from) throws MessagingException {
//        final String username = "your_user_name@gmail.com";
//        final String password = "yourpassword";

        Properties props = new Properties();
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.host", "127.0.0.1");
        props.put("mail.smtp.port", "25");

        Session session = Session.getInstance(props //,
//                new javax.mail.Authenticator() {
//                    protected PasswordAuthentication getPasswordAuthentication() {
//                        return new PasswordAuthentication(username, password);
//                    }
//                }
        );
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(textMessage);

        Transport.send(message);
    }
}
