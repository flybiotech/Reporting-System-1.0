package com.shizhenbao.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.activity.R;
import com.application.MyApplication;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.shizhenbao.pop.Doctor;
import com.shizhenbao.pop.User;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 用户 on 2017/4/1.
 */

public class PDFCreate {
    private List<String> list = new ArrayList<>();
    List<Doctor> list1;
    private Doctor doctor;
    final List<Doctor>doctorList=DataSupport.where("dName=?","Admin").find(Doctor.class);//查迅数据库所有的数据
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

        list1 = DataSupport.findAll(Doctor.class);
        for (int i = 0; i < list1.size(); i++) {
            doctor = list1.get(list1.size() - 1);
        }
        Document doc = new Document(PageSize.A4, 20, 20, 20, 20);//页面布局
        File foder = new File(user.getGatherPath());//声明存储位置
        if (!foder.exists()) {//判断文件夹是否存在，如不存在就重新创建
            foder.mkdirs();
        }

        File myCaptureFile = new File(foder,((user.getpId() +"_"+ user.getpName()) + ".pdf"));//声明文件名称
//        if (!myCaptureFile.exists()) {//判断文件是否存在，如不存在重新创建
//            try {
//                myCaptureFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        OneItem.getOneItem().setFile(myCaptureFile);
        try {

            BaseFont baseFont = BaseFont.createFont("assets/fonts/STSONG.TTF",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);//设置中文显示问题
            Font font = new Font(baseFont, 32);
            Font font1 = new Font(baseFont, 12);
            //这里有可能出现了问题
            FileOutputStream fOut = new FileOutputStream(myCaptureFile);//输出流
            PdfWriter.getInstance(doc, fOut);

            Font font2 = new Font(baseFont, 15);
            font2.setColor(BaseColor.BLUE);
            doc.open();
            if (!myCaptureFile.exists()) {//判断文件是否存在，如不存在重新创建
                myCaptureFile.createNewFile();
            }
            for(int i=0;i<doctorList.size();i++){
                Paragraph p = new Paragraph(doctorList.get(0).getEdit_hos_name(), font);//医院
                p.setAlignment(Element.ALIGN_CENTER);
                doc.add(p);

                Paragraph p2 = new Paragraph(doctorList.get(0).getEdit_hos_keshi(), font1);//科室
                p2.setAlignment(Element.ALIGN_CENTER);
                doc.add(p2);
            }
            Paragraph p40 = new Paragraph(MyApplication.getContext().getString(R.string.print_patient_id)+": "+user.getpId(), font1);
            doc.add(p40);
            Paragraph p3 = new Paragraph("");//添加横线
            p3.add(new Chunk(new LineSeparator()));
            p3.add("");
            doc.add(p3);
//            View view=new View(context);
//            view.setBackgroundColor(Color.GRAY);
//            doc.add((Element) view);
            //创建表格添加第一行数据
            Paragraph p4 = new Paragraph(context.getString(R.string.patient_name)+"：" + user.getpName(), font1);
            Paragraph p6 = new Paragraph(context.getString(R.string.patient_age)+"：" + user.getAge(), font1);
            Paragraph p7 = new Paragraph(context.getString(R.string.patient_medical_record_number)+"：" + user.getCaseNumbe(), font1);
            PdfPTable table1 = new PdfPTable(3); // 4 columns.创建表格，设置列数
            table1.setWidthPercentage(100); // Width 100%
            table1.setSpacingBefore(10f); // Space before table
            table1.setSpacingAfter(10f); // Space after table
            table1.addCell(p4);
            table1.addCell(p6);
            table1.addCell(p7);
            doc.add(table1);
            /////////////////////////////////// ///////////////////////////////////////////////////////////////////////////////
            //创建表格添加第二行数据
            Paragraph p8 = new Paragraph(context.getString(R.string.patient_source)+"：" + user.getpSource(), font1);
            Paragraph p10 = new Paragraph(context.getString(R.string.patient_marriage)+"：" + user.getMarry(), font1);
            Paragraph p11 = new Paragraph(context.getString(R.string.patient_link_telephoen)+"：" + user.getTel(), font1);
            PdfPTable table2 = new PdfPTable(3); // 4 columns.创建表格，设置列数
            table2.setWidthPercentage(100); // Width 100%
            table2.setSpacingBefore(10f); // Space before table
            table2.setSpacingAfter(10f); // Space after table
            table2.addCell(p8);
            table2.addCell(p10);
            table2.addCell(p11);
            doc.add(table2);
            ////////////////////////////////////////////////////////
//            //设置横线
//            Paragraph p0 = new Paragraph("");//添加横线
//            p0.add(new Chunk(new LineSeparator()));
//            p0.add("");
//            doc.add(p0);

/**
 * 创建表格添加图片-------------------------------------------------------------------------------------------------------
 */
            list = user.getImgPath();
//            if(list.size()>0){
                PdfPTable table = new PdfPTable(3); // 3 columns.创建表格，设置列数
                table.setWidthPercentage(100); // Width 100%
                table.getDefaultCell().setFixedHeight(105);
                table.setSpacingBefore(10f); // Space before table
                table.setSpacingAfter(10f); // Space after table
                if (list.size() % 3 != 0) {
                    if (list.size() % 3 == 1) {
                        for (int i = 0; i < 2; i++) {
                            list.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFLY/kb.png");
                        }
                        for (int i = 0; i < list.size(); i++) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();//添加图片
                            Bitmap bitmap = BitmapFactory.decodeFile(list.get(i));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            Image myImg2 = Image.getInstance(stream.toByteArray());
                            table.addCell(myImg2);
                        }
                    } else {
                        list.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFLY/kb.png");
                        for (int i = 0; i < list.size(); i++) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();//添加图片
                            Bitmap bitmap = BitmapFactory.decodeFile(list.get(i));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            Image myImg2 = Image.getInstance(stream.toByteArray());
                            table.addCell(myImg2);
                        }
                    }
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();//添加图片
                        Bitmap bitmap = BitmapFactory.decodeFile(list.get(i));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        Image myImg2 = Image.getInstance(stream.toByteArray());
                        table.addCell(myImg2);
                    }
                }
//            table.
                doc.add(table);
//            }

            //--------------------------------------------------------------------------------------------------------------

            Image myImg2=null;
            Bitmap bitmap=null;
            File file=new File(user.getGatherPath()+"/方位.png");
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            if(!file.exists()){
                bitmap=BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFLY/fwt.png");
            }else {
                bitmap=BitmapFactory.decodeFile(user.getGatherPath()+"/方位.png");
            }
//            Bitmap newBitmap=Bitmap.createBitmap(bitmap,0,0,50,50);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            myImg2 = Image.getInstance(stream.toByteArray());


            Image myImg3=null;
            ByteArrayOutputStream stream3=new ByteArrayOutputStream();
            if (MyApplication.getInstance().getCountry().equals("CN")) {
                Bitmap bitmap3=BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFLY/qq.png");
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
            table3.setSpacingAfter(10f); // Space after table
            table3.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            table3.addCell(p23);
            table3.addCell(p24);
            table3.addCell(myImg2);
            table3.addCell(myImg3);
//            table3.addCell("");
//            table3.addCell("");
            doc.add(table3);
//------------------------------------------------------------------------------------------------------------------------

            Paragraph p1 = new Paragraph(context.getString(R.string.pdfcreate_Inspection_record)+"：  ", font2);
            Paragraph p15 = new Paragraph(context.getString(R.string.pdfcreate_cervical_cytology)+"：  " + user.getAdvice(), font1);
            Paragraph p16 = new Paragraph(context.getString(R.string.pdfcreate_HPV)+"：  " + user.getHpv_dna(), font1);
            Paragraph p17 = new Paragraph(context.getString(R.string.print_symptom)+"：  " + user.getSymptom(), font1);
            Paragraph p18 = new Paragraph(context.getString(R.string.print_Overall_assessment)+"：  " + user.getSummary(), font1);
            Paragraph p19 = new Paragraph(context.getString(R.string.print_Lesion_area)+"：  " + user.getBingbian(), font1);
            Paragraph p20 = new Paragraph(context.getString(R.string.print_colposcopic)+"：  " + user.getYindaojin(), font1);
            Paragraph p12 = new Paragraph(context.getString(R.string.print_Suspected)+"：  " + user.getNizhen(), font1);
            Paragraph p14 = new Paragraph(context.getString(R.string.print_attention)+"：  " + user.getZhuyishixiang(), font1);
            Paragraph p30 = new Paragraph("                  ", font1);
            Paragraph p31 = new Paragraph("                  ", font1);
            Paragraph p21 = new Paragraph(context.getString(R.string.pdfcreate_check_data)+"：  " + user.getCheckDate() + "                                                                 " + context.getString(R.string.pdfcreate_check_doctor)+"：", font1);
//            PdfContentByte cb=writer.getDirectContent();
//            cb.saveState();
//            float xy=doc.bottom();
//            cb.showTextAligned(PdfContentByte.ALIGN_CENTER,"检查时间：  " + user.getCheckDate() + "                                                                 " + "检查医生：" + OneItem.getOneItem().getName(), (doc.right()+doc.left())/2, xy,0);
            p15.setIndentationRight(200);
            p16.setIndentationRight(200);
            p17.setIndentationRight(200);
            p18.setIndentationRight(200);
            doc.add(p1);
            doc.add(p17);
            doc.add(p15);
            doc.add(p16);
            doc.add(p19);
            doc.add(p18);
            doc.add(p20);
            doc.add(p12);
            doc.add(p14);
            doc.add(p30);
            doc.add(p31);
            doc.add(p21);
            doc.close();

        } catch (Exception e) {
            e.printStackTrace();
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






}
