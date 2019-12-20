//#OPTMOD PACKSTRUCT(1)
//#NAME MOD_2

#include <stdio.h>
#include <iostream>    

#pragma pack(1)

#include "h/mod_2.h"

Module2::Module2(){
    this->fakeBool = true;
}

void Module2::doSomething2()
{
    std::cout << "Module 2 - doSomething 2" << std::endl;
}

bool Module2::getFakeBool()
{   
    std::cout << "Module 2 - getFakeBool 2" << std::endl;
    return this->fakeBool;
}











