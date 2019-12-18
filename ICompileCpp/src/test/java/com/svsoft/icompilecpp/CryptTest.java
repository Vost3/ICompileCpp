package com.svsoft.icompilecpp;

import java.io.File;
import javax.crypto.SecretKey;
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
        
        SecretKey oldKey = op.getKey();        
        
        // Delete file
        File f = new File(path);
        f.delete();
        
        // Recreate
        op = new Crypt();
        f = new File(path);
        
        SecretKey newKey = op.getKey();
        Assert.assertTrue(f.exists());
        Assert.assertNotSame("Key regenerated is the same", oldKey, newKey);                
        
        // Ropen
        op = new Crypt();
        Assert.assertEquals("Key is not the same after reopen", newKey, op.getKey());        
        
        // Encrypt and decrypt
        String clearPhrase = "nianiania_truc_tatatoto";
        String encryptPhrase = op.encrypt(clearPhrase);
        String decryptPhrase = op.decrypt(encryptPhrase);        
        
        Assert.assertEquals("Error during decrypt", clearPhrase, decryptPhrase);        
        
        f.delete();
    }
        
}
