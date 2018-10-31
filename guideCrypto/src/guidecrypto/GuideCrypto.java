/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guidecrypto;

import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/**
 *
 * @author JPRM
 */
public class GuideCrypto {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        /*String algoritmo = "AES";
        ex222 (algoritmo, "encriptedSimetricKey");
        ex223 ("textoClaro.txt", "Criptograma", "encriptedSimetricKey", algoritmo);
        ex224 ("textoDesencriptado.txt", "Criptograma", "encriptedSimetricKey", algoritmo);
        */
        String algoritmo = "RSA";
        int keySize = 2048;
        ex281 (algoritmo, keySize, "C:\\Users\\JPRM\\Desktop\\SI\\guide-Crypto\\guideCrypto\\criptografiaChavesAssimetricas\\privateKey", "C:\\Users\\JPRM\\Desktop\\SI\\guide-Crypto\\guideCrypto\\criptografiaChavesAssimetricas\\publicKey");
        
    }
    
    public static void ex281(String algoritm, int keySize, String filePrivateK, String filePublicK) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(algoritm);
        kpg.initialize(keySize);
        KeyPair keyPair = kpg.genKeyPair();
        PublicKey pubK = keyPair.getPublic();
        PrivateKey privK = keyPair.getPrivate();
        byte[] publicK = pubK.getEncoded();
        byte[] privateK = privK.getEncoded();
        
        /*Cipher cipher = Cipher.getInstance(algoritm);
        cipher.init(Cipher.ENCRYPT_MODE, privK);
        byte[] encryptedMensage = cipher.doFinal(fileName.getBytes());
        */
        
        FileOutputStream fospriv = new FileOutputStream(new File(filePrivateK), false);
        fospriv.write(privateK);
        fospriv.close();
        
        FileOutputStream fospub = new FileOutputStream(new File(filePublicK), false);
        fospub.write(publicK);
        fospub.close();
    }
    
    public static void ex282(String nomeFicheiroCifrar, String nomePretendidoFicheiroCifrado, String nomeFicheiroComChave, String algoritm) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException{
        
    }
    
    public static void ex222(String algoritm, String fileName) throws IOException, NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance(algoritm);
        SecretKey sk = kg.generateKey();
        byte[] simetricKey = sk.getEncoded();
        
        FileOutputStream fos = new FileOutputStream(new File(fileName), false);
        fos.write(simetricKey);
        fos.close();
    }
    
    public static void ex223(String nomeFicheiroCifrar, String nomePretendidoFicheiroCifrado, String nomeFicheiroComChave, String algoritm) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException{
        byte[] bytesOfSK = readFromFile(nomeFicheiroComChave);
        SecretKey sk = initSimetricKey(bytesOfSK, algoritm);

        byte[] clearText = readFromFile(nomeFicheiroCifrar);
        //Cipher c = Cipher.getInstance(algoritm);
        //c.init(Cipher.ENCRYPT_MODE, sk);
        Cipher c = Cipher.getInstance(algoritm + "/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, sk, new IvParameterSpec(new byte[16]));
        byte[] criptogram = c.doFinal(clearText);

        writeToFile(criptogram, nomePretendidoFicheiroCifrado);
    }
    
    public static void ex224(String nomePretendidoFicheiroDecifrado, String textoEncriptado, String nomeFicheiroComChave, String algoritm) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
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
    
    public static void writeToFile(byte[] text, String fileName) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(new File(fileName), false);
        fos.write(text);
        fos.close();
    }
}
