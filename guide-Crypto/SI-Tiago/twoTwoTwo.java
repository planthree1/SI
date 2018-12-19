/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aula10.pkg10.criptografia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 *
 * @author tjsantos
 */
public class twoTwoTwo {

    public twoTwoTwo(String algoritm, String fileName) throws IOException, NoSuchAlgorithmException {
        byte[] simetricKey = getSimetricKey(algoritm);
        writeToFile(simetricKey, fileName);
    }
    
    public static byte[] getSimetricKey(String Algoritm) throws NoSuchAlgorithmException {
            KeyGenerator kg = KeyGenerator.getInstance(Algoritm);
            SecretKey sk = kg.generateKey();
            return sk.getEncoded();
    }
    
    public static void writeToFile(byte[] text, String fileName) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(new File(fileName), false);
        fos.write(text);
        fos.close();
    }
}
