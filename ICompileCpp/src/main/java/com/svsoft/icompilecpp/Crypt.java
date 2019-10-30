/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.svsoft.icompilecpp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Steve Vogel
 */
public class Crypt {           
    
    private File f = null;
    /**
     * Key for encrypt and decrypt
     */
    private SecretKey key = null;
    
    /**
     * Path of file where saved key
     */
    private String path = null;        

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    Crypt(){
        path = System.getProperty("user.home")+File.separator;
        path += ".svsoft";
        path += File.separator+"icmpcppk.cfg";  
        
        openFile(path);
        key = new SecretKeySpec("RANDOM_KEY".getBytes(), "AES");                        
    }

    
    private boolean openFile(String path){                
        // Create directory if not exist
        f = new File(path);
        if( f.exists() == false ){
            f.mkdirs();        
            try {
                f.createNewFile();
            } catch (IOException ex) {
                //@TODO : log that
                Logger.getLogger(Crypt.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
            
        return true;        
    }
    
    private void setKey(String data) {
        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(f);
            outStream.write(data.getBytes(), 0, data.length());
        } catch (IOException e) {
            //@TODO : log that
            Logger.getLogger(Crypt.class.getName()).log(Level.SEVERE, null, e);            
        }finally{
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * get key from file
     * @return 
     */
    private String getKey(){
        String line = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            line = br.readLine();
        } catch (IOException ex) {
            // @TODO : log that
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        return line;
    }
    
    public String encrypt(String str) throws Exception {
        Cipher ecipher = null;
        ecipher = Cipher.getInstance("AES");
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        // Encode the string into bytes using utf-8
        byte[] utf8 = str.getBytes("UTF8");

        // Encrypt
        byte[] enc = ecipher.doFinal(utf8);

        // Encode bytes to base64 to get a string
        return new sun.misc.BASE64Encoder().encode(enc);
    }

    public String decrypt(String str) throws Exception {
        Cipher dcipher = null;
        dcipher = Cipher.getInstance("AES");              
        dcipher.init(Cipher.DECRYPT_MODE, key);
        
        // Decode base64 to get bytes
        byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
                
        byte[] utf8 = dcipher.doFinal(dec);

        // Decode using utf-8
        return new String(utf8, "UTF8");
    }
}
