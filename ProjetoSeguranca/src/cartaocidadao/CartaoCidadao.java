package cartaocidadao;
import cartaocidadao.ValidateCertUseCRL;
import java.io.ByteArrayInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Enumeration;


/**
 *
 * @author borys
 */
public class CartaoCidadao {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws KeyStoreException, NoSuchProviderException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, GeneralSecurityException, InvalidAlgorithmParameterException, FileNotFoundException, ValidateCertUseCRL.CertificateVerificationException {
        //Vai listar os providers
        //para funcionar corretamente é necessário ter o leitor de CC ligado ao pc
        Provider[] provs = Security.getProviders();
//        for (int i = 0; i < provs.length; i++) {
//            System.out.println(i + " - Nome do provider: " + provs[i].getName());
//        }
        //vai buscar um provider em especifico SunPKCS11-CartaoCidadao
        //este foi adicionado no ficheiro java.security
        Provider prov = Security.getProvider("SunPKCS11-CartaoCidadao");
        KeyStore ks = KeyStore.getInstance("PKCS11", prov);
        ks.load(null, null);
//
//        //metodo que vai validar se o certificado encontra-se valido neste momento
//        //este necessita de uma keystore que é onde guarda o certificado
//        validity(ks);

//        PrivateKey pk = getPrivateKeyFromCard(ks);
//
//        FileInputStream fis = new FileInputStream("/home/borys/NetBeansProjects/CartaoCidadao/extra/document");
//        byte[] document = new byte[fis.available()];
//        fis.read(document);
//        //corre o metodo para criar a assinatura
//        byte[] digitalSignature = assinatura(document, pk);
//
//        FileOutputStream fos = new FileOutputStream("/home/borys/NetBeansProjects/CartaoCidadao/extra/assinatura");
//        fos.write(digitalSignature);
//        fos.close();
        getInfo(ks);

    }
    
//    public boolean run(String msg) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, GeneralSecurityException{
//        Provider[] provs = Security.getProviders();
//        Provider prov = Security.getProvider("SunPKCS11-CartaoCidadao");
//        KeyStore ks = KeyStore.getInstance("PKCS11", prov);
//        ks.load(null, null);
//        
//        PrivateKey pk = getPrivateKeyFromCard(ks);
//
//        byte[] document = msg.getBytes(Charset.forName("UTF-8"));
//        //corre o metodo para criar a assinatura
//        byte[] digitalSignature = assinatura(document, pk);
//
//        FileOutputStream fos = new FileOutputStream("/home/borys/NetBeansProjects/CartaoCidadao/extra/assinatura");
//        fos.write(digitalSignature);
//        fos.close();
//    }

    public static void signForEncryption(byte[] chavePublica,String path) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, GeneralSecurityException{
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        Provider[] provs = Security.getProviders();
        Provider prov = Security.getProvider("SunPKCS11-CartaoCidadao");
        ks = KeyStore.getInstance("PKCS11", prov);
        ks.load(null, null);
        PrivateKey chavePrivadaCC = getPrivateKeyFromCard(ks,path);
        
        
        byte[] digitalSignature = assinatura(chavePublica, chavePrivadaCC);

        FileOutputStream fos = new FileOutputStream(path+"/assinatura");
        fos.write(digitalSignature);
        fos.close();
        
    }
 
    private static byte[] assinatura(byte[] aDocument, PrivateKey aPrivateKey) throws GeneralSecurityException {
        Signature signatureAlgorithm = Signature.getInstance("SHA256withRSA");
        signatureAlgorithm.initSign(aPrivateKey);
        signatureAlgorithm.update(aDocument);
        byte[] digitalSignature = signatureAlgorithm.sign();
        return digitalSignature;
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

    public static void getInfo(KeyStore ks) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, ValidateCertUseCRL.CertificateVerificationException {
        Enumeration<String> als = ks.aliases();
        while (als.hasMoreElements()) {
            System.out.println(als.nextElement());
            String alias = (String) als.nextElement();
            X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
            
            ValidateCertUseCRL.verifyCertificateCRLs(cert);
            
            //System.out.println(cert);
        }
    }

    public static PrivateKey getPrivateKeyFromCard(KeyStore ks, String path) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, FileNotFoundException, CertificateEncodingException, IOException {
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
                FileOutputStream fos = new FileOutputStream(path+"/certChavePublica");
                byte[] certificadoChavePublica = cert.getEncoded();
                fos.write(certificadoChavePublica);
                fos.close();
                return privateKey;
            }

        }
        return null;
    }

    //Este metodo vai verificar se o certificado encontra-se valido na presente data
    public static void validity(KeyStore ks) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, FileNotFoundException, CertificateEncodingException, IOException, CertificateExpiredException, CertificateNotYetValidException {
        Enumeration aliasesEnum = ks.aliases();
        while (aliasesEnum.hasMoreElements()) {
            String alias = (String) aliasesEnum.nextElement();
            X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
            try {
                cert.checkValidity();
            } catch (CertificateExpiredException | CertificateNotYetValidException e) {
                System.out.println(e);
            }
        }
    }

    //metodo que vai buscar a raiz e ler os cert intermediarios a partir de uma pasta
    //chama-se test devido à falta de originalidade
    public static void test(FileInputStream fisCert) throws KeyStoreException, IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, CertificateException, FileNotFoundException, ValidateCertUseCRL.CertificateVerificationException {
        String filename = System.getProperty("java.home") + "/lib/security/cacerts".replace('/', File.separatorChar);

        //criar o keystore vazio
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

        //precisa-se de carregar a raiz na qual confiamos para o ks, faz-se isso com inputStream
        FileInputStream fisCertRaiz = new FileInputStream(filename);
        char[] password = "changeit".toCharArray();
        //fez-se load dos certificados da raiz para o keystore
        ks.load(fisCertRaiz, password);

        PKIXParameters par = new PKIXParameters(ks);
        for (TrustAnchor ta : par.getTrustAnchors()) {
            X509Certificate c = ta.getTrustedCert();
            //System.out.println(c.getSubjectDN().getName());
        }

//////////ler certificados a partir de uma pasta e guardar num keystore
        KeyStore ksPasta = KeyStore.getInstance(KeyStore.getDefaultType());
        ksPasta.load(null); //empty keystore

        final File folder = new File(System.getProperty("user.home")+"/NetBeansProjects/ProjetoSeguranca/allCert");

        for (Certificate c : getCertsFromFolder(folder)) {
            X509Certificate xc = (X509Certificate) c;
            System.out.println(xc.getSubjectDN().getName());
            ksPasta.setCertificateEntry(xc.getSubjectDN().getName(), c);
        }

        //certificado tem que ser um x509 certificate
        certChainPath(fisCert, ks, getCertsFromFolder(folder));

    }

    public static Certificate[] getCertsFromFolder(final File folder) throws CertificateException, FileNotFoundException, ValidateCertUseCRL.CertificateVerificationException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate[] certArray = new Certificate[folder.listFiles().length];
        int count = 0;

        //vai buscar todos os certificados
        for (final File fileEntry : folder.listFiles()) {
            FileInputStream fis = new FileInputStream(folder + "/" + fileEntry.getName());
            Certificate cert = cf.generateCertificate(fis);
            
            //validação do CRL
            ValidateCertUseCRL.verifyCertificateCRLs((X509Certificate) cert);
            
            certArray[count] = cert;
            count++;
        }
        return certArray;
    }

    //vai verificar se um certificado encontra-se valido
    //para tal precisa de certificados de raiz
    //e certificados intermediarios
    public static void certChainPath(FileInputStream fisMyCert, KeyStore ks, Certificate[] iCerts) throws NoSuchAlgorithmException, KeyStoreException, InvalidAlgorithmParameterException, CertificateException, ValidateCertUseCRL.CertificateVerificationException {
        //recebe um inputStream(certificado que se quer validar) e converte-o para x509 cert 
        //a conversão é feita com o CertificateFactory
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate myCert = (X509Certificate) certFactory.generateCertificate(fisMyCert);

        //valida com as CRL
        ValidateCertUseCRL.verifyCertificateCRLs(myCert);
        
        //defines the end-user certificate as a selector
        X509CertSelector cs = new X509CertSelector();
        cs.setCertificate((X509Certificate) myCert);
        //Create an object to build the certification path
        CertPathBuilder cpb = CertPathBuilder.getInstance("PKIX");
        //Define the parameters to buil the certification path and
        //provide the Trust anchor certificates (trustAnchors) and
        //the end user certificate (cs)
        //todos os certificados da ancora ficam guardados num keystream que é passado para
        //o seguinte metodo (PKIXBuilderParameters)
        PKIXBuilderParameters pkixBParams = new PKIXBuilderParameters(ks, cs);
        pkixBParams.setRevocationEnabled(false); //No revocation check
        //Provide the intermediate certificates (iCerts)
        CollectionCertStoreParameters ccsp = new CollectionCertStoreParameters(Arrays.asList(iCerts));
        CertStore store = CertStore.getInstance("Collection", ccsp);
        pkixBParams.addCertStore(store);
        //Build the certification path
        CertPath cp = null;
        try {
            CertPathBuilderResult cpbr = cpb.build(pkixBParams);
            cp = cpbr.getCertPath();
            System.out.println("Certification path built with success!");
        } catch (CertPathBuilderException ex) {
            System.out.println(
                    "It was not possible to build a certification path!");
        }
        /////////
        //depois de Construir um caminho de certificação
        //procede-se à vlaidação
        validateCertChainPath(ks, cp);
    }

    public static void validateCertChainPath(KeyStore trustAnchors, CertPath cp) throws NoSuchAlgorithmException, KeyStoreException, InvalidAlgorithmParameterException {
        PKIXParameters pkixParams = new PKIXParameters(trustAnchors);
        //Class that performs the certification path validation
        CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
        //Disables the previous mechanism for revocation check (pre Java8)
        pkixParams.setRevocationEnabled(true);
        //Enable OCSP verification
        Security.setProperty("ocsp.enable", "true");
        //Instantiate a PKIXRevocationChecker class
        PKIXRevocationChecker rc = (PKIXRevocationChecker) cpv.getRevocationChecker();
        //Configure to validate all certificates in chain using only OCSP
        rc.setOptions(EnumSet.of(
                PKIXRevocationChecker.Option.SOFT_FAIL,
                PKIXRevocationChecker.Option.NO_FALLBACK));
        PKIXCertPathValidatorResult result = null;
        try {
            //Do the velidation
            result = (PKIXCertPathValidatorResult) cpv.validate(cp, pkixParams);
            System.out.println("Certificado Válido");
            System.out.println("Issuer of trust anchor certificate: "
                    + result.getTrustAnchor().getTrustedCert().getIssuerDN().getName());
        } catch (CertPathValidatorException cpve) {
            System.out.println("Validation failure, cert["
                    + cpve.getIndex() + "] :" + cpve.getMessage());
        }
    }
}
