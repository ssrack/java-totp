package io.github.ssrack.javatotp;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static com.google.zxing.client.j2se.MatrixToImageWriter.writeToStream;
import static javax.mail.Transport.send;

public class EmailUtility {

    public static void sendBarCodeToEmail(EmailProperties properties, String filePath) throws MessagingException, IOException {

        Properties props = new Properties();

        props.put("mail.smtp.auth", properties.isAuth());
        props.put("mail.smtp.starttls.enable", properties.getEnableTLS());
        props.put("mail.smtp.host", properties.getHostName());
        props.put("mail.smtp.port", properties.getPort());

        Session emailSession;
        if (properties.isAuth()) {
            emailSession = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            properties.getSender(), properties.getPassword());
                }
            });
        } else emailSession = Session.getDefaultInstance(props);

        MimeMessage message = new MimeMessage(emailSession);
        message.setFrom(new InternetAddress(properties.getSender()));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(properties.getRecipient()));
        message.setSubject("QR Code to setup MFA");

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent("Attached is the QR Code you've requested. Scan it in one the authenticator apps which you can find on PlayStore in Android or AppStore in IOS devices.", "text/html");

        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        File qrCode = new File(filePath);
        attachmentBodyPart.attachFile(qrCode);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        multipart.addBodyPart(attachmentBodyPart);

        message.setContent(multipart);
        send(message);

        qrCode.deleteOnExit();

    }

    public static void generateQRCode(String barcodeURL, String filePath, int width, int height) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(barcodeURL, BarcodeFormat.QR_CODE, width, height);
        try(FileOutputStream out = new FileOutputStream(filePath)) {
            writeToStream(matrix, "png", out);
        }
    }
}
