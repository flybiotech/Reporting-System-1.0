package com.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activity.R;
import com.activity.VideoPlayerActivity;
import com.adapter.RecycleViewDivider;
import com.adapter.SDVideoRecyclerAdapter;
import com.model.DevModel;
import com.model.SDVideoModel;
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

public class SDVideoFragment extends Fragment implements SDVideoRecyclerAdapter.OnTextClickListner {
//    private DevModel model;
//    private int page = 0;
//    private int length = 15;

    SDImageFragment.OnCheckChangeListner mOnCheckChangeListner;
    LoadingDialog lod;
    static final String tag = "SDVideoFragment";
    public RecyclerView mRecyclerView;
//    public SwipeRefreshLayout mSwipeRefreshLayout;
    public LinearLayoutManager mLinearLayoutManager;
    ;
    public SDVideoRecyclerAdapter adapter;
    public List<SDVideoModel> mVideos = new ArrayList<>();
    public List<SDVideoModel> deleteFileVideoList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_video, container, false);
        initView(rootView);
        initRecylerView();
//        initModerl();


//        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
//                .newDirectoryName(getString(R.string.information))
//                .allowNewDirectoryNameModification(true)
//                .initialDirectory(Environment.getExternalStorageDirectory()
//                        .getAbsolutePath() + "/SZB_save/")
//                .build();
//        mDialog = DirectoryChooserFragment.newInstance(config);

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
        Const.videoType = 1;
    }

//    Handler handler = new Handler();
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
////            page = 0;
//            if (lod == null) {
//                lod = new LoadingDialog(SDVideoFragment.this.getContext());
//            }
//            lod.dialogShow();
//            getVideoData();
//        }
//    };


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
        mRecyclerView.addItemDecoration(new RecycleViewDivider(
                this.getContext(), LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.divider)));
//        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
//        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
    }

    private void initRecylerView() {
        adapter = new SDVideoRecyclerAdapter(this.getActivity(), mVideos);
        adapter.setOnTextClickListner(this);
//        adapter.setDevModel(model);
        mLinearLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(adapter);
        getVideoData();
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
////下拉刷新数据
//                        page = 0;
//                        getVideoData();
//                        mSwipeRefreshLayout.setRefreshing(false);
//                    }
//                }, 1500);
//            }
//        });
    }

//    private void initModerl() {
//
//        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            int lastVisibleItem;
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItem + 1) == adapter.getItemCount() && mVideos.size() > 14) {
//                    adapter.changeMoreStatus(adapter.LOADING_MORE);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
////上拉加载更多数据
//
//                            getVideoData();
//
//                        }
//                    }, 500);
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
////最后一个可见的ITEM的数目
//                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
//            }
//        });
//    }


    private void getVideoData() {

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

//        subscription = Observable.timer(50, TimeUnit.MILLISECONDS).flatMap(new Func1<Long, Observable<List<SDVideoModel>>>() {
//            @Override
//            public Observable<List<SDVideoModel>> call(Long m) {
//                return Network.getCommandApi(model).getVideoList(model.usr, model.pwd, 5, page++, length, SouthUtil.getRandom());
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(observer);
    }

    //展示该文件下病人所有的..jpg格式的图片
    public void showList(final String  filePath) {
        mVideos.clear();
        File[] files = new File(filePath).listFiles(); //获取当前文件夹下的所有文件和文件夹
        Observable.from(files)
                .filter(new Func1<File, Boolean>() {
                    @Override
                    public Boolean call(File file) {
                        return file.getName().endsWith(".mp4") ;
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

                        adapter.setList(mVideos);
                        adapter.notifyDataSetChanged();
                        mRecyclerView.invalidate();

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        SDVideoModel sdVideoModel = new SDVideoModel();
                        sdVideoModel.sdVideo = s;
                        mVideos.add(sdVideoModel);

                    }
                });
    }


    //    Observer<List<SDVideoModel>> observer = new Observer<List<SDVideoModel>>() {
//        @Override
//        public void onCompleted() {
//            lod.dismiss();
//            adapter.changeMoreStatus(adapter.PULLUP_LOAD_MORE);
//            mSwipeRefreshLayout.setRefreshing(false);
//        }
//
//        @Override
//        public void onError(Throwable e) {
//
//            Log.e(tag, e.toString());
//            lod.dismiss();
//            adapter.changeMoreStatus(adapter.PULLUP_LOAD_MORE);
//            mSwipeRefreshLayout.setRefreshing(false);
//        }
//
//        @Override
//        public void onNext(List<SDVideoModel> list) {
////由于现在参数没有效果，所以有新数据就把原来的清除
//            lod.dismiss();
//
//            for (int i = list.size() - 1; i >= 0; i--) {
//                SDVideoModel model = list.get(i);
//                if (!model.isSDVideo()) {
//                    list.remove(i);
//                }
//            }
//
//            DataManager manager = DataManager.getInstance();
//            List<SDVideoModel> dblist = manager.getAllSDVideos();
//            for (SDVideoModel model : list) {
//                for (SDVideoModel tmp : mVideos) {
//                    if (tmp.sdVideo.equals(model.sdVideo)) {
//                        model.checked = tmp.checked;
//                    }
//                }
//                for (SDVideoModel dbmodel : dblist) {
//                    if (dbmodel.sdVideo.equals(model.sdVideo)) {
//                        model.viewed = dbmodel.viewed;
//                    }
//                }
//            }
//
//
//            if (1 == page) {
//                mVideos.clear();
//                mVideos.addAll(list);
//            } else {
////  mVideos.clear();
//                mVideos.addAll(list);
//            }
//
//            adapter.setList(mVideos);
//            adapter.notifyDataSetChanged();
//            adapter.changeMoreStatus(adapter.PULLUP_LOAD_MORE);
//            mSwipeRefreshLayout.setRefreshing(false);
//        }
//    };








    public boolean isAnyItemChecked() {
        boolean isAnySelected = false;
        for (int i = mVideos.size() - 1; i >= 0; i--) {
            SDVideoModel model = mVideos.get(i);
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
        for (int i = mVideos.size() - 1; i >= 0; i--) {
            SDVideoModel model = mVideos.get(i);
            if (model.checked) {
                isAnySelected = true;
                break;
            }
        }

        if (isAnySelected) {
            for (int i = mVideos.size() - 1; i >= 0; i--) {
                SDVideoModel model = mVideos.get(i);
                model.checked = false;
            }
            adapter.notifyDataSetChanged();
            return false;
        } else {
            for (int i = mVideos.size() - 1; i >= 0; i--) {
                SDVideoModel model = mVideos.get(i);
                model.checked = true;
            }
            adapter.notifyDataSetChanged();
            return true;
        }

    }



    public void deleteVideos() {


        boolean isAnySelected = false;
        for (int i = mVideos.size() - 1; i >= 0; i--) {
            SDVideoModel model = mVideos.get(i);
            if (model.checked) {
                isAnySelected = true;
                break;
            }
        }
        if (!isAnySelected) {
            MyToast.showToast(this.getContext(),getString(R.string.chooseImageForDelete));
//            SouthUtil.showToast(this.getContext(),getString(R.string.chooseVideoForDelete));
            return;
        }


        if (lod == null) {
            lod = new LoadingDialog(this.getContext());
        }
        lod.setMessage("");
        lod.dialogShow();
//        Log.e(tag, "deleteVideos");
//        String fileName = "";
        for (SDVideoModel model : mVideos) {
            if (model.checked) {
                deleteFileVideoList.add(model);
//                Log.e(tag, "checked:" + model.sdVideo);
//                fileName += model.sdVideo + ",";
            }
        }


        Observable.from(deleteFileVideoList)
                .map(new Func1<SDVideoModel, Boolean>() {
                    @Override
                    public Boolean call(SDVideoModel sdVideoModel) {
                        mVideos.remove(sdVideoModel);
                        File file = new File(sdVideoModel.sdVideo);
                        boolean delete = file.delete();

                        return delete;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
//                        urls.clear();
////                        names.clear();
//                        for (SDImageModel sdImageModel : mImages) {
//                            urls.add(sdImageModel.sdImage);
////                            names.add(sdImageModel.sdImage);
//                        }
                        if (lod != null) {
                            lod.dismiss();
                        }
                        if (mOnCheckChangeListner != null) {
                            mOnCheckChangeListner.OnCheckChange();
                        }

                        adapter.setList(mVideos);
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
//
//
//                        for (int i = mVideos.size() - 1; i >= 0; i--) {
//                            SDVideoModel model = mVideos.get(i);
//                            if (model.checked) {
//                                mVideos.remove(model);
//
//                            }
//                        }
//                        adapter.setList(mVideos);
//                        adapter.notifyDataSetChanged();
////                        mSwipeRefreshLayout.setRefreshing(false);
//                        if (mOnCheckChangeListner != null) {
//                            mOnCheckChangeListner.OnCheckChange();
//                        }
//                    }
//                });
//

    }

    /**
     * @param type 0 if no data ,data request 1:date request
     */
    public void getVideosInActivity(int type) {
        if (mVideos == null) {
            mVideos = new ArrayList<>();
        }
        if (mVideos.size() == 0 || type == 1) {
//            handler.postDelayed(runnable, 50);
        }

    }


    /**
     * 选择文件夹之后，被调用
     *
     * @param type 0 选中
//     * @param path
     */
//    public void chooseOrCancel(int type, String path) {
//        if (mDialog != null)
//            mDialog.dismiss();
//        if (0 == type) {
//            downloadImages(1, path);
//        }
//    }
//
//    private DirectoryChooserFragment mDialog = null;

    @Override
    public void OnTextClicked(View view, String videoPath, int type) {
//        SDVideoModel sdVideoModel = mVideos.get(position);
        if (type == 0||type==2) {
            return;
        }
        Intent intent = new Intent(this.getActivity(), VideoPlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("videoPath", videoPath);
        intent.putExtras(bundle);
        this.getActivity().startActivityForResult(intent, 2);
    }

    @Override
    public void OnCheckClicked(int type) {
        if (mOnCheckChangeListner != null) {
            mOnCheckChangeListner.OnCheckChange();
        }
    }

    public interface OnCheckChangeListner {

        void OnCheckChange();//如果有点击，则通知
    }

    public void setOnCheckChangeListner(SDImageFragment.OnCheckChangeListner onCheckChangeListner) {
        mOnCheckChangeListner = onCheckChangeListner;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Const.videoType = 0;
    }
}
