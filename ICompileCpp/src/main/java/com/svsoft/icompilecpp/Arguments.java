package com.svsoft.icompilecpp;

import com.svsoft.icompilecpp.exception.ArgumentNotValid;

/**
 * Class of argument for manage argument send like bash
 * ex : 
 * @author Steve Vogel
 * @version 1.0
 */
public class Arguments {
    
    /**
     * ip of power system
     */
    private String ip = null;
    /**
     * login in 10 characters ( see ibmi limit )
     */
    private String user = null;
    /**
     * password 
     */
    private String password = null;
    /**
     * library of compile
     */
    private String library = "*CURLIB";       
    /**
     * target release ( see command CRTCPPMOD ) 
     */
    private String tgtrls = "*PRV"; 
    /**
     * name of module or program ( see command CRTCPPMOD )
     * previous by default
     */
    private String name = null; 
    /**
     * path of cpp in relative of workspace
     */
    private String filePath = null; 
    /**
     * path or remote directory workspace
     */
    private String remoteWorkspace = null;
    /**
     * debug view ( see command CRTCPPMOD DBGVIEW )
     */
    private String dbgview = "*NONE";
    /**
     * verbose mode ( show debug during compile )
     */
    private boolean verbose = false;


    /**
     *
     * @param args
     */
    public Arguments(String[] args)
    {
        getAllArguments(args);   
        check();
    }
    
    /**
     *
     * @return 
     */
    public String getIp() {
        return ip;
    }
    
    /**
     *
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     *
     * @return
     */
    public String getUser() {
        return user;
    }

    /**
     *
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return
     */
    public String getLibrary() {
        return library;
    }

    /**
     *
     * @param library
     */
    public void setLibrary(String library) {
        this.library = library;
    }
        
    /**
     *
     * @param objName
     */
    public void setName(String objName){
        this.name = objName;
    }
    
    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }  
    
    /**
     *
     * @return
     */
    public String getTGTRLS() {
        return tgtrls;
    }  
    
    /**
     *
     * @param path
     */
    public void setFilePath(String path){
        this.filePath = path;
    }
    
    /**
     *
     * @return
     */
    public String getFilePath() {
        return filePath;
    }
    
    /**
     *
     * @param path
     */
    public void setRemoteDirectory(String path){
        this.remoteWorkspace = path;
    }
    
    /**
     *
     * @return
     */
    public String getRemoteDirectory() {
        return remoteWorkspace;
    }   
    
    /**
     * get debug view level wanted
     * @return debugview level
     */
    public String getDebugView(){
        return dbgview;
    }
    /**
     * get if verbose mode is enabled
     * @return verbose
     */
    public boolean verboseMode() {
        return verbose;
    }
    /*
    * @params : args send to te program
    * result : boolean if all parms mandatory recevied
    */   
    private void getAllArguments(String[] args){                
        boolean argTreated = false;        
        int argsLen = args.length;               
        
        if( argsLen == 0 ){
            getHelp();
            System.exit(0);
        }
        
        int i = 0;
        try {                                                       
            while( i < argsLen ){
                argTreated = false;
                String currentArgs = args[i];
                                
                // Library of compile
                if( currentArgs.equals("-l") || currentArgs.equals("-library") || currentArgs.equals("-lib")){
                    library = getNextArg(args, i);
                    argTreated = true;
                }else 
                    // File paht relative of workspace
                    if( currentArgs.equalsIgnoreCase("-file") || currentArgs.equals("-f") ){
                    filePath = getNextArg(args, i);                                        
                    argTreated = true;                    
                }else 
                    // Workspace directory on Server
                    if( currentArgs.equalsIgnoreCase("-rdir") ){   
                    remoteWorkspace = getNextArg(args, i);
                    
                    if( remoteWorkspace.endsWith("/") == false )
                        remoteWorkspace += "/";
                    
                    argTreated = true;                
                }else
                    // Name of program of module
                    if( currentArgs.equals("-name") ){
                    name = getNextArg(args, i);
                    argTreated = true;  
                }else
                    // Debug view 
                    if( currentArgs.equalsIgnoreCase("-dbgview") ){
                    dbgview = getNextArg(args, i);
                    argTreated = true;  
                }else
                    // Ip of power system
                    if( currentArgs.equalsIgnoreCase("-ip") ){
                    ip = getNextArg(args, i);
                    argTreated = true; 
                }else 
                    // Target release
                    if( currentArgs.equalsIgnoreCase("-tgtrls") ){
                    tgtrls = getNextArg(args, i);
                    if( tgtrls.startsWith("V") == false ){
                        System.err.println("ERROR \t: bad format for 'tgtrls' argument");
                        System.exit(1);
                    }
                        
                    argTreated = true; 
                }else
                    // User login
                    if( currentArgs.equals("-user") || currentArgs.equals("-usr") ){
                    user = getNextArg(args, i);
                    argTreated = true;
                }else 
                    // Password for Server login
                    if( currentArgs.equals("-password") || currentArgs.equals("-pwd") ){
                    password = getNextArg(args, i);
                    argTreated = true;                     
                }else
                    // Verbose mode
                    if( currentArgs.equals("-v") ){
                    verbose = true;
                    argTreated = true;                     
                }else
                    // Help
                    if( currentArgs.equals("-h") || currentArgs.equals("-help") ){
                    getHelp();
                    System.exit(0);
                }
                
                // Bound security
                if( i+1 > argsLen-1){
                    break;
                }
                
                // If current argument is just a flag
                if( args[i+1].startsWith("-") )
                    i = i+1;
                else
                    i = i+2;                                
            }
                                    
        } catch (java.lang.ArrayIndexOutOfBoundsException ex ){
            System.err.println("ERROR \t: " + ex.getMessage() + " ( please see documentation '-help')");            
            System.exit(1);
        } catch (NumberFormatException ex) {           
            System.err.println(ex.toString());
            System.exit(1);
        } catch (ArgumentNotValid ex) {
            System.err.println(ex.toString());
            System.exit(1);            
        }
        
        // Replace or init values        
        //initVars();        
        
    }   
    
    /**
     * get the new arguments and check validity
     * @param args
     * @param index
     * @return next argument if not start by "-"
     * @throws ArgumentNotValid 
     */
    private String getNextArg(String[] args, int index) throws ArgumentNotValid
    {
        if( (index+1) >= args.length ) 
            throw new ArrayIndexOutOfBoundsException(args[index]+" parameter badly defined");
        
        String currentArg = args[index+1];
        if( currentArg.startsWith("-") )
            throw new ArgumentNotValid("ERROR \t: "+args[index] + " not valid");
        
        return currentArg;
    }
    
    
    private void check(){
        boolean error = false;
        if( filePath == null ){
            System.err.println("ERROR \t: -file argument is missing");
            error = true;
        }
        
        if( remoteWorkspace == null ){
            System.err.println("ERROR \t: -rmtd argument is missing");
            error = true;
        }
        
        if( error ){
            System.out.println("\t: use flags '-help' for check parms list");
            System.exit(1);
        }
        
    }
    
    private void getHelp(){
        System.out.println("------------------------------");
        System.out.println("-- Help ibmI compile CPP    --");
        System.out.println("------------------------------");
        
        System.out.println("-ip                     : ip or dns         [ Required ]");
        System.out.println("-user | -usr            : user login        [ Required ]");
        System.out.println("-password | -pwd        : password                      ");        
        System.out.println("-file | -f              : input file        [ Required ]");
        System.out.println("-rdir                   : path              [ Required ]");        
        System.out.println("-l | -lib | -library    : library of compilation");        
        System.out.println("-dbgview                : set debug view(*all)");        
        System.out.println("-tgtrls                 : set target release");        
        System.out.println("-v                      : see all commande send on iseries");        
        
        
    }
}
