/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.svsoft.icompilecpp;

/**
 *
 * @author Steve Vogel
 */
public class Arguments {
    private String ip = null;
    private String user = null;
    private String password = null;
    private String library = "*CURLIB";       
    private String tgtrls = "*PRV";         // Previous by default  
    private String name = null;          
    private String filePath = null;         // relative local file path against workspace directory
    private String remoteDirectory = null;  // remote path    
    private boolean dbgview = false;

    // Constructor
    public Arguments(String[] args)
    {
        getAllArguments(args);   
        check();
    }
    
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }
        
    public void setName(String objName){
        this.name = objName;
    }
    
    public String getName() {
        return name;
    }  
    
    public String getTGTRLS() {
        return tgtrls;
    }  
    
    public void setFilePath(String path){
        this.filePath = path;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setRemoteDirectory(String path){
        this.remoteDirectory = path;
    }
    
    public String getRemoteDirectory() {
        return remoteDirectory;
    }   
    
    public boolean getDebugView(){
        return dbgview;
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
        
        try {                                           
            int i = 0;
            while( i < argsLen ){
                argTreated = false;
                String currentArgs = args[i];
                                
                if( currentArgs.equals("-l") || currentArgs.equals("-library") || currentArgs.equals("-lib")){
                    library = args[i+1];
                    argTreated = true;
                }else 
                    if( currentArgs.equalsIgnoreCase("-file") || currentArgs.equals("-f") ){
                    filePath = args[i+1];                    
                    argTreated = true;                    
                }else 
                    // Workspace directory on Server
                    if( currentArgs.equalsIgnoreCase("-dir") ){   
                    remoteDirectory = args[i+1];
                    
                    if( remoteDirectory.endsWith("/") == false )
                        remoteDirectory += "/";
                    
                    argTreated = true;                
                }else if( currentArgs.equals("-name") ){
                    name = args[i+1];
                    argTreated = true;  
                }else if( currentArgs.equalsIgnoreCase("-dbgview") ){
                    dbgview = true;
                    argTreated = true;  
                }else if( currentArgs.equalsIgnoreCase("-ip") ){
                    ip = args[i+1];
                    argTreated = true; 
                }else if( currentArgs.equalsIgnoreCase("-tgtrls") ){
                    tgtrls = args[i+1];
                    if( tgtrls.startsWith("V") == false ){
                        System.err.println("ERROR \t: bad format for 'tgtrls' argument");
                        System.exit(1);
                    }
                        
                    argTreated = true; 
                }else
                    // User login
                    if( currentArgs.equals("-user") || currentArgs.equals("-usr") ){
                    user = args[i+1];
                    argTreated = true;
                }else 
                    // Password for Server login
                    if( currentArgs.equals("-password") || currentArgs.equals("-pwd") ){
                    password = args[i+1];
                    argTreated = true;                     
                }else
                    // Help
                    if( currentArgs.equals("-h") || currentArgs.equals("-help") ){
                    getHelp();
                    System.exit(0);
                }
                
                if( i+1 > argsLen-1){
                    break;
                }
                
                if( args[i+1].startsWith("-") )
                    i = i+1;
                else
                    i = i+2;                                
            }
                                    
        } catch (java.lang.ArrayIndexOutOfBoundsException ex ){
            System.err.println("ERROR \t: please check the documentation");
            System.exit(1);
        } catch (NumberFormatException ex) {           
            System.err.println(ex.toString());
            System.exit(1);
        }
        
        // Replace or init values        
        //initVars();        
        
    }   
    
    private void check(){
        boolean error = false;
        if( filePath == null ){
            System.err.println("ERROR \t: -file argument is missing");
            error = true;
        }
        
        if( remoteDirectory == null ){
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
        System.out.println("-- Help ibmI CRT CPP        --");
        System.out.println("------------------------------");
        
        System.out.println("-ip                     : ip or dns         [ Required ]");
        System.out.println("-user | -usr            : user login        [ Required ]");
        System.out.println("-password | -pwd        : password          [ Required ]");        
        System.out.println("-file | -f              : input file        [ Required ]");
        System.out.println("-rmtd | remoteDir       : path              [ Required ]");        
        System.out.println("-l | -lib | -library    : library of compilation");        
        System.out.println("-dbgview                : set debug view(*all)");        
        System.out.println("-tgtrls                 : set target release");        
        
        
    }
}
