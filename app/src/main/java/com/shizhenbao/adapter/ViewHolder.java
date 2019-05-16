package com.shizhenbao.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dell on 2017/10/18.
 * 创建通用的ViewHolder
 * 与传统的ViewHolder不同，我们使用了一个SparseArray<View>用于存储与之对于的convertView的所有的控件，当需要拿这些控件时，通过getView(id)进行获取；
 */

public class ViewHolder {

    private final SparseArray<View> mViews;
    private View mConvertView;
    private int mPosition;

    private ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mViews = new SparseArray<View>();
        this.mPosition = position;
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }


    /**
     * 获取ViewHolder 的对象
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        } else {
            return (ViewHolder) convertView.getTag();
        }

    }

    /**
     * 通过控件的id获取对应的控件，如果没有获取到。那么就放入mViews 集合中
     * <T extends View>是声明这是一个泛型方法，同时extends View限制了返回的T类型必须是View的子类
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    //为TextView 设置字符串
    public ViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 为ImageView 设置图片
     * @param viewId
     * @param drawableId
     * @return
     */
    public ViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    /**
     * 为ImageView 设置图片
     *
     * @return
     */
    public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    /**
     * 为ImageView 设置图片
     * param viewId
     * param url
     * @return
     */
//    public ViewHolder setImageByUrl(int viewId, String url) {
//        ImageLoader.getInstance(3, Type.LIFO).loadImage(url,
//                (ImageView) getView(viewId));
//        return this;
//    }

    public int getPosition() {
        return mPosition;
    }










}
