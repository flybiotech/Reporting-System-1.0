package com.shizhenbao.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.activity.R;
import com.application.MyApplication;
import com.orhanobut.logger.Logger;


public class HelpShowActivity extends AppCompatActivity implements View.OnClickListener{
    private int helpPosition;
    private TextView tv01, tv02,tv03,title;
    private String helpTitle;
    private ImageView image01, image02,image03;
    private Button btn_left, btn_right;
    private int MDEFAULT = -1;//用于图片为空的情况
    private boolean isChina = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕休眠
        setContentView(R.layout.activity_help_show);
        helpPosition=getIntent().getIntExtra("helpItem", 0);
        helpTitle=getIntent().getStringExtra("helpTitle");
        Logger.e("HelpActivity 传过来的值： "+helpTitle+"   传过来的position :"+helpPosition);
        initView();
        switchInfo(helpPosition);

    }
    private void initView() {
        tv01 = (TextView) findViewById(R.id.textview_help_item_01);
        tv02 = (TextView) findViewById(R.id.textview_help_item_02);
        tv03 = (TextView) findViewById(R.id.textview_help_item_03);
        title = (TextView) findViewById(R.id.title_text);//标题
        image01 = (ImageView) findViewById(R.id.image_help_item_01);
        image02 = (ImageView) findViewById(R.id.image_help_item_02);
        image03 = (ImageView) findViewById(R.id.image_help_item_03);
        btn_left = (Button) findViewById(R.id.btn_left);//返回
        btn_left.setVisibility(View.VISIBLE);
        btn_left.setOnClickListener(this);
        btn_right = (Button) findViewById(R.id.btn_right);
        btn_right.setVisibility(View.INVISIBLE);
        isChina = MyApplication.getInstance().getCountry().equals("CN");


    }

    @Override
    protected void onStart() {
        super.onStart();
//        isChina = MyApplication.getInstance().getCountry().equals("CN");
        //标题
        if (isChina) {
            title.setText(helpTitle);
        } else {
            title.setText("Details");
        }
    }

    private void switchInfo(int position) {
        switch (position) {
            case 0:showInfo(getString(R.string.setting_ordinary_register_show1)+"\n",
                    getString(R.string.setting_ordinary_register_show2)+"\n","",
                    MDEFAULT,MDEFAULT,MDEFAULT);
                break;
            case 1:
                showInfo(getString(R.string.setting_select_patient_message_show1)+"\n",
                        getString(R.string.setting_select_patient_message_show2)+"\n",
                        getString(R.string.setting_select_patient_message_show3)+"\n",MDEFAULT,MDEFAULT,MDEFAULT);
                break;
            case 2:
                showInfo(getString(R.string.setting_Treasures_of_vision_link_faild_show1)+"\n",
                        getString(R.string.setting_Treasures_of_vision_link_faild_show2)+"\n",
                        getString(R.string.setting_Treasures_of_vision_link_faild_show3)+"\n ",
                        MDEFAULT,MDEFAULT,MDEFAULT);
                break;
            case 3:
                showInfo(getString(R.string.setting_print_attention_show1)+"\n",
                        "",
                        "",
                        MDEFAULT,MDEFAULT,MDEFAULT);
                break;
            case 4:
                showInfo(getString(R.string.setting_select_case_show)+"\n",
                        "",
                        "",
                        MDEFAULT,MDEFAULT,MDEFAULT);
                break;
            case 5:
                showInfo(getString(R.string.setting_Contrast_case_show1)+"\n",
                        getString(R.string.setting_Contrast_case_show2)+"\n",
                        "",
                        MDEFAULT,MDEFAULT,MDEFAULT);
                break;
//            case 6:
//                if (isChina) {
//                    showInfo(getString(R.string.setting_Treasures_of_vision_light_show1) + "\n", getString(R.string.setting_Treasures_of_vision_light_show2) + "\n", "",
//                            R.drawable.help0901, R.drawable.help0902, MDEFAULT);
//                } else {
//                    showInfo(getString(R.string.setting_Treasures_of_vision_light_show1)+"\n",getString(R.string.setting_Treasures_of_vision_light_show2)+"\n","",
//                            R.drawable.help0901e,R.drawable.help0902e,MDEFAULT);
//                }
//
//                break;
            case 6:

                if (isChina) {
                    showInfo(getString(R.string.setting_diagnosis_delete_show1)+"\n",getString(R.string.setting_diagnosis_delete_show2)+"\n","",
                            R.drawable.help1001,R.drawable.help1002,MDEFAULT);
                } else {
                    showInfo(getString(R.string.setting_diagnosis_delete_show1)+"\n",getString(R.string.setting_diagnosis_delete_show2)+"\n","",
                            R.drawable.help1001e,R.drawable.help1002e,MDEFAULT);
                }
                break;

//            case 8:
//                showInfo(getString(R.string.setting_removal_used_show)+"\n",
//                        "",
//                        "",
//                        MDEFAULT,MDEFAULT,MDEFAULT);
//                break;

            case 8:
                if (isChina) {
                    showInfo(getString(R.string.setting_picture_edit_show1)+"\n",getString(R.string.setting_picture_edit_show2)+"\n",getString(R.string.setting_picture_edit_show3)+"\n",
                            R.drawable.help1101, R.drawable.help1102,R.drawable.help1103);
                } else {
                    showInfo(getString(R.string.setting_picture_edit_show1)+"\n",getString(R.string.setting_picture_edit_show2)+"\n",getString(R.string.setting_picture_edit_show3)+"\n",
                            R.drawable.help1101e, R.drawable.help1102e,R.drawable.help1103e);
                }
                break;

//            case 9:
//                if (isChina) {
//                    showInfo(getString(R.string.setting_diagnosis_delete_show1)+"\n",getString(R.string.setting_diagnosis_delete_show2)+"\n","",
//                            R.drawable.help1001,R.drawable.help1002,MDEFAULT);
//                } else {
//                    showInfo(getString(R.string.setting_diagnosis_delete_show1)+"\n",getString(R.string.setting_diagnosis_delete_show2)+"\n","",
//                            R.drawable.help1001e,R.drawable.help1002e,MDEFAULT);
//                }
//
//                break;
//
//            case 10:
//                if (isChina) {
//                    showInfo(getString(R.string.setting_picture_edit_show1)+"\n",getString(R.string.setting_picture_edit_show2)+"\n",getString(R.string.setting_picture_edit_show3)+"\n",
//                            R.drawable.help1101, R.drawable.help1102,R.drawable.help1103);
//                } else {
//                    showInfo(getString(R.string.setting_picture_edit_show1)+"\n",getString(R.string.setting_picture_edit_show2)+"\n",getString(R.string.setting_picture_edit_show3)+"\n",
//                            R.drawable.help1101e, R.drawable.help1102e,R.drawable.help1103e);
//                }
//
//                break;
//
//            case 11:
//                break;
            default:
                break;

        }
    }

    private void showInfo(String  text01,String text02,String text03, int imageId01,int imageId02,int imageId03) {
        tv01.setText(text01);
        tv02.setText(text02);
        tv03.setText(text03);
        if (imageId01 != MDEFAULT) {
            image01.setImageBitmap(getThumbnail(imageId01, 400, 600));
//            image01.setBackgroundResource(imageId01);
        }

        if (imageId02 != MDEFAULT) {
            image02.setImageBitmap(getThumbnail(imageId02, 400, 600));
//            image02.setBackgroundResource(imageId02);

        }

        if (imageId03 != MDEFAULT) {
            image03.setImageBitmap(getThumbnail(imageId03, 400, 600));
//            image03.setBackgroundResource(imageId03);

        }


    }


    /**
     * 获取图片缩略图
     * @params path 图片的完整路径
     * @param width 缩略图宽，单位：像素
     * @param height 缩略图高，单位：像素
     * @return 图片缩略图的Bitmap，若出现异常，返回null
     */
    public static Bitmap getThumbnail( int imageId,int width, int height) {
        Bitmap bitmap = null;
        Bitmap thumbnail = null;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            //也就是说，如果我们把它设为true，那么BitmapFactory.decodeFile(String path, Options opt)并不会真的返回一个Bitmap给你，
            // 它仅仅会把它的宽，高取回来给你，这样就不会占用太多的内存，也就不会那么频繁的发生OOM了。
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), imageId, options);
            width = options.outWidth;
            height = options.outHeight;
            options.inJustDecodeBounds = false;

            /**
             * 这个值是一个int，当它小于1的时候，将会被当做1处理，如果大于1，那么就会按照比例（1 / inSampleSize）缩小bitmap的宽和高、
             * 降低分辨率，大于1时这个值将会被处置为2的倍数。例如，width=100，height=100，inSampleSize=2，那么就会将bitmap处理为，
             * width=50，height=50，宽高降为1 / 2，像素数降为1 / 4。
             */
            options.inSampleSize = 3;
            bitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), imageId, options);
//            bitmap = BitmapFactory.decodeFile(path, options);
            thumbnail = ThumbnailUtils.extractThumbnail(bitmap, width, height);
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return thumbnail;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            default:
                break;
        }
    }


}
