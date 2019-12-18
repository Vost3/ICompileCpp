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
    
    public String path = System.getProperty("user.home") + File.separator + ".svsoft" + File.separator + "icompilecpp";
    private String fakeIp = "192.168.0.0";
    private String fakeLogin = "steve";
    private String fakePassword = "password";
    
    /**
     * Test creation of json file
     */
    @Test
    public void createFile(){     
       
        Crypt cpr = new Crypt();
        
        Host h = new Host(fakeIp);
        File f = new File(path + File.separator + fakeIp + ".json");
        
        // is created
        Assert.assertTrue(f.exists());
        
        f.delete();
    }
    
    
    @Test
    public void createFakeContent(){     

        Crypt cpr = new Crypt();
        
        Host h = new Host(fakeIp);
        h.saveData(fakeLogin, fakePassword);
        File f = new File(path + File.separator + fakeIp + ".json");                
        
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
        loginFromFile = cpr.decrypt(loginFromFile);
        Assert.assertEquals(fakeLogin, loginFromFile);
        
        // Check password from file
        String passwordFromFile = json.getString("password");
        passwordFromFile = cpr.decrypt(passwordFromFile);
        Assert.assertEquals(fakePassword, passwordFromFile);
        
        //f.delete();
    }
}
