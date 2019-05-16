package com.hp.mss.hpprint.util;

import android.print.PrintJob;

/**
 * Created by zhangbin on 2017/9/11.
 */

public class PItem {
    private PrintJob printJob;
    private static PItem pItem;
    public PItem(){
    }
    public PrintJob getPrintJob() {
        return printJob;
    }

    public void setPrintJob(PrintJob printJob) {
        this.printJob = printJob;
    }
    public static PItem getOneItem(){
        if(pItem==null){
            synchronized (PItem.class){
                if(pItem==null){
                    pItem=new PItem();
                }
            }
        }
        return pItem;
    }
}
