/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package si;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

public class PasswordEncryption {
    
    //original private static char[] password=null;
    private static String password= "qwertyu";
    private static String salt;
    //65536
    private static int pswdIterations = 1000;
    private static int keySize = 256;
    private static int saltlength = keySize / 8;
    private static byte[] ivBytes;

    // Methods
    public static String encrypt(String plainText) throws Exception {
        /**
        try {
            do {
                JFrame frame = new JFrame();
                JPanel panel = new JPanel(new BorderLayout(5, 5));
                JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));

                label.add(new JLabel("Password", SwingConstants.RIGHT));
                panel.add(label, BorderLayout.WEST);

                JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));

                JPasswordField passwordField = new JPasswordField();
                controls.add(passwordField);
                panel.add(controls, BorderLayout.CENTER);

                JOptionPane.showMessageDialog(frame, panel, "Password(Min:6 carateres)", JOptionPane.QUESTION_MESSAGE);
                password = passwordField.getPassword();

            } while (password == null || password.equals("") || password.length<=5);
        } catch (Exception e) {
            System.err.println("Password Inválida" + e);
        }
        */
        
        char[] charArray = password.toCharArray();
        
        //get salt
        salt = generateSalt();
        byte[] saltBytes = salt.getBytes("UTF-8");

        // Derive the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(
                //password
                charArray,
                saltBytes,
                pswdIterations,
                keySize
        );

        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

        //encrypt the message
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);

        AlgorithmParameters params = cipher.getParameters();
        ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes());

        //String encodedText = Base64.encodeToString(encryptedTextBytes, Base64.DEFAULT);
        String encodedText = Base64.getEncoder().encodeToString(encryptedTextBytes);
        String encodedIV = Base64.getEncoder().encodeToString(ivBytes);
        String encodedSalt = Base64.getEncoder().encodeToString(saltBytes);
        String encodedPackage = encodedSalt + "]" + encodedIV + "]" + encodedText;

        return encodedPackage;
    }

    public static String decrypt(String encryptedText, String password) throws Exception {
        String[] fields = encryptedText.split("]");
        byte[] saltBytes = Base64.getDecoder().decode(fields[0]);
        ivBytes = Base64.getDecoder().decode(fields[1]);
        byte[] encryptedTextBytes = Base64.getDecoder().decode(fields[2]);
        
        // Derive the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                saltBytes,
                pswdIterations,
                keySize
        );

        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

        // Decrypt the message
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));

        byte[] decryptedTextBytes = null;
        try {
            decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return new String(decryptedTextBytes);
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[saltlength];
        random.nextBytes(bytes);
        return new String(bytes);
    }

}
