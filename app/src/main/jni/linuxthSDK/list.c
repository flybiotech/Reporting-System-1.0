//-----------------------------------------------------------------------------
// Author      : ��첨
// Date        : 2012.01.18
// Version     : V 1.00
// Description : 
//-----------------------------------------------------------------------------
#include "list.h"

//***************************************************************************
//---------------------------------------------------------------------------����
TList* lst_Init()
{
  TList* lst = (TList*)malloc(sizeof(TList));
  memset(lst, 0, sizeof(TList));
  return lst;
}
//---------------------------------------------------------------------------�ͷ�
void lst_Free(TList* lst)
{
  lst_Clear(lst);
}
//---------------------------------------------------------------------------ͳ������
i32 lst_Count(TList* lst)
{
  return lst->FCount;
}
//---------------------------------------------------------------------------����
i32 lst_Add(TList* lst, void* Item)
{
  if (lst->FCount >= MaxLstSize) return -1;

  i32 Result = lst->FCount;
  lst->FList[Result] = Item;
  lst->FCount++;
  return Result;
}
//---------------------------------------------------------------------------�������
void lst_Clear(TList* lst)
{
  memset(&lst, 0, sizeof(TList));
}
//---------------------------------------------------------------------------ɾ��
void lst_Delete(TList* lst, i32 Index)
{
  if ((Index < 0) || (Index >= lst->FCount)) return;
  lst->FCount--;
  if (Index < lst->FCount)
  {
    memcpy(&lst->FList[Index], &lst->FList[Index + 1], (lst->FCount - Index) * sizeof(void*));
  }
}
//---------------------------------------------------------------------------����
i32 lst_IndexOf(TList* lst, void* Item)
{
  i32 Result = 0;

  while ((Result < lst->FCount) && (lst->FList[Result] != Item))
  {
    Result++;
  }
  if (Result == lst->FCount)
    Result = -1;
  return Result;
}

//---------------------------------------------------------------------------����
void lst_Insert(TList* lst, i32 Index, void* Item)
{
  if ((Index < 0) || (Index > lst->FCount)) return;

  if (Index < lst->FCount)
  {
    memcpy(&lst->FList[Index + 1], &lst->FList[Index], (lst->FCount - Index) * sizeof(void*));
  }
  lst->FList[Index] = Item;
  lst->FCount++;
}
//---------------------------------------------------------------------------����
void lst_Exchange(TList* lst, i32 CurIndex, i32 NewIndex)
{
  void* tmp = lst->FList[CurIndex];
  lst->FList[CurIndex] = lst->FList[NewIndex];
  lst->FList[NewIndex] = tmp;
}
//---------------------------------------------------------------------------�ƶ�
void lst_Move(TList* lst, i32 CurIndex, i32 NewIndex)
{
  void* Item;
  if (CurIndex != NewIndex)
  {
    if ((NewIndex < 0) || (NewIndex >= lst->FCount)) return;
    Item = lst->FList[CurIndex];
    lst->FList[CurIndex] = NULL;
    lst_Delete(lst, CurIndex);
    lst_Insert(lst, NewIndex, NULL);
    lst->FList[NewIndex] = Item;
  }
}
//---------------------------------------------------------------------------ɾ��
i32 lst_Remove(TList* lst, void* Item)
{
  i32 Result = lst_IndexOf(lst, Item);
  if (Result >= 0)
    lst_Delete(lst, Result);
  return Result;
}

//---------------------------------------------------------------------------���нڵ�
void* lst_Items(TList* lst, i32 Index)
{
  if(Index<0) return NULL;
  if(Index>=lst->FCount) return NULL;
  return lst->FList[Index];
}
//-------------------------------------------------------------------------
