//-----------------------------------------------------------------------------
// Author      : 朱红波
// Date        : 2012.01.18
// Version     : V 1.00
// Description : 
//-----------------------------------------------------------------------------
#ifndef TList_H
#define TList_H

#include "cm_types.h"

#define MaxLstSize 1000
typedef struct TList {
  void* FList[MaxLstSize];
  i32 FCount; //统计数量，不能修改
}TList;

TList* lst_Init();
void lst_Free(TList* lst);
i32 lst_Add(TList* lst, void* Item);
void lst_Clear(TList* lst);
void lst_Delete(TList* lst, i32 Index);
i32 lst_IndexOf(TList* lst, void* Item);
void lst_Insert(TList* lst, i32 Index, void* Item);    
void lst_Move(TList* lst, i32 CurIndex, i32 NewIndex);
void lst_Exchange(TList* lst, i32 CurIndex, i32 NewIndex);
i32 lst_Remove(TList* lst, void* Item);
void* lst_Items(TList* lst, i32 Index);
i32 lst_Count(TList* lst);


//***************************************************************************

#endif

