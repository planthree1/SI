/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package User;

import com.google.gson.JsonObject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
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
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Enumeration;
import sun.misc.BASE64Encoder;

public class Teste {
    
    public static void main(String[] args) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, InvalidKeyException, SignatureException, GeneralSecurityException {
        /*byte[] yte = {'T', 'h'};
        signDocument(yte);
        */
        Provider[] provs = Security.getProviders();
        KeyStore ks = KeyStore.getInstance( "PKCS11", provs[10] );
        ks.load( null, null );
 
        Enumeration<String> als = ks.aliases();
        
        
        // Criar assinatura digital de um documento
       
        MachineInfo machineinfo = new MachineInfo();
        
        //ler ficheiro
        FileInputStream fis = new FileInputStream("mensagem.txt");
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
        
        System.out.println("assinatura criada");
       
        // Guardar certificado de chave publica noutro ficheiro
        Certificate ct = ks.getCertificate("CITIZEN AUTHENTICATION CERTIFICATE");
        byte[] ctf = ct.getEncoded();
       
        FileOutputStream fosc = new FileOutputStream("certificado.txt");
        fosc.write(ctf);
        fosc.close();
        
        System.out.println("certificado criado");
        
        
/*    
        getPrivateKeyFromCard(ks, machineinfo.getInfo());*/
    }
    /*
    public static JsonObject signDocument(byte[] document) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, GeneralSecurityException, FileNotFoundException, CertificateEncodingException {
        JsonObject json = new JsonObject();
        toBase64(json, document, "document");
        
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        Provider[] provs = Security.getProviders();
        Provider prov = Security.getProvider("SunPKCS11-CartaoCidadao");
        ks = KeyStore.getInstance("PKCS11", prov);
        ks.load(null, null);
        PrivateKey chavePrivadaCC = getPrivateKeyFromCard(ks,json);
        
        System.out.println(chavePrivadaCC + ".i.");
        
        
        byte[] digitalSignature = assinatura(document, chavePrivadaCC);
        toBase64(json,digitalSignature,"assinatura");
        
        System.out.println(json + ".i.");
        
        return json;
        
    }
    
    public static PrivateKey getPrivateKeyFromCard(KeyStore ks,JsonObject json) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, FileNotFoundException, CertificateEncodingException, IOException {
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
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, null);
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
    
    public static JsonObject toBase64(JsonObject json,byte[] test ,String name) {

        String encoded = Base64.getEncoder().encodeToString(test);
        json.addProperty(name, encoded);

        return json;
    }
    */
}
