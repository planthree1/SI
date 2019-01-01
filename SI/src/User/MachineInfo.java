/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package User;
import java.io.IOException;
import java.util.Scanner;

import com.google.gson.JsonObject;
/**
 *
 * @author TiagoRodrigues
 */
public class MachineInfo {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */    
    public JsonObject getInfo(){
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
        
        info = biosNumber + "/" + uuid + "/" + unixTime;
        
        JsonObject obj = new JsonObject();
        
        obj.addProperty("biosNumber", biosNumber);
        obj.addProperty("uuid", uuid);
        obj.addProperty("unixTime", unixTime);
        
        //System.out.println(obj);   
        return obj;
    }
}
