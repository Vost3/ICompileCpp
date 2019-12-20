//#NAME PGM1
//#OPTMOD PACKSTRUCT(1)
//#MODULES MOD_1 MOD_2

#include <stdio.h>

#include <errno.h>

#include <iostream>                    
#include <iomanip>                     
#include <fstream>      

#include <qlicobjd.h>
#include <mih/matsobj.h>
#include <mih/rslvsp.h>
#include <mih/setsppfp.h>
#include <mih/setsppo.h>
#include <qusmiapi.h>

#include "h/mod_1.h"
#include "h/mod_2.h"


int main(int argc , char * argv[]) 
{   
    // call module 1
    Module1 * mod1 = new Module1();
    mod1->doSomething1();

    bool fakeBool1 = mod1->getFakeBool();
     
    // call module 2
    Module2 * mod2 = new Module2();
    mod2->doSomething2();

    bool fakeBool2 = mod2->getFakeBool();
     
    return 0;
}













