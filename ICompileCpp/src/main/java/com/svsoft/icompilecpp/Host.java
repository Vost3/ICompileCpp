/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.svsoft.icompilecpp;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

/**
 *
 * @author Steve Vogel
 */
public class Host {
    
    private Crypt crp = null; 
    
    private String path = null;
    
    private File jsonF = null;
    
    private JSONObject json = null;
    
    private String ip = null;
    private String login = null;
    private String password = null;    
                
    
    public Host(String ip){
        crp = new Crypt();
        
        boolean created = false;
        path = System.getProperty("user.home")+File.separator;
        path += ".svsoft";
        path += File.separator + "icompilecpp";
        
        // Create directory if not exist
        File dir = new File(path);
        if( dir.exists() == false )
            dir.mkdir();        
            created = true;
        
        this.ip = ip;
        path += File.separator+this.ip+".json";
        
        jsonF = new File(path);     
        
        openFile();
    }
    
    /**
     * read data in json file and set property of current object
     */
    private void readData(){                        
        
        String content = null;
        try {
            content = FileUtils.readFileToString(jsonF, "utf-8");
        } catch (IOException ex) {
            // @TODO: log that
            //Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // File content is empty
        if( content.trim().length() == 0 )
            return;
        
        // Convert JSON string to JSONObject
        json = new JSONObject(content); 
        login = crp.decrypt(json.getString("login"));
        password = crp.decrypt(json.getString("password"));
    }
    
    /**
     * Open and init if not exist
     */
    private void openFile(){
        boolean created = false;
                
        if( jsonF.exists() == false ){
            try {
                jsonF.createNewFile();                
                created = true;
            } catch (IOException ex) {
                // @TODO: log that
                //Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                                  
        readData();        
    }
    
    /**
     * save json content
     * 
     * @param login
     * @param password 
     */
    public void saveData(String login, String password){
        long currentTimestamp = System.currentTimeMillis();        
        
        // Create Json Object
        JSONObject json = new JSONObject(); 
        json.put("login", crp.encrypt(login));
        json.put("password", crp.encrypt(password));
        json.put("timestamp", currentTimestamp);
        
        String content = json.toString();
        try {
            FileUtils.writeStringToFile(jsonF, content, "utf-8");
        } catch (IOException ex) {
            // @TODO: log that
            // Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
