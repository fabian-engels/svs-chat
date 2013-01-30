/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bhtberlin.svschatclient2;

/**
 *
 * @author nto
 */
public class Command {

    private final String value;
    private final String usage;
    public Command (String value, String usage){
        this.value = value;
        this.usage = usage;
    }
    public Command (String value){
        this.value = value;
        this.usage = "";
    }
    
    public String getValue(){
        return this.value;
    }
    
    public String getUsage(){
        return this.usage;
    }
            
    @Override
    public String toString() {
        return this.value;
    }
    
}
