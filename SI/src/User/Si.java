/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;
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
public class Si {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    //universal unique id
    private static String uuid;
    //bios id
    private static String bios;
    
    private static String algoritmo = "AES";
    //random key generated
    private static byte[] simetricKey;
    //encripted message
    private static byte[] criptogram;
    
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        
        /*
        //generate_key(algoritmo, "key");
        //Encriptation(data, "cifra", "key", algoritmo);
        decriptation("plaintext", "cifra", "key", algoritmo);
        */
        
        createAsymetricKeys();
    }
    
    public static void base64(){
        //passa para base 64 o "teste"
        byte[] encodedBytes = Base64.getEncoder().encode("Test".getBytes());
        System.out.println("encodedBytes " + new String(encodedBytes));
        //dá descodifica da base 64 para texto
        byte[] decodedBytes = Base64.getDecoder().decode(encodedBytes);
        System.out.println("decodedBytes " + new String(decodedBytes));
    }
    
    public static void getInfo(){
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
        
        bios = serial;
        uuid = serial1;
    }
    
    private static void writeToFile(byte[] text, String fileName) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(new File(fileName), false);
        fos.write(text);
        fos.close();
    }
    
    private static void generate_key(String algoritm, String fileName) throws IOException, NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance(algoritm);
        SecretKey sk = kg.generateKey();
        byte[] simetricKey = sk.getEncoded();
        
        
        FileOutputStream fos = new FileOutputStream(new File(fileName), false);
        fos.write(simetricKey);
        fos.close();
        
    }
    

    private static void Encriptation(String data, String nomePretendidoFicheiroCifrado, String nomeFicheiroComChave, String algoritm) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException{
        byte[] bytesOfSK = simetricKey;
        SecretKey sk = initSimetricKey(bytesOfSK, algoritm);

        byte[] clearText = data.getBytes();
        Cipher c = Cipher.getInstance(algoritm + "/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, sk, new IvParameterSpec(new byte[16]));
        byte[] criptogram = c.doFinal(clearText);

        //writeToFile(criptogram, nomePretendidoFicheiroCifrado);
    }
    
    private static void decriptation(String nomePretendidoFicheiroDecifrado, String textoEncriptado, String nomeFicheiroComChave, String algoritm) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
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
    
    private static byte[] readFromFile(String fileName) throws FileNotFoundException, IOException {
        File file = new File(fileName);
        byte[] ba = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(ba);
        fis.close();
        
        return ba;
    }
    
    private static SecretKey initSimetricKey(byte [] keyBytes, String algoritm) {
        SecretKey sk = new SecretKeySpec(keyBytes, algoritm);
        
        return sk;
    }
    
    private static void createAsymetricKeys(){   
        
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(1024, random);
            KeyPair pair = keyGen.generateKeyPair();
            PrivateKey priv = pair.getPrivate();
            PublicKey pub = pair.getPublic();
            
            System.out.println("priv key:" + priv);
            System.out.println("pub key:" + pub   );
            
        } catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
        }
    }

}
