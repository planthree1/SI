package cliente;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacShaSignature {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA256";

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public static String calculateRFC2104HMAC(String data, String key)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF-8"), HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes("UTF-8")));
    }

    public static void main(String[] args) throws Exception {
        String hmac = calculateRFC2104HMAC("batatassss", "o rato roeu a rolha do rei da russdfdsfsia");
        
        System.out.println(hmac);
    }

    public static String encode(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return toHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
    }
}
