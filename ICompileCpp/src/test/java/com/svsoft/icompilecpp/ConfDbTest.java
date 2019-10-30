/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.svsoft.icompilecpp;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Steve Vogel
 */
public class ConfDbTest {
    protected ConfDb op;
    private String path = null;
 
    @Before
    public void setUp() {
        op = new ConfDb();
        path = op.getPath();  
        
        // Delete database
        File f = new File(path);
        // Remove if exist
        if( f.exists() )
            f.delete();        
    }
    
    /**
     * Check if database file exist after init
     */
    @Test
    public void createDbFileTest(){       
        op.initDb();
        File f = new File(path);
        Assert.assertTrue(f.exists());                
    }
    
    /**
     * Check if table is created
     */
    @Test
    public void initContentDbFileTest(){        
        File f = new File(path);        
        
        ResultSet result = null;
        try             
        {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:"+path);
            Statement stmt = conn.createStatement();
            
            String sql = "SELECT count(*) FROM host";                                                            
            result = stmt.executeQuery(sql);
                        
            result.next();
            Assert.assertEquals(0, result.getInt(1));   // should return 0 row in db
            
        } catch (SQLException e) {    
            if( e.getErrorCode() == 1 ){
                Assert.fail("Table 'host' missing in database file");
                return;
            }
            Logger.getLogger(ConfDbTest.class.getName()).log(Level.SEVERE, null, e);            
        }
    }
    
    
    
    
    @After
    public void tearDown() {
        
    }
}
