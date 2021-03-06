/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assinaturadigital;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.misc.BASE64Encoder;

/**
 *
 * @author tjsantos
 */
public class AssinaturaDigital {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, InvalidKeyException, SignatureException {
        
        Provider[] provs = Security.getProviders();
        for (int i = 0; i < provs.length; i++) {
            System.out.println(i + " - Nome do provider: " + provs[i].getName());
        }

        System.out.println(" ");
        KeyStore ks = KeyStore.getInstance( "PKCS11", provs[10] );
        ks.load( null, null );

        Enumeration<String> als = ks.aliases();
        while (als.hasMoreElements()){
            System.out.println( als.nextElement() );
        }

        
        // Criar assinatura digital de um documento
        
        //ler ficheiro
        FileInputStream fis = new FileInputStream("ORealTexto.txt");
        byte[] ba = new byte[fis.available()];
        fis.read(ba);
        fis.close();
        
        Key key = ks.getKey("CITIZEN AUTHENTICATION CERTIFICATE", null);
        
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign((PrivateKey) key);
        sig.update(ba);
        byte[] sigBytes = sig.sign();
        //System.out.println("Singature (Encoded): " + new BASE64Encoder().encode(sigBytes));
        
        //byte[] chv = key.getEncoded();
        
        //Guardar assinatura num ficheiro
        FileOutputStream fos = new FileOutputStream("assinatura.txt");
        fos.write(sigBytes);
        fos.close();
        
        // Guardar certificado de chave publica noutro ficheiro
        
        Certificate ct = ks.getCertificate("CITIZEN AUTHENTICATION CERTIFICATE");
        byte[] ctf = ct.getEncoded();
        
        FileOutputStream fosc = new FileOutputStream("certificado.txt");
        fosc.write(ctf);
        fosc.close();
    }
    
    
}
