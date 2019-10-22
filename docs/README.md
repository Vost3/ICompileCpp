# ICompileCpp project
*Compile C++ on IBM i in remote become easy.
Work in progress. So be indulgent ;)
Don't hesitate to open ticket for any issues or demand of features
Feel free to contribute*

### Installation
Clone this repository.
Add in your path the directory of the local repository.
Open cmd prompt and type `ICompileCpp.bat`.
If you see the help you're the best ;)

## Parameters
| name                    | type      | mandatory | description | possible values |
|:--------------------|:----------|:----------|:------------|:------------|
| `-ip`            | str       | yes       | local ip of your AS 400 |
| `-user`  `-usr`   | str       | yes       | user profil |
| `-password` `-pwd`   | str       | yes       | password |
| `-file`  `-f`   | str       | yes       | the *.cpp file path relative of current directory |
| `-rdir`   | str       | yes       | the remote directory where is uploaded your sources |
| `-l` `-library` `-lib`   | str       | no       | library where the programs or modules going to be compile ( *CURLIB by default) |
| `-dbgview`   | flag       | no       | compile with DBGVIEW(*ALL) ( *NONE by default ) |
| `-tgtrls`   | str       | no       | target release ( *PRV by default ) |  *CURRENT / *PRV / VXRXMX ( following the release of your system ) |
| `-v`   | flag       | no       | put the program in debug mode. see command send, ect ...  |


### Usage
**Visual Studio Code**
For upload your files when its saved use the excellent extension of @liximo `SFTP` 
For compile current file create a tasks.json within your project directory.
```json
{    
    "version": "2.0.0",    
    "tasks": [
        {
            "label": "ICompile Cpp",
            "type": "shell",
            "command": "cd ${workspaceRoot}; ICompileCpp.bat -ip 192.168.1.1 -usr YOUR_LOGIN -pwd YOUR_PASSWORD -f ${relativeFile} -rdir /Directory/OtherOneForYourProject -lib YOUR_LIB",            
            "presentation": {
                "echo": false,
                "reveal": "always",
                "focus": false,
                "panel": "dedicated",
                "showReuseMessage": true,
                "clear": true
            },
            "group": {
                "kind": "build",
                "isDefault": true
            },
            "problemMatcher": []
        }
    ]
}
```

## Tags
Begin your source file with the following tags for name this one, bind modules, add module or pgm options

| name                 |mandatory | description | 
|:--------------------|:----------|:------------|
| `#NAME`             |yes       | name of your module and program |
| `#OPTPGM`           |no        | Parameter for the CRTPGM command ( see CRTPGM 5250 screen ) | 
| `#OPTMOD`           |no        | Parameter for the CRTCPPMOD command ( see CRTCPPMOD 5250 screen ) | 
| `#MODULES`          |no        | List of module should be bind with PGM ( can be replaced in `#OPTPGM` tag by parameter 'MODULE(YOURLIB/YOURMODULE ECT/ECT)' ) | 

```cpp
//#NAME PGM_HELLO
//#OPTPGM OPTION(*DUPPROC *DUPVAR *NOWARN) ACTGRP(*CALLER)
//#OPTMOD PACKSTRUCT(1)
//#MODULES MOD_1 MOD_2
```
Soon an exemple of program with module

### Compatibility 
Tested on V6R1 till V7R4

### Requirement
- Java 8

### Coming soon
- [ ] Requirements ;)
- [ ] Ask password in hidden
- [ ] Save password during 24 hours
- [ ] Encrypt password saved
- [ ] Exemples of program
- [ ] Exemples of module
- [ ] Tests on linux
- [ ] Unit tests
- [ ] Compile SRVPGM

