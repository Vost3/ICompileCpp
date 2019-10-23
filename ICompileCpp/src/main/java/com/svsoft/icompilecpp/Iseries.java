/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.svsoft.icompilecpp;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400FTP;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.QueuedMessage;
import com.svsoft.icompilecpp.exception.SeeListingError;
import java.beans.PropertyVetoException;
import java.io.IOException;

/**
 *
 * @author vogels
 */
public class Iseries {
    private AS400 sys = null;
    private CommandCall cmd = null;
    private String ip = null;
    private String user = null;
    private String password = null;
    private AS400FTP ftp = null;
    
    private int joblogPreviousLenght;
    // Initialized with Arguments Class
    Iseries(Arguments appArgs)
    {
        sys = new AS400(appArgs.getIp(), appArgs.getUser(), appArgs.getPassword());                
        try {
            sys.setGuiAvailable(false);
        } catch (PropertyVetoException ex) {
            //Logger.getLogger(Iseries.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean getCmd()
    {
        // Connexion to signon service
        // Port 8476 ( SSL = 9476 )
        System.out.print(Date.nowFormatted2() + " : INFO\t: connexion on " + sys.getSystemName());
        try {
            sys.connectService(AS400.SIGNON);
        } catch (AS400SecurityException | IOException ex) {
            //Logger.getLogger(Iseries.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(". ERROR\t: ( exception :" + ex.getMessage()+ " )");
            System.exit(1);
            return false;
        }        
        
        cmd = new CommandCall(sys);   
        System.err.println(" done.");
        return true;
    }
    
    
    public boolean runCMD(String cmdS){
        if( cmd == null )
            getCmd();
        
        try{
            // Save current lenght of joblog
            joblogPreviousLenght = cmd.getServerJob().getJobLog().getLength();            
            // exec command
            return cmd.run(cmdS);
            
        } catch (AS400SecurityException | IOException | InterruptedException | PropertyVetoException | ErrorCompletingRequestException ex) {
            //Logger.getLogger(IbmICPP.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(Date.nowFormatted2() + " : ERROR \t: "+ex.getMessage());
            return false;
        }          
    }
    // Send command and resolve joblog if error
    public boolean runCompile(String cmdS) throws SeeListingError
    {
        if( cmd == null )
            getCmd();
        
        try{
            // Save current lenght of joblog
            joblogPreviousLenght = cmd.getServerJob().getJobLog().getLength();            
            // exec command
            if( cmd.run(cmdS) == false )
            {                                                           
                System.out.println("failed. ("+cmdS+")");                
                
                // Get joblog
                int joblogCurrentLenght = cmd.getServerJob().getJobLog().getLength();
                QueuedMessage[] qMsg = cmd.getServerJob().getJobLog().getMessages(joblogPreviousLenght, joblogCurrentLenght);
                
                int errorNumber = 1;
                int i = 0;                
                while (i < qMsg.length) 
                { 
                    // ignore CPF5C62=Client request - run command &2.
                    // ignore CPF5C61=Client request - run program &2/&1.
                    // ignore CPF3C50=&1 program not created
                    if( qMsg[i].getID().equals("CPF5C62") || qMsg[i].getID().equals("CPF5C61") || qMsg[i].getID().equals("CPF3C50") ){
                        i++;
                        continue;
                    }
                    
                    // Syntax error - see listing or joblog
                    if( qMsg[i].getID().equals("CZS0613") ){
                        throw new SeeListingError();                        
                    }
                    
                    String helpText = qMsg[i].getHelp();
                    
                    // Removing first occurence of "1N"
                    if( helpText.startsWith("&N") ){
                        helpText = helpText.substring(2, helpText.length());
                    }
                    
                    // Id + text
                    if( qMsg[i].getID().trim().equals("") == false ){                        
                        System.err.println("Error n°" + errorNumber + " : " + qMsg[i].getID()+" - " +qMsg[i].getText());
                        errorNumber++;
                        
                        // Help text
                        System.err.println(formatHelpText(helpText) );                                                
                    }
                    
                    
                    
                    i++;
                }      
                
                
                
                return false;
            }            
        } catch (AS400SecurityException | IOException | InterruptedException | PropertyVetoException | ErrorCompletingRequestException | ObjectDoesNotExistException ex) {
            //Logger.getLogger(IbmICPP.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(Date.nowFormatted2() + " : ERROR \t: "+ex.getMessage());
            return false;
        }    
        
        return true;
    }
    
    /**
     * Formating help text from ibmi
     * @param helpText
     * @return 
     */
    String formatHelpText(String helpText)
    {     
        String[] helpTextArr = helpText.split("&N");
        helpText = "";
        
        for (int i = 0; i < helpTextArr.length ; i++) {
            // Remove "Cause . . . . . :"
            if( helpTextArr[i].contains("Cause . . . . . :")){
                String fixedLine = helpTextArr[i].replace("Cause . . . . . :", "").trim();
                helpText += fixedLine + "";
                continue;
            }
            
            // Remove "Recovery  . . . :"  
            if( helpTextArr[i].contains("Recovery  . . . :")){
                String fixedLine = helpTextArr[i].replace("Recovery  . . . :", "").trim();
                helpText += fixedLine + "";
                continue;
            }

        }
        
        return helpText;
    }
    
    void disconnect(){
        if (sys != null) {
            sys.disconnectAllServices();
            System.out.println(Date.nowFormatted2() + " : INFO \t: close connexion with " + sys.getSystemName());
        }
    }
    
    public boolean downloadStmf(String pathR, String pathL){
        
        System.out.print(Date.nowFormatted2() + " : INFO \t: downloading listing generated");
        
        // Download
        java.io.File f = null;
        try {
            f = new java.io.File(pathL);
            // File exists ?
            if( f.exists() ){                
                f.delete();                
            }
            // Parent directory exist ?
            if( f.getParentFile().exists() == false)
                f.getParentFile().mkdirs();
            
            // Create
            f.createNewFile();
        } catch (IOException ex) {
            System.out.println(". ERROR (during creation of local file '"+f.getAbsolutePath()+"' )");    
            return false;
        }
        
        // Ftp Connexion
        if( ftp == null ){            
            ftp = new AS400FTP(cmd.getSystem());
            try {
                ftp.setDataTransferType(AS400FTP.BINARY);                
                if( ftp.connect() == false ){
                    System.out.println(". ERROR ( during connect. "+ ftp.getLastMessage() +" )");
                    return false;
                    //utils.logger.log(utils.logger.ERROR, "during FTP connection");
                }                
            } catch (IOException ex) {            
                System.out.println(". ERROR ( exception ftp connexion )");
                return false;
                //utils.logger.log(utils.logger.ERROR, "during FTP connection");
            }
        }
        
        // Download        
        try {
            if( ftp.get(pathR, f) == false ){
                System.out.print(" failed. ");    
                System.out.println("ERROR ( "+ ftp.getLastMessage() +")");                    
                return false;                
            }else{
                System.out.println(" done.");    
            }                    
        } catch (IOException ex) {
            System.out.println(". ERROR ( exception during download)");            
            return false;
        }
                
        return true;
    }
}
