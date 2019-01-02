/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class GenerateKeys {

    private KeyPairGenerator keyGen;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public GenerateKeys(int keylength) throws NoSuchAlgorithmException, NoSuchProviderException {
            this.keyGen = KeyPairGenerator.getInstance("RSA");
            this.keyGen.initialize(keylength);
    }

    GenerateKeys() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void createKeys() {
            this.pair = this.keyGen.generateKeyPair();
            this.privateKey = pair.getPrivate();
            this.publicKey = pair.getPublic();
    }

    public PrivateKey getPrivateKey() {
            return this.privateKey;
    }

    public PublicKey getPublicKey() {
            return this.publicKey;
    }

    public void writeToFile(String path, byte[] key) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }
    
    public byte[] readFromFile(String fileName) throws FileNotFoundException, IOException {
        File file = new File(fileName);
        byte[] ba = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(ba);
        fis.close();
        
        return ba;
    }

    public void saveKeys(){
        GenerateKeys gk;
        try {
            
            File f = new File("KeyPairUser/publicKey");

            if ((f.exists() && f.isDirectory()) != true) {
                gk = new GenerateKeys(1024);
                gk.createKeys();
                gk.writeToFile("KeyPairUser/publicKey", gk.getPublicKey().getEncoded());
                gk.writeToFile("KeyPairUser/privateKey", gk.getPrivateKey().getEncoded());

                System.out.println(gk.getPrivateKey());
                //System.out.println(gk.getPublicKey());
            }
            
            
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
}