/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validacaocertificados;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tjsantos
 */
public class ValidacaoCertificados {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, InvalidAlgorithmParameterException {
        
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
        
        //2.2
        X509Certificate ct = (X509Certificate) ks.getCertificate("CITIZEN AUTHENTICATION CERTIFICATE");
        
        try {
            //ks.setCertificateEntry("CITIZEN AUTHENTICATION CERTIFICATE", ct);
            ct.checkValidity(new Date("12/13/52"));
            //ct.checkValidity();
        } catch (CertificateExpiredException ex) {
            System.out.println("Certificado Expirado");
        } catch (CertificateNotYetValidException ex) {
            System.out.println("Certificado ainda não Válido");
        }
       
        //2.3.1 //TODO
        /*String filename = System.getProperty("java.home") + "/lib/security/cacerts".replace('/', File.separatorChar);
        System.out.println(filename);
        
        PKIXParameters par = new PKIXParameters(ks);
        for(TrustAnchor ta : par.getTrustAnchors() )
        {
            X509Certificate c = ta.getTrustedCert();
            System.out.println( c.getSubjectDN().getName());
        }*/

    }
   
}
