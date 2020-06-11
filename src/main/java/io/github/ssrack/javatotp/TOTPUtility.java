package io.github.ssrack.javatotp;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Long.toHexString;
import static java.lang.System.arraycopy;
import static java.lang.System.currentTimeMillis;
import static java.net.URLEncoder.encode;
import static javax.crypto.Mac.getInstance;
import static org.apache.commons.codec.binary.Hex.encodeHexString;

@NoArgsConstructor
@Slf4j
public class TOTPUtility {

    public static Map<String, String> getGoogleAuthenticatorBarCodeURL(final String issuer, final String applicationName)  {

        String secretKey = generateSecretKey();
        log.info("TOTPUtility: getGoogleAuthenticatorBarCodeURL: generated secret key -> {}", secretKey);
        try {
            Map<String, String> urlAndKeyMap = new HashMap<>(2);
            urlAndKeyMap.put("secret_key", secretKey);

            String barCodeUrl = "otpauth://totp/"
                    + encode(issuer + ":" + applicationName, "UTF-8")
                    .replace("+", "%20")
                    + "?secret=" + encode(secretKey, "UTF-8")
                    .replace("+", "%20")
                    + "&issuer=" + encode(issuer, "UTF-8")
                    .replace("+", "%20");
            urlAndKeyMap.put("barcode_url", barCodeUrl);
            return urlAndKeyMap;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String generateSecretKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[20];
        secureRandom.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeAsString(bytes);
    }

    public static String getOTPCode(String key) {
        return getOTP(getStep(), key);
    }

    public static boolean validate(final String key, final String otp) {
        return validate(getStep(), key, otp);
    }

    private static boolean validate(long step, String key, String otp) {
        return getOTP(step, key).equals(otp) || getOTP(step -1, key).equals(otp);
    }

    private static long getStep() {
        return currentTimeMillis() / 30000;
    }

    private static String getOTP(long step, String key) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(key);
        String hexKey = encodeHexString(bytes);

        String steps = toHexString(step).toUpperCase();
        while (steps.length() < 16) {
            steps = "0" + steps;
        }
        final byte[] text = hexStr2Bytes(steps);
        final byte[] keyBytes = hexStr2Bytes(hexKey);
        final byte[] hash = hmac_sha1(keyBytes, text);
        final int offset = hash[hash.length - 1] & 0xf;
        final int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16) | ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);
        final int otp = binary % 1000000;
        String result = Integer.toString(otp);
        while (result.length() < 6) {
            result = "0" + result;
        }
        return result;
    }

    private static byte[] hexStr2Bytes(final String hex) {
        final byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();
        final byte[] ret = new byte[bArray.length - 1];
        arraycopy(bArray, 1, ret, 0, ret.length);
        return ret;
    }

    private static byte[] hmac_sha1(final byte[] keyBytes, final byte[] text) {
        try {
            final Mac hmac = getInstance("HmacSHA1");
            final SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (final GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }
}
