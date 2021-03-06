/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guidecc;

import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.security.rsa.RSASignature.SHA256withRSA;

/**
 *
 * @author JPRM
 */
public class GuideCC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException, UnrecoverableKeyException {
        Provider[] provs = Security.getProviders();
        for(int i=0; i<provs.length; i++){
            System.out.println(i + " - Nome do provider: " + provs[i].getName());
        }
        System.out.println();
        
        KeyStore ks = KeyStore.getInstance( "PKCS11", provs[10] );
        ks.load( null, null );

        Enumeration<String> als = ks.aliases();
        while (als.hasMoreElements()){
            System.out.println( als.nextElement() );
        }
        
        for (int i = 0; i < provs.length; i++) {
                    if (provs[i].getName().matches("(?i).*SunPKCS11.*")) {
                        ks = KeyStore.getInstance("PKCS11", provs[i].getName());
                    }
                }
        ks.load( null, null );
        Key key = ks.getKey("CITIZEN AUTHENTICATION CERTIFICATE", null);
        System.out.println(key);
        
        //String algoritmo = SHA256withRSA;
        
    }
}
