/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package si;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author TiagoRodrigues
 */
public class SI {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        //opens the wmic and check the bio serial number
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "wmic", "bios", "get", "SerialNumber" });

            process.getOutputStream().close();
        } catch (IOException ex) {
            System.out.println(ex);
        }

        Scanner sc = new Scanner(process.getInputStream());
        String property = sc.next();
        String serial = sc.next();
        System.out.println(property + ": " + serial);

        //same thing but with the UUID (identificadodr unico universal)
        try {
            process = Runtime.getRuntime().exec(new String[] { "wmic", "csproduct", "get", "UUID" });

            process.getOutputStream().close();
        } catch (IOException ex) {
            System.out.println(ex);
        }

        Scanner sc1 = new Scanner(process.getInputStream());
        String property1 = sc1.next();
        String serial1 = sc1.next();
        System.out.println(property1 + ": " + serial1);
        
        String data = serial + ":" + serial1;
        
        String algoritmo = "AES";
        //generate_key(algoritmo, "key");
        //Encriptation(data, "cifra", "key", algoritmo);
        decriptation("plaintext", "cifra", "key", algoritmo);
    }
    
    public static void writeToFile(byte[] text, String fileName) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(new File(fileName), false);
        fos.write(text);
        fos.close();
    }
    
    public static void generate_key(String algoritm, String fileName) throws IOException, NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance(algoritm);
        SecretKey sk = kg.generateKey();
        byte[] simetricKey = sk.getEncoded();
        
        FileOutputStream fos = new FileOutputStream(new File(fileName), false);
        fos.write(simetricKey);
        fos.close();
    }
    

    public static void Encriptation(String data, String nomePretendidoFicheiroCifrado, String nomeFicheiroComChave, String algoritm) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException{
        byte[] bytesOfSK = readFromFile(nomeFicheiroComChave);
        SecretKey sk = initSimetricKey(bytesOfSK, algoritm);

        byte[] clearText = data.getBytes();
        Cipher c = Cipher.getInstance(algoritm + "/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, sk, new IvParameterSpec(new byte[16]));
        byte[] criptogram = c.doFinal(clearText);

        writeToFile(criptogram, nomePretendidoFicheiroCifrado);
    }
    
    public static void decriptation(String nomePretendidoFicheiroDecifrado, String textoEncriptado, String nomeFicheiroComChave, String algoritm) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] bytesOfSK = readFromFile(nomeFicheiroComChave);
        SecretKey sk = initSimetricKey(bytesOfSK, algoritm);
        
        byte[] encriptedText = readFromFile(textoEncriptado);
        //Cipher d = Cipher.getInstance(algoritm);
        //d.init(Cipher.DECRYPT_MODE, sk);
        Cipher d = Cipher.getInstance(algoritm + "/CBC/PKCS5Padding");
        d.init(Cipher.DECRYPT_MODE, sk, new IvParameterSpec(new byte[16]));
        byte[] textDecifrado = d.doFinal(encriptedText);
        
        writeToFile(textDecifrado, nomePretendidoFicheiroDecifrado);
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

}
