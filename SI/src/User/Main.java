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
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException; 
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author TiagoRodrigues
 */
public class Main {
    
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, GeneralSecurityException, UnrecoverableKeyException, Exception, Exception {
        
        GenerateKeys x = new GenerateKeys(1024);
        x.saveKeys();
        
        String info;
        
        MachineInfo y = new MachineInfo();
        
        info = y.getInfo().toString();
        Criptografia crip = new Criptografia();
        
        crip.encrypt(info);
        
        byte[] inp = x.readFromFile("KeyPairUser/simetrickey");
        
        crip.decrypt(inp);
        
    }
    
}
