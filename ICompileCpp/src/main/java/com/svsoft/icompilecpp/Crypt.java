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
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Steve Vogel
 */
public class Crypt {                  
    
    private String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!:;,?*ù%^$_-|@";
    private File f = null;
    
    /**
     * key string generated or read on file
     */
    private String key = null;
    
    /**
     * Key for encrypt and decrypt
     */
    private SecretKey Skey = null;
    
    /**
     * Path of file where saved key
     */
    private String path = null;        

    Crypt(){
        path = System.getProperty("user.home")+File.separator;
        path += ".svsoft";
        path += File.separator+"icmpcppk.cfg";  
        
        openFile(path);
        Skey = new SecretKeySpec(getKey().getBytes(), "AES");                        
    }
    
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }        
    
    private boolean openFile(String path){                
        // Create directory if not exist
        f = new File(path);
        if( f.exists() == false ){
            f.getParentFile().mkdirs(); 
            
            try {
                f.createNewFile();                
                key = getRandomKey(512);
                writeKey(key);
            } catch (IOException ex) {
                //@TODO : log that
                Logger.getLogger(Crypt.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }            
        return true;        
    }
    
    /**
     * Generate random key
     * @param length
     * @return 
     */
    public String getRandomKey(int length) 
    {
        Set<String> identifiers = new HashSet<String>();
        Random rand = new Random();    
        StringBuilder builder = new StringBuilder();
        while(builder.toString().length() == 0) {            
            for(int i = 0; i < length; i++) {
                builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
            }
            if(identifiers.contains(builder.toString())) {
                builder = new StringBuilder();
            }
        }
        return builder.toString();
    }
    
    /**
     * Write key in file
     * @param data 
     */
    private void writeKey(String data) {
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
     * @return key
     */
    public String getKey(){
        
        // Key already read
        if( key != null )
            return key;
        
        // Go to read from file
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
        }finally{
            try {
                br.close();
            } catch (IOException ex) {
                //Logger.getLogger(Crypt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        key = line.trim();
        return key;
    }
    
    /**
     * Encrypt string following key
     * @param str
     * @return
     * @throws Exception 
     */
    public String encrypt(String str) {
        try {
            Cipher ecipher = null;
            ecipher = Cipher.getInstance("AES");
            ecipher.init(Cipher.ENCRYPT_MODE, Skey);
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");
            
            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);
            
            // Encode bytes to base64 to get a string
            return new sun.misc.BASE64Encoder().encode(enc);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException ex) {
            //@TODO : log that
            //Logger.getLogger(Crypt.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /** 
     * Decrypt string following key
     * @param str
     * @return
     * @throws Exception 
     */
    public String decrypt(String str) {
        try {
            Cipher dcipher = null;
            dcipher = Cipher.getInstance("AES");
            dcipher.init(Cipher.DECRYPT_MODE, Skey);
            
            // Decode base64 to get bytes
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
            
            byte[] utf8 = dcipher.doFinal(dec);
            
            // Decode using utf-8
            return new String(utf8, "UTF8");
        } catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            //@TODO : log that
            //Logger.getLogger(Crypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException | BadPaddingException | IOException ex) {
            //@TODO : log that
            //Logger.getLogger(Crypt.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
