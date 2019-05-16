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

import com.shizhenbao.activity.ZhenDuanXiugaiActivity;
import com.shizhenbao.adapter.ImageManagerAdapter;
import com.activity.R;
import com.shizhenbao.activity.ImageManegerActivity;
import com.shizhenbao.adapter.ImageManagerAdapter02;
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
import java.util.logging.Logger;


/**
 * A simple {@link Fragment} subclass.
 */
public class NOTYETFragment extends BaseFragment  {
    private GridView gv;
    //    private Button bt_rush;
//    private List<Item> list;
    String id;
    private User user;
    String gathPath;
    private List<Item> listitem = null;
    public ImageManagerAdapter02 adapter;
    public int i = 0;
    private List<String> list1 = new ArrayList<>();//病人照片路径集合
//    private boolean isVisibles=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notyet, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = getActivity().getIntent();
        id = intent.getStringExtra("msg");
        initSelect(id);
        showList();
//        ImageManegerActivity.setOnCancleImageListner(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        showList();
    }

    private void initView(View view) {
        gv = (GridView) view.findViewById(R.id.gv);
//        bt_rush= (Button) view.findViewById(R.id.flush);
    }

    public String initSelect(String id) {

        if (!TextUtils.isEmpty(id)) {
            user = new LoginRegister().getUserName(Integer.parseInt(id));
        }
        gathPath = user.getGatherPath();
        return gathPath;
    }

    //判断当前页面是否是显示状态
    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            Const.isVisible = 1;
        } else {
            Const.isVisible = 2;
        }
        com.orhanobut.logger.Logger.e("没有编辑过的图片 onFragmentVisibleChange isVisible :  "+Const.isVisible);
    }

    //展示该文件下病人所有的.png格式的图片
    public void showList() {
        gathPath = initSelect(id);
        if (gathPath == null) {
            return;
        }
        listitem = new ArrayList<>();
        if (new Item().getSD() != null) {
            File[] files = new File(gathPath).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    boolean a = false;
                    if (name.endsWith(".jpg")) {//所有.jpg格式的文件添加到数组中
                        a = true;
                    }
                    return a;
                }
            });
            if (files.length != 0) {
                //遍历数组动态添加图片到对象中去
                for (File file : files) {
                    if (!file.getName().equals("方位.png") && file.getName() != "方位.png") {
                        Item itemImage = new Item();
                        itemImage.setCb(new CheckBox(getContext()));
                        itemImage.setPath(gathPath + "/" + file.getName());//图片路径
                        listitem.add(itemImage);
                    }
                }
            }
            adapter = new ImageManagerAdapter02(getActivity(), listitem);
            gv.setAdapter(adapter);
//            ImageManagerAdapter.setList();
            adapter.notifyDataSetChanged();
        }
        else {
            new File(gathPath).mkdirs();
            showList();
        }
    }

    /**
     * 得到SD卡根目录
     *
     * @return
     */
    public File getSD() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory();

        } else {
            return null;
        }
    }


    public void deleteImage() {
        com.orhanobut.logger.Logger.e("deleteImage  没有编辑的图像显示界面 OnCancleImage  ");
        list1 = ImageManagerAdapter02.getList();//选择病人上传图片集合
        if (list1.size() == 0) {
            MyToast.showToast(getContext().getApplicationContext(),getString(R.string.print_picture_no_choise));
//            Toast.makeText(getContext().getApplicationContext(), getString(R.string.print_picture_no_choise), Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < list1.size(); i++) {
            File file = new File(list1.get(i));
            file.delete();
        }
        ImageManagerAdapter02.setList();

        try {
            showList();//医生诊断数据源展示
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }


}
