/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cartaocidadao;

import static cartaocidadao.CartaoCidadao.getPrivateKeyFromCard;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Enumeration;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author borys
 */
public class Assinar {
    
    public static JSONObject signDocument(byte[] document) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, GeneralSecurityException, FileNotFoundException, CertificateEncodingException, JSONException{
        JSONObject json = new JSONObject();
        toBase64(json, document, "document");
        
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        Provider[] provs = Security.getProviders();
        Provider prov = Security.getProvider("SunPKCS11-CartaoCidadao");
        ks = KeyStore.getInstance("PKCS11", prov);
        ks.load(null, null);
        PrivateKey chavePrivadaCC = getPrivateKeyFromCard(ks,json);
        
        System.out.println(chavePrivadaCC);
        
        
        byte[] digitalSignature = assinatura(document, chavePrivadaCC);
        toBase64(json,digitalSignature,"assinatura");
        
        return json;
        
    }
    
    public static PrivateKey getPrivateKeyFromCard(KeyStore ks,JSONObject json) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, FileNotFoundException, CertificateEncodingException, IOException, JSONException {
        Enumeration aliasesEnum = ks.aliases();
        while (aliasesEnum.hasMoreElements()) {
            String alias = (String) aliasesEnum.nextElement();
            System.out.println("Alias: " + alias);
            X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
            //subject -> dono do certeficado
            System.out.println("Certificate: " + cert.getSubjectDN().getName());
            //quem emitiu o certeficado
            System.out.println("Certificate: " + cert.getIssuerDN().getName());
            System.out.print("\n");
            PrivateKey privateKey
                    = (PrivateKey) ks.getKey(alias, null);
            System.out.println("Private key: " + privateKey);

            if (alias.equals("CITIZEN AUTHENTICATION CERTIFICATE")) {
                byte[] certificadoChavePublica = cert.getEncoded();
                toBase64(json,certificadoChavePublica,"certificadoChavePublica");
                return privateKey;
            }

        }
        return null;
    }
    
    private static byte[] assinatura(byte[] aDocument, PrivateKey aPrivateKey) throws GeneralSecurityException {
        Signature signatureAlgorithm = Signature.getInstance("SHA256withRSA");
        signatureAlgorithm.initSign(aPrivateKey);
        signatureAlgorithm.update(aDocument);
        byte[] digitalSignature = signatureAlgorithm.sign();
        return digitalSignature;
    }
    
    public static JSONObject toBase64(JSONObject json,byte[] test ,String name) throws JSONException {

        String encoded = Base64.getEncoder().encodeToString(test);
        json.append(name, encoded);

        return json;
    }
}
