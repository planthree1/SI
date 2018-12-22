package cartaocidadao;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.*;
import java.security.cert.*;
import java.util.*;
import java.security.cert.X509Certificate;
import java.security.cert.PKIXParameters;
import java.util.jar.Attributes;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Extensions;

/**
 * Check the revocation status of a public key certificate using a CRL.
 *
 * NOTE: it only works with V1 CRLs
 */
public class ValidateCertUseCRL {

     public static void verifyCertificateCRLs(X509Certificate cert)
            throws CertificateVerificationException {
        try {
            List<String> crlDistPoints = getCrlDistributionPoints(cert);
            for (String crlDP : crlDistPoints) {
                X509CRL crl = downloadCRL(crlDP);
                if (crl.isRevoked(cert)) {
                    throw new CertificateVerificationException(
                            "The certificate is revoked by CRL: " + crlDP);
                }else {
                    System.out.println("Verification Passed");
                }
            }
        } catch (Exception ex) {
            if (ex instanceof CertificateVerificationException) {
                throw (CertificateVerificationException) ex;
            } else {
                throw new CertificateVerificationException(
                        "Can not verify CRL for certificate: "
                        + cert.getSubjectX500Principal());
            }
        }
    }

    //////////// CRL exemplo tirado e adoptado da net
    public static List<String> getCrlDistributionPoints(X509Certificate cert)
            throws CertificateParsingException, IOException {
        byte[] crldpExt = cert.getExtensionValue(
                X509Extensions.CRLDistributionPoints.getId());
        ASN1InputStream oAsnInStream = new ASN1InputStream(
                new ByteArrayInputStream(crldpExt));

        ASN1Primitive derObjCrlDP = oAsnInStream.readObject();
        DEROctetString dosCrlDP = (DEROctetString) derObjCrlDP;
        byte[] crldpExtOctets = dosCrlDP.getOctets();

        ASN1InputStream oAsnInStream2 = new ASN1InputStream(
                new ByteArrayInputStream(crldpExtOctets));

        ASN1Primitive derObj2 = oAsnInStream2.readObject();
        CRLDistPoint distPoint = CRLDistPoint.getInstance(derObj2);
        List<String> crlUrls = new ArrayList<String>();
        for (DistributionPoint dp : distPoint.getDistributionPoints()) {
            System.out.println(dp);
            DistributionPointName dpn = dp.getDistributionPoint();
            // Look for URIs in fullName
            if (dpn != null) {
                if (dpn.getType() == DistributionPointName.FULL_NAME) {
                    GeneralName[] genNames = GeneralNames.getInstance(
                            dpn.getName()).getNames();
                    // Look for an URI
                    for (int j = 0; j < genNames.length; j++) {
                        if (genNames[j].getTagNo() == GeneralName.uniformResourceIdentifier) {
                            String url = DERIA5String.getInstance(
                                    genNames[j].getName()).getString();
                            crlUrls.add(url);
                        }
                    }
                }
            }
        }
        return crlUrls;
    }
    
    private static X509CRL downloadCRL(String crlURL) throws IOException,
            CertificateException, CRLException,
            CertificateVerificationException, NamingException {
        if (crlURL.startsWith("http://") || crlURL.startsWith("https://")
                || crlURL.startsWith("ftp://")) {
            X509CRL crl = downloadCRLFromWeb(crlURL);
            return crl;
        } else if (crlURL.startsWith("ldap://")) {
            X509CRL crl = downloadCRLFromLDAP(crlURL);
            return crl;
        } else {
            throw new CertificateVerificationException(
                    "Can not download CRL from certificate "
                    + "distribution point: " + crlURL);
        }
    }

    private static X509CRL downloadCRLFromLDAP(String ldapURL)
            throws CertificateException, NamingException, CRLException, CertificateVerificationException
             {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapURL);

        DirContext ctx = new InitialDirContext(env);
        Attributes avals = (Attributes) ctx.getAttributes("");
        Attributes aval = (Attributes) avals.get("certificateRevocationList;binary");
        byte[] val = (byte[]) aval.get(aval);
        if ((val == null) || (val.length == 0)) {
            throw new CertificateVerificationException(
                    "Can not download CRL from: " + ldapURL);
        } else {
            InputStream inStream = new ByteArrayInputStream(val);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509CRL crl = (X509CRL) cf.generateCRL(inStream);
            return crl;
        }
    }

    private static X509CRL downloadCRLFromWeb(String crlURL)
            throws MalformedURLException, IOException, CertificateException,
            CRLException {
        URL url = new URL(crlURL);
        InputStream crlStream = url.openStream();
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509CRL crl = (X509CRL) cf.generateCRL(crlStream);
            return crl;
        } finally {
            crlStream.close();
        }
    }

    public static class CertificateVerificationException extends Exception {

        private static final long serialVersionUID = 1L;

        public CertificateVerificationException(String message, Throwable cause) {
            super(message, cause);
        }

        public CertificateVerificationException(String message) {
            super(message);
        }
    }

}
