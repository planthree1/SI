    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Owner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 *
 * @author JPRM
 */
public class TesteO {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException, InvalidKeyException, SignatureException {
        //ler ficheiro
        FileInputStream fis = new FileInputStream("mensagem.txt");
        byte[] mensagem = new byte[fis.available()];
        fis.read(mensagem);
        fis.close();
        /**
        FileInputStream fi = new FileInputStream("mensagem.txt");
        byte[] assinatura = new byte[fi.available()];
        fi.read(assinatura);
        fi.close();
        
        FileInputStream f = new FileInputStream("mensagem.txt");
        byte[] certificado = new byte[f.available()];
        f.read(certificado);
        f.close();
        **/
        File file = new File("mensagem.txt");go
        byte[] ba = new byte[(int) file.length()];
        FileInputStream fisa = new FileInputStream(file);
        fisa.read(ba);
        fisa.close();
        
        System.out.println(ba);
        System.out.println(mensagem);
        //System.out.println(certificado);
        
        validateSignature(mensagem, assinatura, certificado);
    }
    
    public static boolean validateSignature(byte[] document,byte[] signature,byte[] certChavePublica) throws FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance("SHA256withRSA");
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        //não pode ter read pois o mesmo é usado no generateCertificate(fisCertChavePublica)
        InputStream fisCertChavePublica = new ByteArrayInputStream(certChavePublica);
        Certificate cert = cf.generateCertificate(fisCertChavePublica);
        //inicializa o objeto para fazer a verificação da assinatura
        sig.initVerify(cert);
        //é necessário fornecer o documento (tecto em claro) -> byte de array
        sig.update(document);

        //verify da assinatura, devolve um bool
        if(sig.verify(signature)==true){
            return true;
        }
        
        return false;
    }
    
}
