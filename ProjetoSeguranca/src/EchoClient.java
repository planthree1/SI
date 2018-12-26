/
import si.PasswordEncryption;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EchoClient {

    static Scanner sc = new Scanner(System.in);
    static Socket echoSocket = null;
    static PrintWriter out = null;
    static BufferedReader in = null;
    protected static char[] password;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException, InvalidKeySpecException, InvalidParameterSpecException, CertificateException, UnrecoverableKeyException, GeneralSecurityException, KeyStoreException, FileNotFoundException, CertificateEncodingException, JSONException, Exception {
        JsonObject json = new JsonObject();
        String serverHostname = new String("127.0.0.1");

        if (args.length > 0) {
            serverHostname = args[0];
        }
        System.out.println("Attemping to connect to host "
                + serverHostname + " on port 3000.");

        try {
            // echoSocket = new Socket("taranis", 7);
            echoSocket = new Socket(serverHostname, 3000);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to: " + serverHostname);
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in));
        String userInput = null;
        String error = "";

        System.out.println("1: Login\n2: Register\n3: Exit");
        while ((userInput = sc.nextLine()) != null) {
            if (userInput.equals("3")) {
                System.exit(0);
            }
            if (userInput.equals("2")) {
                //create
                json = new JsonObject();
                json.addProperty("type", "create");

                System.out.println("Nome do novo user: ");
                json.addProperty("uuid", sc.nextLine());
                System.out.println(json);
                out.println(json);
                System.out.println(in.readLine());
                System.out.println("1: Login\n2: Register\n3: Exit");
            } else if (userInput.equals("1")) {
                //falta login
                Hashtable<String, String> logininformation = new Hashtable<String, String>();
                try {
                    JFrame frame = new JFrame();
                    JPanel panel = new JPanel(new BorderLayout(5, 5));

                    JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
                    label.add(new JLabel("Name", SwingConstants.RIGHT));
                    label.add(new JLabel("Password", SwingConstants.RIGHT));
                    panel.add(label, BorderLayout.WEST);

                    JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
                    JTextField username = new JTextField();
                    controls.add(username);
                    JPasswordField passwd = new JPasswordField();
                    controls.add(passwd);
                    panel.add(controls, BorderLayout.CENTER);

                    JOptionPane.showMessageDialog(frame, panel, "login", JOptionPane.QUESTION_MESSAGE);
                    logininformation.put("user", username.getText());
                    logininformation.put("pass", new String(passwd.getPassword()));

                } catch (Exception e) {
                    System.err.println("Password Inválida");
                    System.out.println("1: Login\n2: Register\n3: Exit");
                    continue;
                }
                String decryptedPackage = null;
                try {
                    FileInputStream fisLogin = new FileInputStream(System.getProperty("user.home") + "/NetBeansProjects/ProjetoSeguranca/KeyPair/" + logininformation.get("user") + "/" + logininformation.get("user"));
                    byte[] login = new byte[fisLogin.available()];
                    fisLogin.read(login);

                    decryptedPackage = PasswordEncryption.decrypt(new String(login), logininformation.get("pass"));

                } catch (Exception e) {
                    System.err.println("Login ou Password incorretos");
                    System.out.println("1: Login\n2: Register\n3: Exit");
                    continue;
                }

                JsonObject jo = new JsonParser().parse(decryptedPackage).getAsJsonObject();
                final int myID = Integer.parseInt(jo.get("id").toString());
                //final byte[] privateKey = jo.get("privateKey").toString().substring(1, jo.get("privateKey").toString().length() - 1).getBytes();
                final byte[] privateKey = Base64.getDecoder().decode(jo.get("privateKey").toString().substring(1, jo.get("privateKey").toString().length() - 1));
                final byte[] symmetricKey = Base64.getDecoder().decode(jo.get("symmetricKey").toString().substring(1, jo.get("symmetricKey").toString().length() - 1));

                System.out.print("input: ");
                System.out.println("Available commands (send recv status list exit): ");
                while ((userInput = stdIn.readLine()) != null) {
                    if (userInput.equals("exit")) {
                        System.exit(0);
                    }
                    //send
                    if (userInput.equals("send")) {
                        json = new JsonObject();
                        int dst;
                        //nunca permanece nulo
                        String nameDst = null;

                        System.out.println("Para quem é enviada(uuid): ");
                        getUser();
                        json.addProperty("type", userInput);
                        json.addProperty("src", myID);
                        System.out.print("Nome: ");
                        //try catch para verificar se existe um user com o nome que foi introduzido
                        try {
                            //introduz o nome para quem quer enviar
                            nameDst = sc.nextLine();
                            //vai buscar o id associado ao nome
                            dst = getIDWithUUID(nameDst);
                            json.addProperty("dst", dst);
                        } catch (Exception e) {
                            System.err.println("Não existe utilizador com esse nome!");
                            System.out.println("Available commands (send recv status list exit): ");
                            continue;
                        }
                        System.out.println("Mensagem: ");
                        String mensagem = sc.nextLine();
                        //assinatura
                        JSONObject jsonAssinado = cartaocidadao.Assinar.signDocument(mensagem.getBytes());
                        //System.out.println(jsonAssinado.toString());
                        //cifrar
                        //dst é o id da pessoa para quem quero enviar
                        try {
                            FileInputStream fisPublicKey = new FileInputStream(System.getProperty("user.home") + "/NetBeansProjects/ProjetoSeguranca/KeyPair/" + nameDst + "/publicKey");
                            byte[] publicKey = new byte[fisPublicKey.available()];
                            //public key é o document neste caso
                            fisPublicKey.read(publicKey);

                            FileInputStream fisAssinatura = new FileInputStream(System.getProperty("user.home") + "/NetBeansProjects/ProjetoSeguranca/KeyPair/" + nameDst + "/assinatura");
                            byte[] assinatura = new byte[fisAssinatura.available()];
                            //public key é o document neste caso
                            fisAssinatura.read(assinatura);

                            FileInputStream fisCertChavePublica = new FileInputStream(System.getProperty("user.home") + "/NetBeansProjects/ProjetoSeguranca/KeyPair/" + nameDst + "/certChavePublica");
                            byte[] certChavePublica = new byte[fisCertChavePublica.available()];
                            //public key é o document neste caso
                            fisCertChavePublica.read(certChavePublica);

                            if (cartaocidadao.CartaoCidadao.validateSignature(publicKey, assinatura, certChavePublica) == true) {
                                //Verificação de CRL
                                try {
                                    CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                                    InputStream in = new ByteArrayInputStream(certChavePublica);
                                    X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);
                                    cartaocidadao.ValidateCertUseCRL.verifyCertificateCRLs(cert);

                                    FileInputStream fisCert = new FileInputStream(System.getProperty("user.home") + "/NetBeansProjects/ProjetoSeguranca/KeyPair/" + nameDst + "/certChavePublica");
                                    cartaocidadao.CartaoCidadao.test(fisCert);

                                } catch (Exception e) {
                                    System.err.println("Certificado Revogado!");
                                    System.exit(0);
                                }
                                //cifrar envia-se a chave e o json
                                JSONObject cifrado = cliente.CryptographyUtil.encryptSymetricKey(symmetricKey, publicKey, jsonAssinado.toString().getBytes(StandardCharsets.UTF_8));

                                String base64Cifrado = Base64.getEncoder().encodeToString(cifrado.toString().getBytes());
                                json.addProperty("msg", "\"" + base64Cifrado + "\"");
                                System.out.println("Encryptado com sucesso");
                            } else {
                                System.out.println("Não é possível verificar a integridade da chave pública");
                            }
                        } catch (Exception e) {
                            System.err.println("Ficheiros em falta");
                            System.exit(0);
                        }

                        //cria-se uma cópia e cifra-se com a minha chave privada
                        //para depois poder ver a cópia
                        try {
                            JSONObject cifradoPrivateKey = cliente.CryptographyUtil.encryptSymetricKeyWithPrivateKey(symmetricKey, privateKey, jsonAssinado.toString().getBytes(StandardCharsets.UTF_8));
                            String base64CifradoCopy = Base64.getEncoder().encodeToString(cifradoPrivateKey.toString().getBytes());
                            json.addProperty("copy", base64CifradoCopy);
                        } catch (Exception e) {
                            System.err.println(e + "Something bad happened :C !?");
                        }

                        out.println(json);
                        System.out.println(in.readLine());
                    } else if (userInput.equals("recv")) {
                        //recv
                        json = new JsonObject();
                        json.addProperty("type", userInput);
                        String msgSplit[];
                        String allResult = all(myID);

                        JsonObject allJson = new JsonParser().parse(allResult).getAsJsonObject();
                        JsonArray allParams = allJson.getAsJsonArray("result");
                        allParams = (JsonArray) allParams.get(0);
                        if (allParams.size() == 0) {
                            System.out.println("Não tem nenhuma mensagem!!!");
                            System.out.println("Available commands (send recv status list): ");
                            continue;
                        }
                        System.out.println("Mensagens: " + allParams);

                        System.out.println("Which message; ");
                        String msg = sc.nextLine();

                        json.addProperty("id", myID);
                        json.addProperty("msg", msg);
                        out.println(json);

                        String result = "";
                        try {
                            while ((result += in.readLine()) != null && in.ready()) {

                            }
                        } catch (IOException e) {
                            System.err.println("Error: " + e);
                        }

                        try {
                            //base 64 para json
                            //System.out.println(result);
                            JsonObject encryptedJson = new JsonParser().parse(result).getAsJsonObject();
                            JsonArray params = encryptedJson.getAsJsonArray("result");
                            String str = params.get(1).toString();
                            str = str.substring(1, str.length() - 1);
                            //pega na mensagem selecionada pelo user e vai buscar o id
                            //compara esse id com o que vem no json
                            int verifyId;
                            if (msg.charAt(0) == '_') {
                                msgSplit = msg.split("_");
                                verifyId = Integer.parseInt(msgSplit[1]);
                            } else {
                                msgSplit = msg.split("_");
                                verifyId = Integer.parseInt(msgSplit[0]);
                            }

                            if (Integer.parseInt(params.get(0).toString()) != verifyId) {
                                System.out.println("Aqui houve gato, ID não são iguais");
                                System.exit(0);
                            }
                            
                            try {
                                byte[] decodedBase64 = Base64.getDecoder().decode(str);
                                byte[] decoded = cliente.CryptographyUtil.desencriptar(privateKey, decodedBase64);

                                str = new String(decoded);
                            } catch (Exception e) {
                                System.err.println("Erro a decifrar");
                                System.exit(0);
                            }

                            //str = str.substring(1, str.length() - 1);
                            str = unescapeJavaString(str);
                            JSONObject jsonObj = new JSONObject(str);

                            byte[] document = Base64.getDecoder().decode((String) jsonObj.getJSONArray("document").get(0));
                            byte[] assinatura = Base64.getDecoder().decode((String) jsonObj.getJSONArray("assinatura").get(0));
                            byte[] certChavePublica = Base64.getDecoder().decode((String) jsonObj.getJSONArray("certificadoChavePublica").get(0));

                            if (cartaocidadao.CartaoCidadao.validateSignature(document, assinatura, certChavePublica) == true) {
                                try {
                                    CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                                    InputStream in = new ByteArrayInputStream(certChavePublica);
                                    X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);
                                    cartaocidadao.ValidateCertUseCRL.verifyCertificateCRLs(cert);

                                    FileInputStream fisCert = new FileInputStream(System.getProperty("user.home") + "/NetBeansProjects/ProjetoSeguranca/KeyPair/" + logininformation.get("user") + "/certChavePublica");
                                    cartaocidadao.CartaoCidadao.test(fisCert);

                                } catch (Exception e) {
                                    System.err.println("Certificado Revogado!");
                                    System.exit(1);
                                }
                                System.out.println("Mensagem: " + new String(document));
                                //gerar o recibo
                                json = new JsonObject();
                                json.addProperty("type", "receipt");
                                json.addProperty("id", myID);
                                json.addProperty("msg", msg);
                                json.addProperty("receipt", Base64.getEncoder().encodeToString(assinatura));
                                out.println(json);
                                System.out.println(in.readLine());

                            } else {
                                System.out.println("Wops, 2 bad");
                            }
                        } catch (Exception e) {
                            System.err.println("Algo correu mal!!!!!!");
                            System.exit(0);
                        }

                    } else if (userInput.equals("list")) {
                        json = new JsonObject();
                        json.addProperty("type", userInput);
                        //list
                        json.addProperty("id", 0);
                        System.out.println(json);
                        out.println(json);
                        System.out.println(in.readLine());
                    } else if (userInput.equals("status")) {
                        json = new JsonObject();
                        json.addProperty("type", userInput);
                        String result = all(myID);

                        JsonObject encryptedJson = new JsonParser().parse(result).getAsJsonObject();
                        JsonArray params = encryptedJson.getAsJsonArray("result");
                        params = (JsonArray) params.get(1);
                        System.out.println(params);
                        if (params.size() != 0) {
                            json.addProperty("id", myID);
                            System.out.println("Message id: ");
                            String msg = sc.nextLine();
                            json.addProperty("msg", msg);

                            out.println(json);
                            String status = in.readLine();
                            String str = null;
                            JsonObject statusResult = null;
                            try {
                                statusResult = new JsonParser().parse(status).getAsJsonObject().getAsJsonObject("result");
                                str = statusResult.get("msg").toString();
                                str = str.substring(1, str.length() - 1);
                            } catch (Exception e) {
                                System.err.println("Mensagem que selecionou não existe!");
                            }

                            try {
                                FileInputStream fisPublicKey = new FileInputStream(System.getProperty("user.home") + "/NetBeansProjects/ProjetoSeguranca/KeyPair/" + logininformation.get("user") + "/publicKey");
                                byte[] publicKey = new byte[fisPublicKey.available()];
                                fisPublicKey.read(publicKey);

                                byte[] decodedBase64 = Base64.getDecoder().decode(str);
                                byte[] decoded = cliente.CryptographyUtil.desencriptarChavePublica(publicKey, decodedBase64);

                                str = new String(decoded);
                                str = unescapeJavaString(str);
                                JSONObject jsonObj = new JSONObject(str);

                                byte[] document = Base64.getDecoder().decode((String) jsonObj.getJSONArray("document").get(0));
                                byte[] assinatura = Base64.getDecoder().decode((String) jsonObj.getJSONArray("assinatura").get(0));
                                byte[] certChavePublica = Base64.getDecoder().decode((String) jsonObj.getJSONArray("certificadoChavePublica").get(0));

                                if (cartaocidadao.CartaoCidadao.validateSignature(document, assinatura, certChavePublica) == true) {

                                    //Não sei o que fazer com os recibos
                                    //tenho a assinatura no recibo, para que serve !?
                                    JSONObject modifiedjson = new JSONObject();
                                    modifiedjson.put("msg", new String(document));
                                    modifiedjson.put("receipts", statusResult.get("receipts"));
                                    System.out.println(modifiedjson);

                                } else {
                                    System.out.println("Wops, 2 bad");
                                }

                            } catch (Exception e) {
                                System.err.println("Erro a decifrar");
                                System.out.println("input: Available commands (send recv status list exit)");
                                continue;
                            }

                        } else {
                            System.err.println("Não enviou nenhuma mensagem");
                            System.out.println("input: Available commands (send recv status list exit)");
                        }
                    }
                    System.out.println("Available commands (send recv status list exit): ");
                }

                out.close();
                in.close();
                stdIn.close();
                echoSocket.close();
            } else {
                System.out.println("1: Login\n2: Register");
            }
        }
    }

    public static String all(int id) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("type", "all");
        json.addProperty("id", id);
        out.println(json);
        return in.readLine();
    }

    public static void getUser() throws IOException {
        JsonObject json = new JsonObject();
        JsonObject jo;
        json.addProperty("type", "list");
        out.println(json);
        JsonObject allJson = new JsonParser().parse(in.readLine()).getAsJsonObject();
        JsonArray allParams = allJson.getAsJsonArray("data");
        System.out.println("Pessoas disponíveis: ");
        for (int i = 0; i < allParams.size(); i++) {
            jo = new JsonParser().parse(allParams.get(i).toString()).getAsJsonObject();
            System.out.print(jo.get("uuid").toString().substring(1, jo.get("uuid").toString().length() - 1) + " ");
        }
        System.out.println("");
    }

    public static int getIDWithUUID(String uuid) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("type", "check");
        json.addProperty("uuid", uuid);
        out.println(json);
        String str = in.readLine();
        str = str.substring(1, str.length() - 1);
        JsonObject jo = new JsonParser().parse(str).getAsJsonObject();
        return Integer.parseInt(jo.get("id").toString());
    }

    public static String unescapeJavaString(String st) {

        StringBuilder sb = new StringBuilder(st.length());

        for (int i = 0; i < st.length(); i++) {
            char ch = st.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == st.length() - 1) ? '\\' : st
                        .charAt(i + 1);
                // Octal escape?
                if (nextChar >= '0' && nextChar <= '7') {
                    String code = "" + nextChar;
                    i++;
                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                            && st.charAt(i + 1) <= '7') {
                        code += st.charAt(i + 1);
                        i++;
                        if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                                && st.charAt(i + 1) <= '7') {
                            code += st.charAt(i + 1);
                            i++;
                        }
                    }
                    sb.append((char) Integer.parseInt(code, 8));
                    continue;
                }
                switch (nextChar) {
                    case '\\':
                        ch = '\\';
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case '\"':
                        ch = '\"';
                        break;
                    case '\'':
                        ch = '\'';
                        break;
                    // Hex Unicode: u????
                    case 'u':
                        if (i >= st.length() - 5) {
                            ch = 'u';
                            break;
                        }
                        int code = Integer.parseInt(
                                "" + st.charAt(i + 2) + st.charAt(i + 3)
                                + st.charAt(i + 4) + st.charAt(i + 5), 16);
                        sb.append(Character.toChars(code));
                        i += 5;
                        continue;
                }
                i++;
            }
            sb.append(ch);
        }
        return sb.toString();
    }
}
