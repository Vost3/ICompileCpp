/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.svsoft.icompilecpp;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author Steve Vogel
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HostTest {
    
    public String dirPath = System.getProperty("user.home") + File.separator + ".svsoft" + File.separator + "icompilecpp";
    private String fakeIp = "192.168.0.0";
    private String fakeLogin = "steve";
    private String fakePassword = "password";
    private Crypt crypt = new Crypt();
    
    /**
     * Test creation of json file
     */
    @Test
    public void createFile(){                    
        
        Host h = new Host(fakeIp);
        File f = new File(dirPath + File.separator + fakeIp + ".json");
        
        // is created
        Assert.assertTrue(f.exists());
        
        f.delete();
    }
    
    /**
     * create fake content encrypted
     */
    @Test
    public void createFakeContent(){            
        
        Host h = new Host(fakeIp);
        h.saveData(fakeLogin, fakePassword);
        File f = new File(dirPath + File.separator + fakeIp + ".json");                
        
        String content = null;
        try {
            content = FileUtils.readFileToString(f, "utf-8");
        } catch (IOException ex) {
            // @TODO: log that
            //Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Check login from file        
        JSONObject json = new JSONObject(content);
        String loginFromFile = json.getString("login");
        loginFromFile = crypt.decrypt(loginFromFile);
        Assert.assertEquals(fakeLogin, loginFromFile);
        
        // Check password from file
        String passwordFromFile = json.getString("password");
        passwordFromFile = crypt.decrypt(passwordFromFile);
        Assert.assertEquals(fakePassword, passwordFromFile);
        
        f.delete();
    }
    
    @Test
    public void rewriteOnIt(){     
       
        
        Host h = new Host(fakeIp);
        h.saveData(fakeLogin, fakePassword);
        File f = new File(dirPath + File.separator + fakeIp + ".json");                
        
        String content_1 = null;
        try {
            content_1 = FileUtils.readFileToString(f, "utf-8");
        } catch (IOException ex) {
            // @TODO: log that
            //Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // change content
        h.saveData("other_fake_login", "other_fake_password");
        
        String content_2 = null;
        try {
            content_2 = FileUtils.readFileToString(f, "utf-8");
        } catch (IOException ex) {
            // @TODO: log that
            //Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Assert.assertNotEquals(content_1, content_2);
        
        f.delete();
        }
    
        @Test
        public void clearAllFiles(){     
            Host h = new Host("192.168.1.1");
            h = new Host("192.168.1.2");
            h = new Host("192.168.1.3");
            h = new Host("192.168.1.4");                        
            
            File[] cList = new File(dirPath).listFiles();
            int nb = 0;
            for (File c : cList) {
                if( c.getName().contains(".json") ){
                    nb++;
                }
            }
            
            Assert.assertTrue((nb >= 4));
            h.clearAll();
            
            cList = new File(dirPath).listFiles();
            nb = 0;
            for (File c : cList) {
                if( c.getName().contains(".json") ){
                    nb++;
                }
            }
            Assert.assertEquals(0, nb);
        }       
        
        /**
         * test age of data saved.
         * cannot be older than 24h
         */
        @Test
        public void testAgeOfFile(){   
            Host h = new Host("192.168.1.1");
            h.setTestMode(true);
            h.saveData(fakeLogin, fakePassword);            
            
            Host h2 = new Host("192.168.1.1");        
            Assert.assertEquals(h2.getUser(), null);
            Assert.assertEquals(h2.getPassword(), null);
        }
}
