package com.svsoft.icompilecpp;

import java.io.File;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Steve Vogel
 */
public class CryptTest {             
    
    @Test
    public void creationAndRegenerateTest(){
        Crypt op = new Crypt();
        String path = op.getPath();
        
        String oldKey = op.getKey();
        
        // Delete file
        File f = new File(path);
        f.delete();
        
        // Recreate
        op = new Crypt();
        f = new File(path);
        Assert.assertTrue(f.exists());
        Assert.assertNotSame(oldKey, op.getKey());
        Assert.assertEquals(512, oldKey.length());
        Assert.assertEquals(512, op.getKey().length());
    }
        
}
