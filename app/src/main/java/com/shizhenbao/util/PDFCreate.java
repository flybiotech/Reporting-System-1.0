package com.shizhenbao.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.activity.R;
import com.application.MyApplication;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Header;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.User;
import com.shizhenbao.updataapk.SpUtils;

import org.litepal.LitePal;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by 用户 on 2017/4/1.
 */

public class PDFCreate {
    private List<String> list = new ArrayList<>();
    List<Doctor> list1;
    private Doctor doctor;
    final List<Doctor>doctorList= LitePal.where("dName=?","Admin").find(Doctor.class);//查迅数据库所有的数据
    private Context context;
//    private boolean isChain = true;
    /**
     * 根据返回的数据生成pdf文件
     *
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    public PDFCreate (Context context){
        this.context=context;
    }
    public void initView(User user,int index) throws FileNotFoundException, DocumentException {

        list1 = LitePal.findAll(Doctor.class);
        for (int i = 0; i < list1.size(); i++) {
            doctor = list1.get(list1.size() - 1);
        }
        Document doc = new Document(PageSize.A4, 20, 20, 0, 20);//页面布局
        File foder = new File(user.getGatherPath());//声明存储位置
        if (!foder.exists()) {//判断文件夹是否存在，如不存在就重新创建
            foder.mkdirs();
        }
        File myCaptureFile = new File(foder,((user.getpId() +"_"+ user.getpName()) + ".pdf"));//声明文件名称
        OneItem.getOneItem().setFile(myCaptureFile);
        try {
            BaseFont baseFont = BaseFont.createFont("assets/fonts/STSONG.TTF",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);//设置中文显示问题
            //医院字体大小
            Font font = new Font(baseFont, 24);
            //具体内容大小
            Font font1 = new Font(baseFont, 12);
            //小标题字体大小
            Font font2 = new Font(baseFont, 15);
            font2.setColor(BaseColor.BLUE);
            //科室的字体大小
            Font font3=new Font(baseFont,15);
            FileOutputStream fOut = new FileOutputStream(myCaptureFile);//输出流
            PdfWriter writer = PdfWriter.getInstance(doc, fOut);
            doc.open();
            if (!myCaptureFile.exists()) {//判断文件是否存在，如不存在重新创建
                myCaptureFile.createNewFile();
            }
            PdfPTable pTable=new PdfPTable(1);
            pTable.setWidthPercentage(100);
            pTable.getDefaultCell().setBorder(0);
            for(int i=0;i<doctorList.size();i++){
                Paragraph p = new Paragraph(doctorList.get(0).getEdit_hos_name(), font);//医院
                p.setAlignment(Element.ALIGN_CENTER);
//                doc.add(p);

                Paragraph p2 = new Paragraph(doctorList.get(0).getEdit_hos_keshi(), font3);//科室
                p2.setAlignment(Element.ALIGN_CENTER);
//                doc.add(p2);
                PdfPCell celltitle = new PdfPCell();
                celltitle.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                celltitle.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                celltitle.addElement(p);
                celltitle.addElement(p2);
                celltitle.setBorder(0);//设置边框的宽度
                pTable.addCell(celltitle);
                doc.add(pTable);
            }
            //添加子标题
            Paragraph p58=new Paragraph(context.getString(R.string.print_title),font3);
            p58.setAlignment(Element.ALIGN_CENTER);
            doc.add(p58);

//            //添加空内容拉开间距
//            Paragraph p42=new Paragraph("                   ",font1);
//            doc.add(p42);

            PdfPTable table4=new PdfPTable(2);
            table4.setWidthPercentage(100);
            table4.getDefaultCell().setBorder(0);
            Paragraph p40 = new Paragraph(MyApplication.getContext().getString(R.string.print_patient_id)+": "+user.getpId(), font1);
            Paragraph p41=new Paragraph("                       "+MyApplication.getContext().getString(R.string.pdfcreate_check_data)+"：  " + user.getCheckDate(), font1);
            table4.addCell(p40);
            table4.addCell(p41);
            doc.add(table4);

            //创建表格添加第一行数据
            Paragraph p4 = new Paragraph(context.getString(R.string.patient_name)+"：" + user.getpName(), font1);
            Paragraph p6 = new Paragraph(context.getString(R.string.patient_age)+"：" + user.getAge(), font1);
            Paragraph p7 = new Paragraph(context.getString(R.string.patient_medical_record_number)+"：" + user.getCaseNumbe(), font1);
            PdfPTable table1 = new PdfPTable(3); // 4 columns.创建表格，设置列数
            table1.setWidthPercentage(100); // Width 100%
//            table1.setSpacingBefore(5f); // Space before table
//            table1.setSpacingAfter(10f); // Space after table
            table1.getDefaultCell().setBorderWidthBottom(0);
            table1.getDefaultCell().setBorderWidthLeft(0);
            table1.getDefaultCell().setBorderWidthRight(0);
            table1.addCell(p4);
            table1.addCell(p6);
            table1.addCell(p7);
            doc.add(table1);
            //创建表格添加第二行数据
            Paragraph p8 = new Paragraph(context.getString(R.string.patient_source)+"：" + user.getpSource(), font1);
            Paragraph p10 = new Paragraph(context.getString(R.string.patient_marriage)+"：" + user.getMarry(), font1);
            Paragraph p11 = new Paragraph(context.getString(R.string.patient_link_telephoen)+"：" + user.getTel(), font1);
            PdfPTable table2 = new PdfPTable(3); // 4 columns.创建表格，设置列数
            table2.setWidthPercentage(100); // Width 100%
            table2.setSpacingBefore(5f); // Space before table
            table2.getDefaultCell().setBorder(0);
            table2.addCell(p8);
            table2.addCell(p10);
            table2.addCell(p11);
            doc.add(table2);

/**
 * 创建表格添加图片-------------------------------------------------------------------------------------------------------
 */
            list = user.getImgPath();
//            if(list.size()>0){
            PdfPTable table = new PdfPTable(4); // 3 columns.创建表格，设置列数
            table.setWidthPercentage(100); // Width 100%
            table.getDefaultCell().setFixedHeight(105);
            table.setSpacingBefore(10f); // Space before table
//            table.setSpacingAfter(10f); // Space after table
            if (list.size() % 4 != 0) {
                if (list.size() % 4 == 1) {
                    for (int i = 0; i < 3; i++) {
                        list.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFLY/blank.png");
                    }
                    for (int i = 0; i < list.size(); i++) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();//添加图片
                        Bitmap bitmap = BitmapFactory.decodeFile(list.get(i));
                        String newPicPath=getNewPic(bitmap);
                        Bitmap newBitmap=BitmapFactory.decodeFile(newPicPath);
                        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        Image myImg2 = Image.getInstance(stream.toByteArray());
//                            myImg2.setAlignment(1);
                        PdfPCell pdfPCell=new PdfPCell();
                        pdfPCell.setImage(myImg2);
                        pdfPCell.setFixedHeight(150);
                        pdfPCell.setHorizontalAlignment(1);
                        pdfPCell.disableBorderSide(12);
//                            pdfPCell.setBorder(0);
                        table.addCell(pdfPCell);
                    }
                } else if(list.size()%4==2){
                    for(int i=0;i<2;i++){
                        list.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFLY/blank.png");
                    }
                    for (int i = 0; i < list.size(); i++) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();//添加图片
                        Bitmap bitmap = BitmapFactory.decodeFile(list.get(i));
                        String newPicPath=getNewPic(bitmap);
                        Bitmap newBitmap=BitmapFactory.decodeFile(newPicPath);
                        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        Image myImg2 = Image.getInstance(stream.toByteArray());
//                            myImg2.setAlignment(1);
                        PdfPCell pdfPCell=new PdfPCell();
                        pdfPCell.setFixedHeight(150);
                        pdfPCell.setHorizontalAlignment(1);
                        pdfPCell.setImage(myImg2);
                        pdfPCell.disableBorderSide(12);
//                            pdfPCell.setBorder(0);
                        table.addCell(pdfPCell);
                    }
                }else {
                    list.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFLY/blank.png");
                    for (int i = 0; i < list.size(); i++) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();//添加图片
                        Bitmap bitmap = BitmapFactory.decodeFile(list.get(i));
                        String newPicPath=getNewPic(bitmap);
                        Bitmap newBitmap=BitmapFactory.decodeFile(newPicPath);
                        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        Image myImg2 = Image.getInstance(stream.toByteArray());
//                            myImg2.setAlignment(1);
                        PdfPCell pdfPCell=new PdfPCell();
                        pdfPCell.setFixedHeight(150);
                        pdfPCell.setHorizontalAlignment(1);
                        pdfPCell.setImage(myImg2);
                        pdfPCell.disableBorderSide(12);
//                            pdfPCell.setBorder(0);
                        table.addCell(pdfPCell);
                    }
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();//添加图片
                    Bitmap bitmap = BitmapFactory.decodeFile(list.get(i));
                    String newPicPath=getNewPic(bitmap);
                    Bitmap newBitmap=BitmapFactory.decodeFile(newPicPath);
                    newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    Image myImg2 = Image.getInstance(stream.toByteArray());
//                        myImg2.setAlignment(1);
                    PdfPCell pdfPCell=new PdfPCell();
                    pdfPCell.setFixedHeight(150);
                    pdfPCell.setImage(myImg2);
                    pdfPCell.setHorizontalAlignment(1);
//                        pdfPCell.setBorder(0);
                    pdfPCell.disableBorderSide(12);
                    table.addCell(pdfPCell);
                }
            }
//            table.

            doc.add(table);
//            }

            //--------------------------------------------------------------------------------------------------------------
            //填充活检位置
            Image myImg2=null;
            Bitmap bitmap=null;
            File file=new File(user.getGatherPath()+"/方位.png");
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            if(!file.exists()){
                bitmap=BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFLY/position.png");
            }else {
                bitmap=BitmapFactory.decodeFile(user.getGatherPath()+"/方位.png");
            }
//            Bitmap newBitmap=Bitmap.createBitmap(bitmap,0,0,50,50);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            myImg2 = Image.getInstance(stream.toByteArray());


            Image myImg3=null;
            ByteArrayOutputStream stream3=new ByteArrayOutputStream();
            if (MyApplication.getInstance().getCountry().equals("CN")) {
                Bitmap bitmap3=BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFLY/refrence.png");
                bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, stream3);
                myImg3 = Image.getInstance(stream3.toByteArray());
            } else {
                Bitmap bitmap3 = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.gjpe);
                bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, stream3);
                myImg3 = Image.getInstance(stream3.toByteArray());
            }


            Paragraph p22=new Paragraph(context.getString(R.string.patient_Biopsy_location)+":",font2);
            doc.add(p22);
            Paragraph p23=new Paragraph("Po  =  Polyp"+"\n"+"M  =  Mosaic"+"\n"+"C  =  Condyfoma"+"\n"+"E  =  Erosion"+"\n"+"L  =  Leukoplakia"+"\n"+"I  =  I.S.C",font1);
            Paragraph p24=new Paragraph("W  =  A.W.E"+"\n"+"AT  =  A.T.Z"+"\n"+"P  =  Puncation"+"\n"+"V  =  A.V"+"\n"+"Xn  =  Biopsyplace"+"\n"+"(n  =  Container No.)",font1);
            PdfPTable table3 = new PdfPTable(4); // 3 columns.创建表格，设置列数
            table3.setWidthPercentage(100); // Width 100%
            table3.getDefaultCell().setFixedHeight(105);
            table3.setSpacingBefore(10f); // Space before table
//            table3.setSpacingAfter(10f); // Space after table
            table3.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            table3.addCell(p23);
            table3.addCell(p24);
            table3.addCell(myImg2);
            table3.addCell(myImg3);
//            table3.addCell("");
//            table3.addCell("");
            doc.add(table3);

            //填充检查记录

            Paragraph p1 = new Paragraph(context.getString(R.string.pdfcreate_Inspection_record)+"：  ", font2);//检查记录
            Paragraph p15 = new Paragraph(context.getString(R.string.pdfcreate_cervical_cytology)+"：  ", font1);//细胞学
            Paragraph p15_1=new Paragraph(user.getAdvice(),font1);
            Paragraph p16 = new Paragraph(context.getString(R.string.pdfcreate_HPV)+"：  ", font1);//hpv
            Paragraph p16_1 = new Paragraph(user.getHpv_dna(), font1);//hpv
            Paragraph p17 = new Paragraph(context.getString(R.string.print_symptom)+"：  ", font1);//症状
            Paragraph p17_1 = new Paragraph(user.getSymptom(), font1);//症状
            Paragraph p18 = new Paragraph(context.getString(R.string.print_Overall_assessment)+"：  " , font1);//总体评估
            Paragraph p18_1 = new Paragraph( user.getSummary(), font1);//总体评估
            Paragraph p19 = new Paragraph(context.getString(R.string.print_Lesion_area)+"：  ", font1);//病例结果
            Paragraph p19_1 = new Paragraph(user.getBingbian(), font1);//病例结果
            Paragraph p20 = new Paragraph(context.getString(R.string.print_colposcopic)+"：  " , font1);//阴道镜所见
            Paragraph p20_1 = new Paragraph(user.getYindaojin(), font1);//阴道镜所见
            doc.add(p1);
            doc.add(initAlignment(p17,p17_1));
            doc.add(initAlignment(p15,p15_1));
            doc.add(initAlignment(p16,p16_1));
            doc.add(initAlignment(p19,p19_1));
            doc.add(initAlignment(p18,p18_1));
            doc.add(initAlignment(p20,p20_1));


            //填充空的内容，目的拉开间距
            Paragraph paragraph=new Paragraph("     ",font1);
            Paragraph p12 = new Paragraph(context.getString(R.string.print_Suspected)+"：  ", font2);//拟诊
            Paragraph p12_1 = new Paragraph(user.getNizhen(), font1);//拟诊
            Paragraph p14 = new Paragraph(context.getString(R.string.print_attention)+"：  ", font2);//注意事项
            Paragraph p14_1 = new Paragraph(user.getZhuyishixiang(), font1);//注意事项
            Paragraph p50 = new Paragraph(context.getString(R.string.print_opinion)+"：  ", font2);//处理意见
            Paragraph p50_1 = new Paragraph(user.getHandle(), font1);//处理意见

//            p15.setIndentationRight(200);
//            p16.setIndentationRight(200);
//            p17.setIndentationRight(200);
//            p18.setIndentationRight(200);
//            doc.add(p1);
//            doc.add(p17);
//            doc.add(p15);
//            doc.add(p16);
//            doc.add(p19);
//            doc.add(p18);
//            doc.add(p20);
            doc.add(paragraph);
            doc.add(initAlignment(p12,p12_1));
            doc.add(initAlignment(p14,p14_1));
            doc.add(initAlignment(p50,p50_1));
            int isDisplay = (int) SPUtils.get(context, Const.display, -1);
//            if(isDisplay == 1){
//                Paragraph p56=new Paragraph("   ",font1);
//
//                Paragraph p57=new Paragraph(context.getString(R.string.print_Assessment_results)+":  ",font2);
//
//                Paragraph p51 = new Paragraph(context.getString(R.string.print_border)+"：  ", font1);
//                Paragraph p51_1 = new Paragraph( user.getBianjie(), font1);
//                Paragraph p52 = new Paragraph(context.getString(R.string.print_color)+"：  ", font1);
//                Paragraph p52_1 = new Paragraph(user.getColor(), font1);
//                Paragraph p53 = new Paragraph(context.getString(R.string.print_blood_vessel)+"：  ", font1);
//                Paragraph p53_1 = new Paragraph(user.getXueguna(), font1);
//                Paragraph p54 = new Paragraph(context.getString(R.string.print_Iodine_staining)+"：  ", font1);
//                Paragraph p54_1 = new Paragraph(user.getDianranse(), font1);
//                Paragraph p55 = new Paragraph(context.getString(R.string.print_Acetic_acid_change)+"：  ", font1);
//                Paragraph p55_1 = new Paragraph(user.getCusuan(), font1);
//                doc.add(p56);
//                doc.add(p57);
//                doc.add(initAlignment(p51,p51_1));
//                doc.add(initAlignment(p52,p52_1));
//                doc.add(initAlignment(p53,p53_1));
//                doc.add(initAlignment(p54,p54_1));
//                doc.add(initAlignment(p55,p55_1));
//            }

            /**
             * 指定位置添加文本
             */
            PdfContentByte cb = writer.getDirectContent();//底部检查时间
            ColumnText ct3 = new ColumnText(cb);
            ct3.setSimpleColumn(new Phrase(context.getString(R.string.pdfcreate_check_data)+"：  " + user.getCheckDate(),font1), 20, 15, 580, 30, 15, Element.ALIGN_LEFT);
            ct3.go();

            ColumnText ct4 = new ColumnText(cb);//底部检查医生
            ct4.setSimpleColumn(new Phrase(context.getString(R.string.pdfcreate_check_doctor)+"：",font1), 20, 15, 500, 30, 15, Element.ALIGN_RIGHT);
            ct4.go();

            Phrase phrase1=new Phrase(" ");//底部横线
            phrase1.add(new Chunk(new LineSeparator()));
            phrase1.add("");
            ColumnText ct5 = new ColumnText(cb);
            ct5.setSimpleColumn(phrase1, 20, 20, 580, 45, 15, Element.ALIGN_RIGHT);
            ct5.go();

            int page =writer.getPageNumber();//判断当前pdf的页数
//                Log.e("页数",page+"");
            if(page>1){//当有2页时，添加表头
                ColumnText ct = new ColumnText(cb);//顶部患者编号
                ct.setSimpleColumn(new Phrase(MyApplication.getContext().getString(R.string.print_patient_id)+": "+user.getpId(),font1), 20, 780, 580, 840, 15, Element.ALIGN_LEFT);
                ct.go();

                ColumnText ct1 = new ColumnText(cb);//顶部检查时间
                ct1.setSimpleColumn(new Phrase(context.getString(R.string.patient_name)+"：" + user.getpName(),font1), 20, 780, 580, 840, 15, Element.ALIGN_RIGHT);
                ct1.go();

                Phrase phrase=new Phrase(" ");//顶部横线
                phrase.add(new Chunk(new LineSeparator()));
                phrase.add("");
                ColumnText ct2 = new ColumnText(cb);
                ct2.setSimpleColumn(phrase, 20, 775, 580, 835, 15, Element.ALIGN_RIGHT);
                ct2.go();
            }
            doc.close();
//            int page1 =writer.getPageNumber();
//            Log.e("页数1",page1+"");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("报告生成异常",e.getMessage());
        }finally {
            mPdfCreateInterface.pdfSuccess(index);
        }
    }

    static  PdfCreateInterface mPdfCreateInterface;

    public static void setPdfCreateInterfaceListener( PdfCreateInterface pdfCreateInterface) {
        mPdfCreateInterface = pdfCreateInterface;

    }

    public interface PdfCreateInterface {

        void pdfSuccess(int index);
    }

    //旋转图片
    private Bitmap rotateBimap(float degree,Bitmap srcBitmap){
        if(srcBitmap!=null){
            Matrix matrix=new Matrix();
            matrix.reset();
            matrix.setRotate(degree);
            Bitmap bitmap=Bitmap.createBitmap(srcBitmap,0,0,srcBitmap.getWidth(),srcBitmap.getHeight(),matrix,true);
            return bitmap;
        }
        return null;
    }
    //在本地生成一个新的旋转90度的新文件
    private String getNewPic(Bitmap bitmap){
        Bitmap newBitmap=rotateBimap(90,bitmap);
        String fn = "temporary.png";//设置缓存到本地图片的名称
        String path = new Item().getSD() + "/AFLY/" + fn;//声明保存的路径
        try {
            OutputStream os = new FileOutputStream(path);//输出流
            newBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);//设置图片的大小
            os.close();//关闭流
            if(new File(path).exists()&&new File(path).length()>0){
                return path;
            }
        } catch (Exception e) {
            Log.e("TAG", "", e);
        }
        return null;
    }
    //对诊断信息进行格式的调整，使之对齐
    private PdfPTable initAlignment(Paragraph paragraph,Paragraph paragraph1){

        try {
            PdfPTable table5=new PdfPTable(2);
            int[] TableWidths = { 15,85};//按百分比分配单元格宽带
            table5.setWidths(TableWidths);
            table5.setWidthPercentage(100);
            table5.getDefaultCell().setBorder(0);
            table5.addCell(paragraph);
            table5.addCell(paragraph1);
            return table5;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }
    //    //将诊断信息放入同一个对象，方便操作
//    private ParagraghObj getPara(){
//
//    }
    class ParagraghObj{
        private Paragraph title;
        private Paragraph content;

        public Paragraph getTitle() {
            return title;
        }

        public void setTitle(Paragraph title) {
            this.title = title;
        }

        public Paragraph getContent() {
            return content;
        }

        public void setContent(Paragraph content) {
            this.content = content;
        }
    }
}
