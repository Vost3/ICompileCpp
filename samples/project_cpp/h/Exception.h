#ifndef DEF_EXCEPTION_HANDLER
#define DEF_EXCEPTION_HANDLER

void exceptionHandler(_INTRPT_Hndlr_Parms_T * __ptr128 parms)
{   
   // get datas about excpetion
   _GetExcData(parms);

   // copy in return area send at second argument of "#pragma exception_handler"
   memcpy((char *)parms->Com_Area,parms->Msg_Id, 7);
}

#endif