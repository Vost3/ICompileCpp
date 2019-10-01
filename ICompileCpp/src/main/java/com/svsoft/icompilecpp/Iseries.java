/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.svsoft.icompilecpp;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.QueuedMessage;
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
        try {
            sys.connectService(AS400.SIGNON);
        } catch (AS400SecurityException | IOException ex) {
            //Logger.getLogger(Iseries.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(Date.nowFormatted2() + " : INFO\t: " + ex.getMessage());
            System.exit(1);
            return false;
        }        
        
        cmd = new CommandCall(sys);   
        System.out.println(Date.nowFormatted2() + " : INFO\t: connected on " + sys.getSystemName());
        return true;
    }
    
    public boolean runCMD(String cmdS){
            return runCMD(cmdS, false);
    }
    // Send command and resolve joblog if error
    public boolean runCMD(String cmdS, boolean ignoreError)
    {
        if( cmd == null )
            getCmd();
        
        try{
            // Save current lenght of joblog
            joblogPreviousLenght = cmd.getServerJob().getJobLog().getLength();            
            // exec command
            if( cmd.run(cmdS) == false )
            {                           
                // Joblog or error not wanted
                if( ignoreError )
                    return true;
                
                System.out.println("failed. \r\n");
                
                int joblogCurrentLenght = cmd.getServerJob().getJobLog().getLength();
                QueuedMessage[] qMsg = cmd.getServerJob().getJobLog().getMessages(joblogPreviousLenght, joblogCurrentLenght);
                
                int errorNumber = 1;
                int i = 0;                
                while (i < qMsg.length) 
                {  
                    // Ignore CPF5C62=Client request - run command &2.
                    // Ingore CPF5C61=Client request - run program &2/&1.
                    if( qMsg[i].getID().equals("CPF5C62") || qMsg[i].getID().equals("CPF5C61") || qMsg[i].getID().equals("CZS0613") || qMsg[i].getID().equals("CPF3C50") ){
                        i++;
                        continue;
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
                    }
                    
                    // Help text
                    System.err.println(formatHelpText(helpText) + "\r\n");                        
                    
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
/*            if( helpTextArr[i].contains("Recovery  . . . :")){
                String fixedLine = helpTextArr[i].replace("Recovery  . . . :", "").trim();
                helpText += fixedLine + "";
                continue;
            }
*/
        }
        
        return helpText;
    }
    
    void disconnect(){
        if (sys != null) {
            sys.disconnectAllServices();
            System.out.println(Date.nowFormatted2() + " : INFO \t: close connexion with " + sys.getSystemName());
        }
    }
}
