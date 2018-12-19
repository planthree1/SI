/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aula10.pkg10.criptografia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author tjsantos
 */
public class twoTwoThree {
    
    public twoTwoThree(String nomeFicheiroCifrar, String nomePretendidoFicheiroCifrado, String nomeFicheiroComChave, String algoritm) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        //first get the file of the secret key and init it
        
        byte[] bytesOfSK = readFromFile(nomeFicheiroComChave);
        SecretKey sk = initSimetricKey(bytesOfSK, algoritm);
        
        //secondly get the clear text and cipher
        byte[] clearText = readFromFile(nomeFicheiroCifrar);
        byte[] criptogram = cipherText(algoritm, sk, clearText);
        
        //third write to file the criptgram
        writeToFile(criptogram, nomePretendidoFicheiroCifrado);
    }
    
    public static byte[] readFromFile(String fileName) throws FileNotFoundException, IOException {
        File file = new File(fileName);
        byte[] ba = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(ba);
        fis.close();
        
        return ba;
    }
    
    public static SecretKey initSimetricKey(byte [] keyBytes, String algoritm) {
        SecretKey sk = new SecretKeySpec(keyBytes, algoritm);
        
        return sk;
    }
    
    public static byte[] cipherText(String algoritm, SecretKey sk, byte[] clearText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        
        Cipher c = Cipher.getInstance(algoritm);
        c.init(Cipher.ENCRYPT_MODE, sk);
        
        return c.doFinal(clearText);
    }
    
    public static void writeToFile(byte[] text, String fileName) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(new File(fileName), false);
        fos.write(text);
        fos.close();
    }
}
