/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package User;

import java.nio.charset.Charset;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author JPRM
 */
public class Criptografia {
    
    private static final String ALGO = "AES";
    // meter random byte
    private static final byte[] keyValue = new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};
    //private static final byte[] keyValue = new byte[]{'T', 'h'};

    public String encrypt(String data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        
        //return encVal;
        return Base64.getEncoder().encodeToString(encVal);
    }

    /**
     * Decrypt a string with AES algorithm.
     *
     * @param encryptedData is a string
     * @return the decrypted string
     */
    public String decrypt(byte[] encryptedData) throws Exception {
        
        String encData = encryptedData.toString();
        System.out.println(encData);
        
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.getDecoder().decode(encData);
        byte[] decValue = c.doFinal(decordedValue);
        
        System.out.println(decValue);
        return new String(decValue);
    }

    /**
     * Generate a new encryption key.
     */
    public Key generateKey() throws Exception {
        return new SecretKeySpec(keyValue, ALGO);
    }


    /**

    byte[] bytesOfSK = readFromFile(nomeFicheiroComChave);
    SecretKey sk = initSimetricKey(bytesOfSK, algoritmo);

    byte[] clearText = readFromFile(nomeFicheiroCifrar);
    //Cipher c = Cipher.getInstance(algoritmo);
    //c.init(Cipher.ENCRYPT_MODE, sk);
    Cipher c = Cipher.getInstance(algoritmo + "/CBC/PKCS5Padding");
    c.init(Cipher.ENCRYPT_MODE, sk, new IvParameterSpec(new byte[16]));
    byte[] criptogram = c.doFinal(clearText);

    writeToFile(criptogram, nomePretendidoFicheiroCifrado);
    **/
}
