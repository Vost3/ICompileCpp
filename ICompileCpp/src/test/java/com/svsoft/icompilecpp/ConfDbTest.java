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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author Steve Vogel
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConfDbTest {
    protected ConfDb op = null;
    private String path = null;
 
    @Before
    public void setUp(){
        op = new ConfDb();
        path = op.getPath();  
    }
    
    /**
     * Check if database file exist after init
     */
    @Test
    public void createDbFileTest(){            
        // Delete database
        File f = new File(path);
        // Remove if exist
        if( f.exists() )
            f.delete();  
        
        op.initDb();
        
        File f2 = new File(path);
        Assert.assertTrue(f2.exists());                
    }
    
    /**
     * Check if table is created
     */
    @Test
    public void initContentDbFileTest(){   
        deleteDb();        
        op.initDb();
        
        ResultSet result = null;
        try             
        {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:"+path);
            Statement stmt = conn.createStatement();
            
            String sql = "SELECT count(*) FROM host";                                                            
            result = stmt.executeQuery(sql);
                        
            result.next();
            Assert.assertEquals(0, result.getInt(1));   // should return 0 row in db
            
            stmt.close();
            conn.close();            
        } catch (SQLException e) {    
            if( e.getErrorCode() == 1 ){
                Assert.fail("Table 'host' missing in database file");
                return;
            }
            Logger.getLogger(ConfDbTest.class.getName()).log(Level.SEVERE, null, e);            
        }
    }
    
    public void deleteDb()
    {
        // Delete database
        File f = new File(path);
        // Remove if exist
        if( f.exists() )
            f.delete();  
    }
    /**
     * Set an host in table
     */
    @Test
    public void setHostTest(){           
        op.setHost("ip_here", "login_here", "password_here");
        Assert.assertEquals(1, getNbRow());   // should return 0 row in 
    }
    
    
    public int getNbRow(){                   
        ResultSet result = null;
        int nb = -1;
        try             
        {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:"+path);
            Statement stmt = conn.createStatement();
            
            String sql = "SELECT count(*) FROM host";                                                            
            result = stmt.executeQuery(sql);
                        
            result.next();
            nb = result.getInt(1);            
            
            stmt.close();
            conn.close();
        } catch (SQLException e) {                
            Logger.getLogger(ConfDbTest.class.getName()).log(Level.SEVERE, null, e);   
            return -1;
        }
        return nb;
    }
    
    @Test
    public void cleanHost30MinTest(){                           
        deleteDb();
        op.initDb();
        long timestamp = (System.currentTimeMillis() - (60*31));        
        try {
            String query = "INSERT INTO host ('ip', 'login', 'pass', 'updd') VALUES (?,?,?,?);";
            Connection conn = DriverManager.getConnection("jdbc:sqlite:"+path);            
            PreparedStatement stmt = conn.prepareStatement(query);            
            stmt.setString(1, "cleanHost30MinTest");
            stmt.setString(2, "login_here");
            stmt.setString(3, "password_here");
            stmt.setLong(4, timestamp);            
            int result = stmt.executeUpdate();    
            stmt.close();
            conn.close();
        } catch (SQLException ex) {                        
            Logger.getLogger(ConfDb.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        int nbRow = getNbRow();
        
        op.clean(30);
        Assert.assertEquals(nbRow-1, getNbRow());   // should return 0 row in 
    }
    
    
    
    @After
    public void tearDown() {
        
    }
}
