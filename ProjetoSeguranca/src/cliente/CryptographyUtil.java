package cliente;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONObject;

/**
 * @author visruthcv
 *
 */
public class CryptographyUtil {

    private static final String ALGORITHM = "RSA";

    public static JSONObject encryptSymetricKey(byte[] symmetricKey, byte[] publicKey, byte[] inputData) throws Exception {
        JSONObject json = new JSONObject();

        Cipher cipher = Cipher.getInstance("AES");

        SecretKey key = new SecretKeySpec(symmetricKey, 0, symmetricKey.length, "AES");

        cipher.init(Cipher.ENCRYPT_MODE, key);
        //texto cifrado com chave simetrica
        byte[] encryptedBytes = cipher.doFinal(inputData);

        //chave simetrica cifrada com chave publica
        byte[] chaveSimetricaCifrada = encriptar(publicKey, symmetricKey);

        json.append("texto", Base64.getEncoder().encodeToString(encryptedBytes));
        json.append("chave", Base64.getEncoder().encodeToString(chaveSimetricaCifrada));
        return json;

    }

    public static JSONObject encryptSymetricKeyWithPrivateKey(byte[] symmetricKey, byte[] privateKey, byte[] inputData) throws Exception {

        JSONObject json = new JSONObject();

        Cipher cipher = Cipher.getInstance("AES");

        SecretKey key = new SecretKeySpec(symmetricKey, 0, symmetricKey.length, "AES");

        cipher.init(Cipher.ENCRYPT_MODE, key);

        //texto cifrado com chave simetrica
        byte[] encryptedBytes = cipher.doFinal(inputData);

        //chave simetrica cifrada com chave publica
        byte[] chaveSimetricaCifrada = encriptarChavePrivada(privateKey, symmetricKey);

        json.append("texto", Base64.getEncoder().encodeToString(encryptedBytes));
        json.append("chave", Base64.getEncoder().encodeToString(chaveSimetricaCifrada));
        return json;

    }

    public static byte[] desencriptar(byte[] privateKey, byte[] texto) throws FileNotFoundException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        //como texto é um json que contem a chave e o texto cifrado
        //é necessário tratar do texto 1º
        String jsonTexto = new String(texto);
        JsonObject jsonResult = new JsonParser().parse(jsonTexto).getAsJsonObject();
        byte[] data = Base64.getDecoder().decode(jsonResult.get("texto").getAsString());
        byte[] chaveSimetricaCifrada = Base64.getDecoder().decode(jsonResult.get("chave").getAsString());

        PrivateKey chavePrivate = KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(privateKey));
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.PRIVATE_KEY, chavePrivate);
        //cipher.init(Cipher.PRIVATE_KEY, key);
        byte[] chaveSimetricaDecifrada = c.doFinal(chaveSimetricaCifrada);

        SecretKey key = new SecretKeySpec(chaveSimetricaDecifrada, 0, chaveSimetricaDecifrada.length, "AES");

        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, key);
        byte[] textoDesencriptado = aesCipher.doFinal(data);

        return textoDesencriptado;
    }

    public static byte[] desencriptarChavePublica(byte[] publicKey, byte[] texto) throws FileNotFoundException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        //como texto é um json que contem a chave e o texto cifrado
        //é necessário tratar do texto 1º
        String jsonTexto = new String(texto);
        JsonObject jsonResult = new JsonParser().parse(jsonTexto).getAsJsonObject();
        byte[] data = Base64.getDecoder().decode(jsonResult.get("texto").getAsString());
        byte[] chaveSimetricaCifrada = Base64.getDecoder().decode(jsonResult.get("chave").getAsString());

        PublicKey chavePublic = KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKey));
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, chavePublic);
        //cipher.init(Cipher.PRIVATE_KEY, key);
        byte[] chaveSimetricaDecifrada = c.doFinal(chaveSimetricaCifrada);

        SecretKey key = new SecretKeySpec(chaveSimetricaDecifrada, 0, chaveSimetricaDecifrada.length, "AES");

        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, key);
        byte[] textoDesencriptado = aesCipher.doFinal(data);

        return textoDesencriptado;
    }

    public static byte[] encriptar(byte[] publicKey, byte[] texto) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        PublicKey chavePublic = KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKey));

        Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        c.init(Cipher.PUBLIC_KEY, chavePublic);
        byte[] textoEncriptado = c.doFinal(texto);

        return textoEncriptado;
    }

    public static byte[] encriptarChavePrivada(byte[] privateKey, byte[] texto) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        PrivateKey chavePrivada = KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(privateKey));

        Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        c.init(Cipher.ENCRYPT_MODE, chavePrivada);
        byte[] textoEncriptado = c.doFinal(texto);

        return textoEncriptado;
    }

}
