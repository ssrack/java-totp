package io.github.ssrack.javatotp;

import lombok.SneakyThrows;
import org.junit.Test;

import java.util.Map;

import static java.lang.System.getenv;
import static io.github.ssrack.javatotp.EmailUtility.generateQRCode;
import static io.github.ssrack.javatotp.EmailUtility.sendBarCodeToEmail;
import static io.github.ssrack.javatotp.TOTPUtility.*;
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
        Map<String, String> urlAndKeyMap = getGoogleAuthenticatorBarCodeURL(ISSUER, APPLICATION_NAME);
        String barCodeURL = urlAndKeyMap.get("barcode_url");
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

        String secretKey = urlAndKeyMap.get("secret_key");
        String otpCode = getOTPCode(secretKey);
        boolean valid = validate(secretKey, otpCode);
        assertEquals(true, valid);
    }

    @Test
    public void testOTP() {
        String otpCode = getOTPCode(SECRET_KEY);
        boolean valid = validate(SECRET_KEY, otpCode);
        assertEquals(true, valid);
    }

}
