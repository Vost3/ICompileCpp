package com.svsoft.icompilecpp;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Steve Vogel  
 */
public class ConfDb {
    
    private Connection conn = null;
    /**
     * path of confDb.db file
     */
    private String path = null;    

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
        
    ConfDb()
    {
        path = System.getProperty("user.home")+File.separator;
        path += ".svsoft";
        
        // Create directory if not exist
        File dir = new File(path);
        if( dir.exists() == false )
            dir.mkdir();
        
        path += File.separator+"confdb.db";        
    }
    
    void connexion(){
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:"+path);
        } catch (SQLException e) {
            System.out.println(e.getMessage());            
        }
    }
    
    boolean initDb()
    {                 
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (IOException ex) {
            // @TODO logger
            //Logger.getLogger(ConfDb.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        // SQL statement for creating a new table
        String sql =    "CREATE TABLE `host` (" +
                        "`id`	INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "`ip`	TEXT NOT NULL UNIQUE," +
                        "`pass`	TEXT," +
                        "`login` TEXT,"+
                        "`updd` INTEGER DEFAULT 0);";
        
        try{
            connexion();
            Statement stmt = conn.createStatement();            
            // create a new table
            stmt.execute(sql);
            
            stmt.close();
            close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        
        return true;
    }
    
    private void close(){        
        try {
            conn.close();
        } catch (SQLException ex) {
            //Logger.getLogger(ConfDb.class.getName()).log(Level.SEVERE, null, ex);
        }
        conn = null;
    }
    
    /**
     * Set host in db
     * @param ip
     * @param login
     * @param password
     * @return 
     */
    public int setHost(String ip, String login, String password){                
                
        int result = -1;
        try {
            String query = "INSERT INTO host ('ip', 'login', 'pass', 'updd') VALUES (?,?,?,?);";
            connexion();
            PreparedStatement stmt = conn.prepareStatement(query);            
            stmt.setString(1, ip);
            stmt.setString(2, login);
            stmt.setString(3, password);
            stmt.setLong(4, System.currentTimeMillis());            
            result = stmt.executeUpdate();
            stmt.close();
            close();
        } catch (SQLException ex) {                        
            Logger.getLogger(ConfDb.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return result;
    }
    
    /**
     * clean host saved since more than x minutes
     * @return 
     */
    public boolean clean(int minutes){                
                
        int nbSeconds = (minutes * 60);
        long timestamp = System.currentTimeMillis() - nbSeconds;        
        
        boolean result = false;
        try {
            String sql = "DELETE FROM host WHERE updd < ? ;";     
            connexion();
            PreparedStatement pstmt = conn.prepareStatement(sql);            
            pstmt.setLong(1, timestamp);
            result = pstmt.execute();
            pstmt.close();
            close();
        } catch (SQLException ex) {                        
            Logger.getLogger(ConfDb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
