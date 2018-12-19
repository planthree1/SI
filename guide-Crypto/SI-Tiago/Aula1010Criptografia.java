/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aula10.pkg10.criptografia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 *
 * @author tjsantos
 */
public class Aula1010Criptografia {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // TODO code application logic here
        
        /*String fileName = "simetricKey" + ".crypt-sim";
        twoTwoTwo firstEx = new twoTwoTwo("DES", fileName);*/
        
        /*String nomeFicheiroCifrar = "textoClaro.txt", nomePretendidoFicheiroCifrado = "Criptograma.txt", nomeFicheiroComChave = "simetricKey.crypt-sim";
        twoTwoThree secondEx = new twoTwoThree(nomeFicheiroCifrar, nomePretendidoFicheiroCifrado, nomeFicheiroComChave, "DES");
        */
        
       twotwoFour thirdEx = new twoTwoFour();
    }
    
    
    
}
