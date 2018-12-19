/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guidecertvalidation;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import java.io.File;
import java.io.IOException;
import java.security.Certificate;
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

/**
 *
 * @author JPRM
 */
public class GuideCertValidation {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, InvalidAlgorithmParameterException {
        ex22();
        ex231();
    }
    
    public static void ex22() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        Provider[] provs = Security.getProviders();
        
        KeyStore ks = KeyStore.getInstance("PKCS11", provs[10]);
        ks.load(null, null);
        
        X509Certificate cf = (X509Certificate) ks.getCertificate("CITIZEN AUTHENTICATION CERTIFICATE");
        
        try {
            //ks.setCertificateEntry("CITIZEN AUTHENTICATION CERTIFICATE", cf);
            //cf.checkValidity(new Date ("15/10/2059"));
            cf.checkValidity();
        } catch (CertificateExpiredException ex) {
            System.out.println("Certificado expirado");
        } catch (CertificateNotYetValidException ex) {
            System.out.println("Certificado ainda não validado");
        }
    }

    private static void ex231() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, InvalidAlgorithmParameterException {
        Provider[] provs = Security.getProviders();
        
        KeyStore ks = KeyStore.getInstance("PKCS11", provs[10]);
        ks.load(null, null);
        
        String filename = System.getProperty("java.home") + "/lib/security/cacerts".replace('/', File.separatorChar);
        System.out.println(filename);
       
        
        PKIXParameters par = new PKIXParameters(ks);
        for(TrustAnchor ta : par.getTrustAnchors() )
        {
            X509Certificate c = ta.getTrustedCert();
            System.out.println( c.getSubjectDN().getName());
        }
    }
}
