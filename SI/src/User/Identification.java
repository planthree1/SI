/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package User;

/**
 *
 * @author TiagoRodrigues
 */
public class Identification {
    
    String id = "1";
    String version = "2.0";

    Identification ( String id, String name)
    {
        this.id = id;
        this.version = version;
    }

    Identification() {
        
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}