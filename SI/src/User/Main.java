/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package User;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException; 
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.util.Scanner;

/**
 *
 * @author TiagoRodrigues
 */
public class Main {
    
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, GeneralSecurityException, UnrecoverableKeyException, Exception, Exception {
        
        //GenerateKeys x = new GenerateKeys(1024);
        //MachineInfo machineInfo = new MachineInfo();
        //x.saveKeys();
        //byte[] info;
        //info = machineInfo.getInfo().toString().getBytes();
        //System.out.println(crip.encrypt(info));

        
        /**
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
        
        **/
        
        Scanner sc = new Scanner(System.in);
        int option = 0;
        
        try{
            do{
                System.out.println("");
                System.out.println("Escolha uma opção");
                System.out.println("1 - Login");
                System.out.println("2 - Registar");
                System.out.println("3 - Ver dados do produto");
                option = sc.nextInt();
            } while(option < 1 || option > 3);
        } catch (Exception e)
        {
            System.out.println("introduz um numero");
        }
        
        Identification identific = new Identification();
        
        switch(option){
            case 1:
                
                break;
                
            case 2:
                
                break;
                
            case 3:
                System.out.println("");
                System.out.println("id do programa: " + identific.getId());
                System.out.println("versão do programa: " +  identific.getVersion());
                break;
        }
    }
   
}
