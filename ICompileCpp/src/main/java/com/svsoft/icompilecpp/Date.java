/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.svsoft.icompilecpp;

import java.text.SimpleDateFormat;

/**
 *
 * @author svogel
 */
public class Date {        
        
    public static String nowFormatted2(){                
        SimpleDateFormat sdfCell = new SimpleDateFormat("yyyyMMdd-HHmmss");        
        return sdfCell.format(System.currentTimeMillis());        
    }
    public static String nowFormatted1(){                
        SimpleDateFormat sdfCell = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");        
        return sdfCell.format(System.currentTimeMillis());        
    }
    
}
