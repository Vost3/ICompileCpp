/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.svsoft.icompilecpp;

import java.io.File;
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
    }
    
    @Test
    public void initDbTest(){
        File f = new File(path);
        // Remove if exist
        if( f.exists() )
            f.delete();
        
        op.initDb();
        f = new File(path);
        Assert.assertTrue(f.exists());
    }
    
    @After
    public void tearDown() {
        
    }
}
