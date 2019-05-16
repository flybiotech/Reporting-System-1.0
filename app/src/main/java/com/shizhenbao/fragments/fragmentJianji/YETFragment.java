package com.shizhenbao.fragments.fragmentJianji;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.Toast;

import com.shizhenbao.adapter.ImageManagerAdapter;
import com.activity.R;
import com.shizhenbao.activity.ImageManegerActivity;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.fragments.BaseFragment;
import com.shizhenbao.pop.User;
import com.shizhenbao.util.Const;
import com.shizhenbao.util.Item;
import com.view.MyToast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class YETFragment extends BaseFragment  {
    private GridView gv;
    //    private Button bt_rush;
//    private List<Item> list;
    String id;
    private User user;
    String gathPath;
    private List <Item>listitem=null;
    public ImageManagerAdapter adapter;
    private List<String>list1=new ArrayList<>();//病人照片路径集合
//    private boolean isVisibless=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_yet, container, false);
        initView(view);
//        Intent intent=getActivity().getIntent();
//        id=intent.getStringExtra("msg");
//        initSelect(id);
//        showList1();
//        iniTClick();
        return view;
    }
    private void initView(View view) {
        gv= (GridView) view.findViewById(R.id.gv);

//        bt_rush= (Button) view.findViewById(R.id.flush);
    }
    public String initSelect(String id){

        if(!TextUtils.isEmpty(id)){
            user=new LoginRegister().getUserName(Integer.parseInt(id));
        }
        gathPath=user.getGatherPath();
        return gathPath;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent=getActivity().getIntent();
        id=intent.getStringExtra("msg");
        initSelect(id);
        showList1();
//        ImageManegerActivity.setOnCancleImageListner(this);
    }


    //判断当前页面是否是显示状态
    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            //表示现在显示的界面是已编辑过的界面
            Const.isVisible = 2;
        } else {
            Const.isVisible = 1;
        }
    }



    //展示该文件下医生所有的.png格式的图片
    private void showList1() {
        gathPath=initSelect(id);
        listitem = new ArrayList<>();
        if (new Item().getSD() != null) {
            String strtpath = gathPath+"/User_cut";
            if(new File(strtpath).exists()){
//                Toast.makeText(this, "医生路径："+strtpath, Toast.LENGTH_SHORT).show();
                File[] files = new File(strtpath).listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        boolean a = false;
                        if (name.endsWith(".png")) {
                            a = true;
                        }
                        return a;
                    }
                });
                if(files.length!=0){
                    for (File file : files) {
                        Item itemImage=new Item();
                        itemImage.setCb(new CheckBox(getContext()));
                        itemImage.setPath(strtpath+"/"+file.getName());
                        listitem.add(itemImage);
                    }
                }
//            ImageDCAdapter a = new ImageDCAdapter(this, list);
                adapter = new ImageManagerAdapter(getActivity(),listitem);
                gv.setAdapter(adapter);
            }else {
                new File(strtpath).mkdirs();
                showList1();
            }
        }
    }
    /**
     * 得到SD卡根目录
     * @return
     */
    public File getSD(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return Environment.getExternalStorageDirectory();

        }else {
            return null;
        }
    }



    public void deleteImage(){
        com.orhanobut.logger.Logger.e(" 2 yes 编辑过的图像显示界面 OnCancleImage  ");
        list1= ImageManagerAdapter.getList();//选择病人上传图片集合
        if (list1.size() == 0) {
            MyToast.showToast(getContext().getApplicationContext(),getString(R.string.print_picture_no_choise));
//            Toast.makeText(getContext().getApplicationContext(),getString(R.string.print_picture_no_choise), Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i=0;i<list1.size();i++) {
            File file = new File(list1.get(i));
            file.delete();
        }
        ImageManagerAdapter.setList();

        try {
            showList1();//医生诊断数据源展示
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();



}



}
