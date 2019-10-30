package com.svsoft.icompilecpp;

import com.svsoft.icompilecpp.exception.SeeListingError;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Vogel Steve 
 * TODO: 
 * - compile without tag #NAME 
 * - check target release wanted can be reach on current server  
 * - logger
 */
public class Main {

    private Arguments parms;
    private Iseries srv = null;

    private String name = null;
    private String modOptions = null;
    private String pgmOptions = null;
    private String[] modules = null;
    private boolean isPgm = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main app = new Main(args);
    }

    Main(String[] args) {
        // Get all argument
        parms = new Arguments(args);
        
        boolean err = !readFile();
        if( err )
            System.exit(1);
                
        if (compileModule() && isPgm) {
            compileProgram();
        }
        
        if( srv != null )
            srv.disconnect();

    }

    /**
     * read the current file content
     * @return 
     */
    private boolean readFile() {
        File f = new File(parms.getFilePath());
        if (f.exists() == false) {
            System.err.println(Date.nowFormatted2() + " : ERROR\t: " + f.getAbsolutePath()+ " not found.");
            System.exit(1);
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        boolean inComment = false;
        try {
            String line = null;
            try {
                line = br.readLine();

                while (line != null) {
                    // Tag found
                    if (line.startsWith("//#")) {
                        line = line.replace("//#", "");
                        dispatchLine(line);
                    }
                    
                    // Comment multi-line
                    if( line.trim().startsWith("/*") )
                        inComment = true;
                    
                    if( line.trim().endsWith("*/") || line.contains("*/") )
                        inComment = false;
                    
                    // Main prototype
                    if ( !inComment && line.contains("main")) {
                        isPgm = checkIfMainValid(line);
                    }

                    line = br.readLine();

                }

            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } finally {
            // close
            try {
                br.close();
            } catch (IOException ex) {
                //Logger.getLogger(IbmICPP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return true;
    }

    // Save parm following line reed
    void dispatchLine(String line) {
        if (line.startsWith("NAME")) {
            name = line.replaceFirst("NAME", "").trim();
        } else if (line.startsWith("OPTMOD")) {
            modOptions = line.replaceFirst("OPTMOD", "").trim();
        } else if (line.startsWith("OPTPGM")) {
            pgmOptions = line.replaceFirst("OPTPGM", "").trim();
        } else if (line.startsWith("MODULES") ) {
            String modulesTmp = line.replaceFirst("MODULES", "").trim();
            modules = modulesTmp.split(" ");
            isPgm = true;
        }

    }
    
    /**
     * check if line contain valid main protoype and not commented
     * @param line
     * @return 
     */
    boolean checkIfMainValid(String line)
    {
        line = line.trim();
        
        // Main commented
        if( line.startsWith("//") || line.startsWith("/*") )
            return false;
        
        if( line.contains("argc") && line.contains("argv"))
            return true;
        
        return false;
    }
    
    // Save parm following line reed
    boolean compileModule() 
    {        
        if( name == null ){
            System.out.print(Date.nowFormatted2() + " : ERROR\t: name of module not defined ( see documentation by '-help' )");
            return false;
        }            
        
        // Connect to iseries
        srv = new Iseries(parms);
        
        boolean compiled = false;
        // TODO: manage path for linux
        String fileName = "compile_"+parms.getUser()+"_" + name + ".txt";
        String localListingPath = System.getProperty("java.io.tmpdir")+"\\"+fileName;        
        String remoteListingPath = parms.getRemoteDirectory() + fileName;
        
        String srcstmf = parms.getRemoteDirectory() + parms.getFilePath();
        if( srcstmf.contains("\\") )
            srcstmf = srcstmf.replaceAll(Pattern.quote("\\"), "/");
            
        try {
            // Delete and ignore error
            srv.runCMD("DLTMOD " + parms.getLibrary() + "/" + name);
            
            // Preparing for listing            
            File localListing = new File(localListingPath);
            if(localListing.exists())
                localListing.delete();
            
            srv.runCMD("QSH CMD('rm "+remoteListingPath+"')");  
            srv.runCMD("QSH CMD('touch -C 1252 "+remoteListingPath+"')");           
            
            // Compile command
            String cmdS = "CRTCPPMOD MODULE(" + parms.getLibrary() + "/" + name + ") SRCSTMF('" + srcstmf + "') TGTRLS(" + parms.getTGTRLS() + ")";
            // Output for get
            cmdS += " OUTPUT('"+remoteListingPath+"' "+name+") ";
            cmdS += " OPTION(*SHOWINC *NOSHOWSYS)";
            
            if (modOptions != null) {
                cmdS += " " + modOptions;
            }
            
            cmdS += " DBGVIEW("+parms.getDebugView()+")";
            
            if( parms.verboseMode() )
                System.out.println(Date.nowFormatted2() + " : DEBUG\t: program " + cmdS);
            
            System.out.print(Date.nowFormatted2() + " : INFO\t: module " + parms.getLibrary() + "/" + name + " ");
            compiled = srv.runCompile(cmdS);
            if (compiled) {
                System.out.println("compiled successfully.");
            }else{
                return false;
            }                
            
        } catch (SeeListingError ex) {            
            boolean downloaded = srv.downloadStmf(remoteListingPath, localListingPath);     
            // error during download
            if( !downloaded )
                return false;
            
            readListing(localListingPath);
        }finally {            
            srv.runCMD("QSH CMD('rm "+remoteListingPath+"')");            
        }                        
        
        return compiled;
    }

    /**
     * Compile the program
     */
    void compileProgram() {
        // Delete and ignore error
        srv.runCMD("DLTPGM " + parms.getLibrary() + "/" + name);

        String cmdS = "CRTPGM PGM(" + parms.getLibrary() + "/" + name + ") MODULE(%s) TGTRLS(" + parms.getTGTRLS() + ")";

        // Get list of modules        
        String modulesFormated = parms.getLibrary() + "/" + name;   // hiself module
        // Only if other modules found.
        if( modules != null ){
            int i = 0;
            while (i < modules.length) {
                modulesFormated += " " + parms.getLibrary() + "/" + modules[i];
                i++;
            }            
        }
        
        cmdS = String.format(cmdS, modulesFormated);                    
                
        if (pgmOptions != null) {
            cmdS += " " + pgmOptions;
        }
        if( parms.verboseMode() )
            System.out.println(Date.nowFormatted2() + " : DEBUG\t: program " + cmdS);
               
        System.out.print(Date.nowFormatted2() + " : INFO\t: program " + parms.getLibrary() + "/" + name + " ");
        boolean compiled = false;
        try {
            compiled = srv.runCompile(cmdS);
        } catch (SeeListingError ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (compiled) {
            System.out.println("compiled successfully.");
        }                           
    }
    
    /**
     * read listing generated by compile and parse error
     * @param path
     * @return 
     */
    private boolean readListing(String path) {
        File f = new File(path);
        if (f.exists() == false) {
            System.err.println(Date.nowFormatted2() + " : ERROR\t: " + f.getAbsolutePath() + " not found.");
            return false;
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        int lineShowed = 0;
        try {
            boolean showNextLine = false;
            String line = null;
            try {
                line = br.readLine();

                while (line != null) {
                    // Show this line
                    if( showNextLine ){
                        System.err.println( line );
                        // reset
                        showNextLine = false;
                        lineShowed++;
                    }
                    
                    // error described in listing generated
                    if (line.startsWith("===") && line.endsWith("==^")) {
                        showNextLine = true;
                    }

                    line = br.readLine();
                }                                

            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } finally {
            // close
            try {
                br.close();
            } catch (IOException ex) {
                return false;
            }
        }               
        
        if( lineShowed == 0 ){
            System.err.println(Date.nowFormatted2() + " : ERROR\t: during read of listing.");
            return false;
        }            
            
        return true;
    }
}
