//#OPTMOD PACKSTRUCT(1)
//#NAME MOD_1

#include <stdio.h>
#include <iostream>    

#pragma pack(1)

#include "h/mod_1.h"

Module1::Module1(){
    this->fakeBool = true;
}

void Module1::doSomething1()
{
    std::cout << "Module 1 - doSomething 1" << std::endl;
}

bool Module1::getFakeBool()
{   
    std::cout << "Module 1 - getFakeBool 2" << std::endl;
    return this->fakeBool;
}











