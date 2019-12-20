[![License: IPL 1.0](https://img.shields.io/badge/License-IPL%201.0-blue.svg)](https://opensource.org/licenses/IPL-1.0) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
# ICompileCpp project
*Compile C++ on IBM i in remote become easy.<br />
Work in progress. So be indulgent ^^ <br />
Don't hesitate to open ticket for any issues or demand of features <br />
Feel free to contribute*

See the evolution through the [Todo's List here](#Todos)

### Requirement
* Java 8
* IBMI i V6R1 till V7R4
* port 21
* port 8476 for JT400 ( [see details here](https://javadoc.midrange.com/ports.php) )
* [Toolbox for Java and JTOpen](https://developer.ibm.com/articles/i-javatoolbox/)

### Installation
Clone this repository.<br />
Add in your path the directory of the local repository.<br />
Open cmd prompt and type `ICompileCpp.bat`.<br />
If you see the help you're the best ;)<br />

## Command arguments
| argument name           | type      | mandatory | description | possible values |
|:------------------------|:----------|:----------|:------------|:------------|
| `-ip`            | str       | yes       | local ip of your AS 400 |
| `-user` <br /> `-usr`   | str       | yes       | user profil |
| `-password` <br /> `-pwd`   | str       | yes       | password |
| `-file` <br /> `-f`   | str       | yes       | the *.cpp file path relative of current directory |
| `-rdir`   | str       | yes       | the remote directory where is uploaded your sources |
| `-l` <br /> `-library` <br /> `-lib`   | str       | no       | library where the programs or modules going to be compile ( *CURLIB by default) |
| `-dbgview`   | str       | no       | compile with DBGVIEW(*ALL) ( *NONE by default ) | *NONE / *ALL / *STMT / *SOURCE / *LIST
| `-tgtrls`   | str       | no       | target release ( *PRV by default ) |  *CURRENT / *PRV / VXRXMX ( following the release of your system ) |
| `-v`   | flag       | no       | put the program in debug mode. see command send, ect ...  |
| `-clean`   | flag       | no       | clean conf cache files  |


### Usage with Visual Studio Code
For upload your files when its saved use the excellent extension of @liximo `SFTP`.<br />
For compile current file create a `tasks.json` within your project directory.<br />
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

## File Tags
Begin your source file with the following tags for name this one, bind modules, add module or pgm options

| name                 |mandatory | description | 
|:--------------------|:----------|:------------|
| `#NAME`             |yes       | name of your module and program |
| `#OPTPGM`           |no        | Parameter for the CRTPGM command ( see CRTPGM 5250 screen ) | 
| `#OPTMOD`           |no        | Parameter for the CRTCPPMOD command ( see CRTCPPMOD 5250 screen ) | 
| `#MODULES`          |no        | List of module should be bind with PGM ( can be replaced in `#OPTPGM` tag by parameter 'MODULE(LIB/YOURMODULE LIB/ANOTHERMODULE)' ) | 

```cpp
//#NAME PGM_HELLO
//#OPTPGM OPTION(*DUPPROC *DUPVAR *NOWARN) ACTGRP(*CALLER)
//#OPTMOD PACKSTRUCT(1)
//#MODULES MOD_1 MOD_2
```
Soon an exemple of program and module

## Built With

* [NetBeans](https://netbeans.org/) - A simple IDE
* [Maven](https://maven.apache.org/) - Dependency Management

## Todo's
- [X] 10/30/2019 - *Requirements*
- [X] 10/30/2019 - *Ask password in hidden*
- [ ] *Compile current directory recursively*
- [X] 12/18/2019 - *Save password for current user during 24 hours*
- [X] 12/18/2019 - *Encrypt password saved*
- [ ] *Exemples of program*
- [ ] *Exemples of module*
- [ ] *Tests on linux*
- [X] 12/18/2019 - *Tests for Encryption password*
- [X] 12/18/2019 - *Tests for Persistance of data*
- [ ] *Compile SRVPGM*
- [X] 12/20/2019 - *Clear config cache*

