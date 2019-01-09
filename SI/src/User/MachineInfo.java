/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package User;
import java.io.IOException;
import java.util.Scanner;

import com.google.gson.JsonObject;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
/**
 *
 * @author TiagoRodrigues
 */
public class MachineInfo {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    
    public Boolean verifymachine() throws IOException{
        
        String x = getInfoWithoutSave();
        
        String content;

        content = new String(Files.readAllBytes(Paths.get("mensagem.txt")));
        
        String maquina = x.substring(0, x.length() - 10);
        String teste = content.substring(0, content.length() - 10);
        
        if (maquina.equals(teste)){
            System.out.println("é igual");
            return true; 
        }
        System.out.println("nao deu");
        return false;
    };
    
    public String getInfoWithoutSave(){
        String info;
        //opens the wmic and check the bio biosNumber number
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "wmic", "bios", "get", "SerialNumber" });

            process.getOutputStream().close();
        } catch (IOException ex) {
            System.out.println(ex);
        }

        Scanner sc = new Scanner(process.getInputStream());
        String property = sc.next();
        String biosNumber = sc.next();
        //System.out.println(property + ": " + biosNumber);
        
      
        
        //same thing but with the UUID (identificadodr unico universal)
        try {
            process = Runtime.getRuntime().exec(new String[] { "wmic", "csproduct", "get", "UUID" });

            process.getOutputStream().close();
        } catch (IOException ex) {
            System.out.println(ex);
        }

        Scanner sc1 = new Scanner(process.getInputStream());
        String property1 = sc1.next();
        String uuid = sc1.next();
        
        
        //System.out.println(property1 + ": " + uuid);
        long unixTime = System.currentTimeMillis() / 1000L;
        
        String mensage = biosNumber+ "/" + uuid + "/" + unixTime;
        return mensage;
    };
    
    
    public String getInfo() throws IOException{
        String info;
        //opens the wmic and check the bio biosNumber number
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "wmic", "bios", "get", "SerialNumber" });

            process.getOutputStream().close();
        } catch (IOException ex) {
            System.out.println(ex);
        }

        Scanner sc = new Scanner(process.getInputStream());
        String property = sc.next();
        String biosNumber = sc.next();
        //System.out.println(property + ": " + biosNumber);
        
      
        
        //same thing but with the UUID (identificadodr unico universal)
        try {
            process = Runtime.getRuntime().exec(new String[] { "wmic", "csproduct", "get", "UUID" });

            process.getOutputStream().close();
        } catch (IOException ex) {
            System.out.println(ex);
        }

        Scanner sc1 = new Scanner(process.getInputStream());
        String property1 = sc1.next();
        String uuid = sc1.next();
        
        
        //System.out.println(property1 + ": " + uuid);
        long unixTime = System.currentTimeMillis() / 1000L;
        
        String mensage = biosNumber+ "/" + uuid + "/" + unixTime;
        byte[] mensagem = mensage.getBytes();
        FileOutputStream fos = new FileOutputStream("mensagem.txt");
        fos.write(mensagem);
        fos.close();
        
        
        return mensage;
    }
}
