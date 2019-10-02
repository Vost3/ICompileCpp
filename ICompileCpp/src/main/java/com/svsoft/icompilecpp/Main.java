/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.svsoft.icompilecpp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vogel Steve 
 * TODO: 
 * - compile without tag #NAME 
 * - check target release wanted can be reach on current server 
 * - verbose mode 
 * - logguer
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
        srv = new Iseries(parms);

        readFile();
        if (compileModule() && isPgm) {
            compileProgram();
        }

        srv.disconnect();

    }

    // read the file content
    // TODO : opti ( 10 lines max without //# in beginnin )
    private boolean readFile() {
        File f = new File(parms.getFilePath());
        if (f.exists() == false) {
            System.err.println(Date.nowFormatted2() + " : ERROR\t: " + parms.getFilePath() + " not found.");
            System.exit(1);
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            String line = null;
            try {
                line = br.readLine();

                while (line != null) {
                    if (line.startsWith("//#")) {
                        line = line.replace("//#", "");
                        dispatchLine(line);
                    }

                    line = br.readLine();

                }

            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
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
            name = line.replace("NAME", "").trim();
        } else if (line.startsWith("OPTMOD")) {
            modOptions = line.replace("OPTMOD", "").trim();
        } else if (line.startsWith("OPTPGM")) {
            pgmOptions = line.replace("OPTPGM", "").trim();
        } else if (line.startsWith("MOD")) {
            String modulesTmp = line.replace("MOD", "").trim();
            modules = modulesTmp.split(" ");
            isPgm = true;
        } else if (line.startsWith("PGM")) {
            name = line.replace("PGM", "").trim();
            isPgm = true;
        }

    }

    // Save parm following line reed
    boolean compileModule() {
        // Delete and ignore error
        srv.runCMD("DLTMOD " + parms.getLibrary() + "/" + name);

        // Preparing for listing    
        String localListingPath = System.getProperty("user.home")+"\\Downloads\\compile_" + name + ".txt";
        File localListing = new File(localListingPath);
        if(localListing.exists())
            localListing.delete();
        
        srv.runCMD("QSH CMD('touch -C 1252 /tmp/compile_" + name + ".txt')");
                 
        // Compile command
        String cmdS = "CRTCPPMOD MODULE(" + parms.getLibrary() + "/" + name + ") SRCSTMF('" + parms.getRemoteDirectory() + parms.getFilePath() + "') TGTRLS(" + parms.getTGTRLS() + ")";
        // Output for get
        cmdS += " OUTPUT('/tmp/compile_"+name+".txt' "+name+") ";
        
        if (modOptions != null) {
            cmdS += " " + modOptions;
        }
        
        cmdS += " DBGVIEW("+parms.getDebugView()+")";        

        System.out.print(Date.nowFormatted2() + " : INFO\t: module " + parms.getLibrary() + "/" + name + " ");
        boolean compiled = srv.runCompile(cmdS);
        if (compiled) {
            System.out.println("compiled successfully.");
        }else{
            // @TODO : test
            srv.downloadStmf("/tmp/compile_" + name + ".txt", localListingPath);                        
        }
        srv.runCMD("QSH CMD('rm /tmp/compile_" + name + ".txt')");

        return compiled;
    }

    // Compile the program
    void compileProgram() {
        // Delete and ignore error
        srv.runCMD("DLTPGM " + parms.getLibrary() + "/" + name);

        String cmdS = "CRTPGM PGM(" + parms.getLibrary() + "/" + name + ") MODULE(%s) TGTRLS(" + parms.getTGTRLS() + ")";

        // Get list of modules        
        String modulesFormated = parms.getLibrary() + "/" + name;   // hiself module
        int i = 0;
        while (i < modules.length) {
            modulesFormated += " " + parms.getLibrary() + "/" + modules[i];
            i++;
        }
        cmdS = String.format(cmdS, modulesFormated);

        System.out.print(Date.nowFormatted2() + " : INFO\t: program " + parms.getLibrary() + "/" + name + " ");
        if (pgmOptions != null) {
            cmdS += " " + pgmOptions;
        }
 
        boolean compiled = srv.runCompile(cmdS);
        if (compiled) {
            System.out.println("compiled successfully.");
        }
    }
}
