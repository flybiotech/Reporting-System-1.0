package com.shizhenbao.printer;

import android.app.Activity;
import android.net.Uri;
import android.print.PrintAttributes;
import android.widget.Toast;

import com.hp.mss.hpprint.model.PDFPrintItem;
import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJobData;
import com.hp.mss.hpprint.util.PrintMetricsCollector;
import com.hp.mss.hpprint.model.asset.PDFAsset;
import com.hp.mss.hpprint.util.PrintUtil;
import java.io.File;

/**
 * Created by dell on 2017/7/25.
 */

public class HPprinter {

    private Activity activity;

    private Uri userPickedUri = null;//这个必须要给赋值，将PDF的路径赋值给它就可以了

    PrintItem.ScaleType scaleType= PrintItem.ScaleType.CENTER_TOP;//图片摆放的位置
    PrintAttributes.Margins margins = new PrintAttributes.Margins(0, 0, 0, 0);//边距  暂时是0

    PrintJobData printJobData=null;
    PrintAttributes.MediaSize mediaSize5x7;
    PrintMetricsCollector printMetricsCollector;
    public HPprinter(Activity activity) {
        this.activity = activity;
        mediaSize5x7 = new PrintAttributes.MediaSize("na_5x7_5x7in", "5 x 7", 5000, 7000);
    }

    //点击打印按钮跳到另一个界面。这个界面显示选择的照片或者pdf  还是选择打印机的设置
    public void continueButtonPrint(String filePath) {
        if (filePath == null) {
            return;
        }
        this.userPickedUri = Uri.fromFile(new File(filePath));
        createPrintJobData();
        //设置要使用的打印作业数据对象
        PrintUtil.setPrintJobData(printJobData);
        //调用启动HP打印SDK打印流程。
        PrintUtil.print(activity);
    }

    //创建打印机的 打印数据
    private void createPrintJobData() {
        if (userPickedUri == null) {
            return;
        }
        createUserSelectedPDFJobData();
        printJobData.setJobName("PDF");
        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
                .build();
        printJobData.setPrintDialogOptions(printAttributes);

    }

    //选择PDF之后
    private void createUserSelectedPDFJobData() {

        PDFAsset pdfAsset = new PDFAsset(userPickedUri, false);

        PrintItem printItem4x6 = new PDFPrintItem(PrintAttributes.MediaSize.NA_INDEX_4X6, margins, scaleType, pdfAsset);
        PrintItem printItem5x7 = new PDFPrintItem(mediaSize5x7, margins, scaleType, pdfAsset);
        PrintItem printItemLetter = new PDFPrintItem(PrintAttributes.MediaSize.NA_LETTER, margins, scaleType, pdfAsset);

        printJobData = new PrintJobData(activity, printItem4x6);

        printJobData.addPrintItem(printItem5x7);
        printJobData.addPrintItem(printItemLetter);

    }
}
