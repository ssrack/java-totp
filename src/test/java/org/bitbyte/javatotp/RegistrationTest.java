package org.bitbyte.javatotp;

import lombok.SneakyThrows;
import org.junit.Test;

import static java.lang.System.getenv;
import static org.bitbyte.javatotp.EmailUtility.generateQRCode;
import static org.bitbyte.javatotp.EmailUtility.sendBarCodeToEmail;
import static org.bitbyte.javatotp.TOTPUtility.*;
import static org.junit.Assert.assertEquals;

public class RegistrationTest {

    private final String ISSUER = "bitbyte";
    private final String APPLICATION_NAME = "cargaraage";
    private final String FILE_PATH = "QRCODE.PNG";
    private final String SECRET_KEY = "SKHUOOUNTDSMZ33DZTXW7KBCJOSHUCBY";
    private final String SENDER = getenv("SENDER");
    private final String SENDER_PASSWORD = getenv("SENDER_PASSWORD");
    private final String RECIPIENT = getenv("RECIPIENT");

    @SneakyThrows
    @Test
    public void testRegistration() {
        String barCodeURL = getGoogleAuthenticatorBarCodeURL(ISSUER, APPLICATION_NAME);
        generateQRCode(barCodeURL, FILE_PATH, 500, 500);

        EmailProperties properties = new EmailProperties
                .EmailPropertiesBuilder()
                .auth(true)
                .enableTLS("true")
                .hostName("smtp.gmail.com")
                .port("587")
                .sender(SENDER)
                .password(SENDER_PASSWORD)
                .recipient(RECIPIENT)
                .build();
        sendBarCodeToEmail(properties, FILE_PATH);
    }

    @Test
    public void testOTP() {
        String otpCode = getOTPCode(SECRET_KEY);
        boolean valid = validate(SECRET_KEY, otpCode);
        assertEquals(true, valid);
    }

}
