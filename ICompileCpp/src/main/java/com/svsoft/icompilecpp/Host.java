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
    
    private Crypt crypt = null; 

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }
    
    private String path = "";
    
    private String dirPath = "";
    
    private File f = null;   
    
    /**
     * ip of IBMi
     * this is the name of json file too
     */
    private String ip = null;
       
    private String login = null;
    
    private String password = null;    
                
    private boolean testMode = false;
    
    public Host(String ip){
        crypt = new Crypt();
                
        dirPath = System.getProperty("user.home")+File.separator;
        dirPath += ".svsoft";
        dirPath += File.separator + "icompilecpp";
        
        // Create directory if not exist
        File dir = new File(dirPath);
        if( dir.exists() == false )
            dir.mkdir();                    
        
        this.ip = ip;
        path += dirPath + File.separator + this.ip + ".json";
        
        f = new File(path);     
        
        openFile();
    }
    
    
    /**
     * Open and init if not exist
     */
    private void openFile(){
        boolean created = false;
                
        if( f.exists() == false ){
            try {
                f.createNewFile();                
                created = true;
            } catch (IOException ex) {
                // @TODO: log that
                //Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                                  
        readData();        
    }
    
    /**
     * read data in json file and set property of current object
     */
    private void readData(){                        
        
        String content = null;
        try {
            content = FileUtils.readFileToString(f, "utf-8");
        } catch (IOException ex) {
            // @TODO: log that
            //Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // File content is empty
        if( content.trim().length() == 0 )
            return;
                
        // Convert JSON string to JSONObject
        JSONObject json = new JSONObject(content); 
        long timestamp = json.getLong("timestamp");
        if( timestamp < (timestamp-(60*60*24)) ){            
            return;
        }
        login = json.getString(crypt.decrypt("login"));
        login = crypt.decrypt(login);
        
        password = crypt.decrypt(json.getString("password"));
        password = crypt.decrypt(password);
    }
        
    /**
     * save json content
     * 
     * @param login
     * @param password 
     */
    public void saveData(String login, String password){
        long currentTimestamp = System.currentTimeMillis();        
        
        // For test mode
        // set current timestamp to 24h before
        if( testMode )
            currentTimestamp  = currentTimestamp - ( 60*60*24 );
        
        // Create Json Object
        JSONObject json = new JSONObject(); 
        json.put(crypt.encrypt("login"), crypt.encrypt(login));
        json.put(crypt.encrypt("password"), crypt.encrypt(password));
        json.put("timestamp", currentTimestamp);
        
        String content = json.toString();
        try {
            FileUtils.writeStringToFile(f, content, "utf-8");
        } catch (IOException ex) {
            // @TODO: log that
            // Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * clear all conf file saved
     */
    public void clearAll(){
        File[] cList = new File(dirPath).listFiles();
        for (File c : cList) {
            if( c.getName().contains(".json") ){
                c.delete();
            }
        }
    }
    
}
