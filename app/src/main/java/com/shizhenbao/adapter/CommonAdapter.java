package com.shizhenbao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by dell on 2017/10/18.
 * 通用的adapter
 * adapter一般需要保持一个List对象，存储一个Bean的集合，不同的ListView，Bean肯定是不同的，这个CommonAdapter肯定需要支持泛型，内部维持一个List<T>，就解决我们的问题了；
 于是我们初步打造我们的CommonAdapter
 */

public abstract class CommonAdapter<T> extends BaseAdapter {
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas;
    protected final int mItemLayoutId;

    public CommonAdapter(Context context, List<T> mDatas, int mItemLayoutId) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mDatas = mDatas;
        this.mItemLayoutId = mItemLayoutId;
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = getViewHolder(position, convertView, parent);
        convert(viewHolder,getItem(position));
        return viewHolder.getConvertView();
    }

    /**
     * 对外公布了一个convert方法，并且还把viewHolder和本Item对于的Bean对象给传出去，现在convert方法里面需要干嘛呢？
     * 通过ViewHolder把View找到，通过Item设置值；
     *
     * @param helper
     * @param item
     */
    public abstract void convert(ViewHolder helper, T item);

    private ViewHolder getViewHolder(int postion, View convertView, ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId, postion);
    }

}
