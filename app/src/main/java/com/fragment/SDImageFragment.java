package com.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activity.ImageBrowserActivity;
import com.activity.R;
import com.adapter.SDImageRecyclerAdapter;
import com.model.DevModel;
import com.model.SDImageModel;
import com.orhanobut.logger.Logger;
import com.shizhenbao.db.LoginRegister;
import com.shizhenbao.util.Const;
import com.util.SouthUtil;
import com.view.LoadingDialog;
import com.view.MyToast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by gyl1 on 3/30/17.
 */

public class SDImageFragment extends Fragment implements SDImageRecyclerAdapter.OnImageClickListner {
//    private DevModel model;
//    private int page = 0;
//    private int length = 10;


    public RecyclerView mRecyclerView;
//    public SwipeRefreshLayout mSwipeRefreshLayout;
    public GridLayoutManager mGridLayoutManager;
    public SDImageRecyclerAdapter adapter;
    OnCheckChangeListner mOnCheckChangeListner;

    LoadingDialog lod;

    public List<SDImageModel> mImages = new ArrayList<>();
    public ArrayList<String> urls = new ArrayList<>(); //保存图片的路径
    //保存准备删除的图片的 SDImageModel
    public ArrayList<SDImageModel> deleteFileImageList = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        initView(rootView);
        initRecylerView();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
// TODO Auto-generated method stub
        Log.d(this.getTag(), "onResume---------");
        super.onResume();
        Const.videoType =1;

    }

    @Override
    public void onPause() {
// TODO Auto-generated method stub
        Log.d(this.getTag(), "onPause--------");
        super.onPause();
    }


    public void setModel(DevModel m) {
//        if (m != null) {
//            model = m;
//            page = 0;
//        }
    }

    private void initView(View v) {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initRecylerView() {
        adapter = new SDImageRecyclerAdapter(this.getActivity(), mImages);
        adapter.setOnImageClickListner(this);
        mGridLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(adapter);
        getImageData();
    }

    private void getImageData() {
//        String gatherPath = new LoginRegister().getUserName(Integer.parseInt(Const.userId)).getGatherPath();
//        Logger.e("图片的路径 gatherPath = "+gatherPath +"   患者的编号 id = "+Const.userId+"  Const.saveImageFilePath = "+Const.saveImageFilePath);
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(Const.saveImageFilePath);
                subscriber.onCompleted();

            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        showList(s);
                    }
                });

    }

    //展示该文件下病人所有的..jpg格式的图片
    public void showList(final String  filePath) {
        mImages.clear();
        File[] files = new File(filePath).listFiles(); //获取当前文件夹下的所有文件和文件夹
        Observable.from(files)
                .filter(new Func1<File, Boolean>() {
                    @Override
                    public Boolean call(File file) {
                        return file.getName().endsWith(".jpg") && !file.getName().equals("方位.png");
                    }
                })
                .map(new Func1<File, String>() {
                    @Override
                    public String call(File file) {
                        return file.getAbsolutePath();
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        adapter.setList(mImages);
                        adapter.notifyDataSetChanged();
                        mRecyclerView.invalidate();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        SDImageModel sdImageModel = new SDImageModel();
                        sdImageModel.sdImage = s;
                        mImages.add(sdImageModel);
                        urls.add(s);
//                        Logger.e(" 图片的数量 onNext mImages = " + s);
                    }
                });
    }




    @Override
    public void OnImageClicked(View view, int position) {


//        显示放大之后的图片
        Intent intent = new Intent(this.getActivity(), ImageBrowserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("urls", urls);
//        bundle.putStringArrayList("names", names);
        bundle.putInt("channel", position);
//        bundle.putParcelable("devmodel", model);
        intent.putExtras(bundle);
// startActivity(intent);
        this.getActivity().startActivityForResult(intent, 1);
        this.getActivity().overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

    @Override
    public void OnCheckClicked() {
//通知有变化
        if (mOnCheckChangeListner != null) {
            mOnCheckChangeListner.OnCheckChange();
        }
    }

    public boolean isAnyItemChecked() {
        boolean isAnySelected = false;
        for (int i = mImages.size() - 1; i >= 0; i--) {
            SDImageModel model = mImages.get(i);
            if (model.checked) {
                isAnySelected = true;
                break;
            }
        }
        return isAnySelected;
    }

    public boolean checkAll() {

//首先判断是否已经有选择
        boolean isAnySelected = false;
        for (int i = mImages.size() - 1; i >= 0; i--) {
            SDImageModel model = mImages.get(i);
            if (model.checked) {
                isAnySelected = true;
                break;
            }
        }

        if (isAnySelected) {
            for (int i = mImages.size() - 1; i >= 0; i--) {
                SDImageModel model = mImages.get(i);
                model.checked = false;
            }
            adapter.notifyDataSetChanged();
            return false;
        } else {
            for (int i = mImages.size() - 1; i >= 0; i--) {
                SDImageModel model = mImages.get(i);
                model.checked = true;
            }
            adapter.notifyDataSetChanged();
            return true;
        }

    }



    public void deleteImages() {

        boolean isAnySelected = false;
        for (int i = mImages.size() - 1; i >= 0; i--) {
            SDImageModel model = mImages.get(i);
            if (model.checked) {
                isAnySelected = true;
                break;
            }
        }
        if (!isAnySelected) {
            MyToast.showToast(this.getContext(),getString(R.string.chooseImageForDelete));
//            SouthUtil.showToast(this.getContext(), getString(R.string.chooseImageForDelete));
            return;
        }

        if (lod == null) {
            lod = new LoadingDialog(this.getContext());
        }
        lod.dialogShow();
//        String fileName = "";
        for (SDImageModel model : mImages) {
            if (model.checked) {
//                Log.e(tag, "checked:" + model.sdImage);
                deleteFileImageList.add(model);
//                fileName += model.sdImage + ",";
            }
        }




        Observable.from(deleteFileImageList)
                .map(new Func1<SDImageModel, Boolean>() {
                    @Override
                    public Boolean call(SDImageModel sdImageModel) {
                        mImages.remove(sdImageModel);
                        File file = new File(sdImageModel.sdImage);
                        boolean delete = file.delete();

                        return delete;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        if (lod != null) {
                            lod.dismiss();
                        }

                        urls.clear();
//                        names.clear();
                        for (SDImageModel sdImageModel : mImages) {
                            urls.add(sdImageModel.sdImage);
//                            names.add(sdImageModel.sdImage);
                        }
                        if (mOnCheckChangeListner != null) {
                            mOnCheckChangeListner.OnCheckChange();
                        }

                        adapter.setList(mImages);
                        adapter.notifyDataSetChanged();
                        mRecyclerView.invalidate();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {


                    }
                });



//        Network.getCommandApi(model).deleteFile(model.usr, model.pwd, 77, fileName, SouthUtil.getRandom())
//
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<List<RetModel>>() {
//
//                    @Override
//                    public void onCompleted() {
//                        lod.dismiss();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        lod.dismiss();
//                    }
//
//                    @Override
//                    public void onNext(List<RetModel> rets) {
//                        Log.e(tag, "delete success");
//                        lod.dismiss();
//                        for (int i = mImages.size() - 1; i >= 0; i--) {
//                            SDImageModel model = mImages.get(i);
//                            if (model.checked) {
//                                mImages.remove(model);
//                                Log.e(tag, "remove " + model.sdImage + "mImages size is " + mImages.size());
//                            }
//                        }
//                        urls.clear();
//                        names.clear();
//                        for (SDImageModel sdImageModel : mImages) {
//                            urls.add(sdImageModel.getSdImageUrl(model));
//                            names.add(sdImageModel.sdImage);
//                        }
//                        if (mOnCheckChangeListner != null) {
//                            mOnCheckChangeListner.OnCheckChange();
//                        }
//
//                        adapter.setList(mImages);
//                        adapter.notifyDataSetChanged();
//                        mRecyclerView.invalidate();
//
//                    }
//                });
    }

    /**
     * @param type 0 if no data ,data request 1:date request
     */
    public void getImagesInActivity(int type) {
        if (mImages == null) {
            mImages = new ArrayList<>();
        }
        if (mImages.size() == 0 || type == 1) {

//            handler.postDelayed(runnable, 50);
        }

    }


//    /**
//     * 选择文件夹之后，被调用
//     *
//     * @param type 0 选中
//     * @param path
//     */
//    public void chooseOrCancel(int type, String path) {
//        if (mDialog != null)
//            mDialog.dismiss();
//        if (0 == type) {
//            downloadImages(1, path);
//        }
//    }
//
//    private DirectoryChooserFragment mDialog;

    public interface OnCheckChangeListner {

        void OnCheckChange();//如果有点击，则通知
    }

    public void setOnCheckChangeListner(OnCheckChangeListner onCheckChangeListner) {
        mOnCheckChangeListner = onCheckChangeListner;
    }










}
