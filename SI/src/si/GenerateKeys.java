/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package si;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class GenerateKeys {

	private KeyPairGenerator keyGen;
	private KeyPair pair;
	private PrivateKey privateKey;
	private PublicKey publicKey;

	public GenerateKeys(int keylength) throws NoSuchAlgorithmException, NoSuchProviderException {
		this.keyGen = KeyPairGenerator.getInstance("RSA");
		this.keyGen.initialize(keylength);
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
        
        public static SecretKey getSymmetricKey() throws NoSuchAlgorithmException {
            SecretKey key = KeyGenerator.getInstance("AES").generateKey();
            return key;
        }

	public void writeToFile(String path, byte[] key) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();

		FileOutputStream fos = new FileOutputStream(f);
		fos.write(key);
		fos.flush();
		fos.close();
	}
        
        public static void create(String user, int id) throws CertificateException, UnrecoverableKeyException, GeneralSecurityException, NoSuchAlgorithmException, IOException, Exception {
        GenerateKeys gk;
        String[] userSplit;
        JsonObject json = new JsonObject();

        try {
            userSplit = user.split("\"");
            String path = "KeyPair/" + userSplit[1] + "/";
            gk = new GenerateKeys(2048);
            gk.createKeys();

            new File(path).mkdirs();

            //CartaoCidadao.signForEncryption(gk.getPublicKey().getEncoded(), path);
            gk.writeToFile(path + "publicKey", gk.getPublicKey().getEncoded());

            json.addProperty("privateKey", Base64.getEncoder().encodeToString(gk.getPrivateKey().getEncoded()));
            json.addProperty("symmetricKey", Base64.getEncoder().encodeToString(getSymmetricKey().getEncoded()));
            json.addProperty("id", id);
            String encodedPackage = PasswordEncryption.encrypt(json.toString());

            gk.writeToFile(path + userSplit[1],encodedPackage.getBytes());
        } catch (NoSuchAlgorithmException | IOException | NoSuchProviderException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }
}