/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException; 
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.util.Scanner;
import java.util.logging.Le çvel;
import java.util.logging.Logger;

/**
 *
 * @author TiagoRodrigues
 */
public class Main {
    
    public static void main(String[] args) throws IOException, Exception {
        
        GenerateKeys x = new GenerateKeys(1024);
        MachineInfo machineInfo = new MachineInfo();
        Criptografia crip = new Criptografia();
        x.saveKeys();
        String info;
        info = machineInfo.getInfo();
        System.out.println(crip.encrypt(info));

        
        /*
        Criptografia crip = new Criptografia();
        ReadKeys readkey = new ReadKeys();
        
        //gera uma chave AES
        Key key = crip.generateKey();
        //passa a chave para byte[]
        byte [] chave = key.getEncoded();
        
        
        //le as chaves
        PublicKey publicKey = readkey.readPublicKey("KeyPairUser/publicKey");
        PrivateKey privateKey = readkey.readPrivateKey("KeyPairUser/privateKey");
        
        //encripta a chave AES
        byte[] secret = readkey.encrypt(publicKey, chave);
        //desencripta a chave AES
        byte[] recovered_message = readkey.decrypt(privateKey, secret);
        System.out.println(new String(recovered_message, "UTF8"));
        */

        
        /*
        Scanner sc = new Scanner(System.in);
        int option = 0;
        
        try{
            do{
                System.out.println("");
                System.out.println("Escolha uma opção");
                System.out.println("1 - Login");
                System.out.println("2 - Registar");
                System.out.println("3 - Ver dados do produto");
                System.out.println("4 - Exit");
                option = sc.nextInt();
                
                Identification identific = new Identification();
                MachineInfo machineinfo = new MachineInfo();
        
                switch(option){
                    case 1:
                        System.out.println("login");
                        
                        break;

                    case 2:
                        System.out.println("Registo");
                        System.out.print("Nome: ");
                        String nome = sc.next();

                        //criar ficheiro com o nome do utilizador
                        File ficheiro = new File("Users/" + nome + ".txt");
                        //se existir
                        if (ficheiro.exists()) {
                            System.out.println("Utilizador já existe.");
                            break;
                        }
                        
                        //se não existir continua
                        System.out.print("Password: ");
                        String pass = sc.next();
                        
                        //escrever no ficheiro
                        try {
                            FileWriter user = new FileWriter("Users/" + nome + ".txt");
                            //pass
                            
                            Criptografia criptografia = new Criptografia();
                            
                            user.write(criptografia.cryptWithMD5(pass));
                            user.close();
                            FileWriter pedido = new FileWriter("Users/" + nome + "Pedido.txt");
                            //informação da máquina usada/informação do programa
                            pedido.write(machineinfo.getInfo() + "/" + identific.getId() + "/" + identific.getVersion());
                            pedido.close();
                        } catch (IOException e) {
                            System.out.println("An error occurred.");
                            e.printStackTrace();
                        }
                        
                        System.out.print("");
                        
                        break;

                    case 3:
                        System.out.println("");
                        System.out.println("id do programa: " + identific.getId());
                        System.out.println("versão do programa: " +  identific.getVersion());
                        break;
                    
                    case 4:
                        System.out.println("good bey");
                        break;
                }
            } while(option!=4);
        } catch (Exception e) {
            System.out.println("introduz um numero");
        }
        */
    }
    
}