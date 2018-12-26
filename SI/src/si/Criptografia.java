/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package si;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author JPRM
 */
public class Criptografia {
    public static void encriptarSymetricKey(String nomeFicheiroCifrar, String nomePretendidoFicheiroCifrado, String nomeFicheiroComChave) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        String algoritmo = "AES";
        byte[] bytesOfSK = readFromFile(nomeFicheiroComChave);
        SecretKey sk = initSimetricKey(bytesOfSK, algoritmo);

        byte[] clearText = readFromFile(nomeFicheiroCifrar);
        //Cipher c = Cipher.getInstance(algoritmo);
        //c.init(Cipher.ENCRYPT_MODE, sk);
        Cipher c = Cipher.getInstance(algoritmo + "/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, sk, new IvParameterSpec(new byte[16]));
        byte[] criptogram = c.doFinal(clearText);

        writeToFile(criptogram, nomePretendidoFicheiroCifrado);
    }
}
