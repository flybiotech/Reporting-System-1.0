package com.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Manager.DataManager;
import com.activity.LiveVidActivity;
import com.activity.R;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.model.DevModel;
import com.shizhenbao.util.Const;
import com.util.SouthUtil;

import java.util.List;

//BaseSwipeAdapter : 是一个开源库
public class ListDevAdapter extends BaseSwipeAdapter {
    public  final  static  String tag =  "ListDevAdapter";
    private LayoutInflater mInflater;
    public List<DevModel> devList = null;
    public Context mContext;
//    public FragIntentListDev fragIntentListDev=null;
    DataManager manager = DataManager.getInstance();
    public ListDevAdapter(Context con) {
        mContext = con;
        mInflater = LayoutInflater.from(mContext);
    }
    public void setDevArray( List<DevModel> array) {
        devList = array;
    }
    @Override
    public int getCount() {
        if (devList == null){
            return 0;
        }
        else{
            return devList.size();
        }

    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_dev, null);
        final int p = position;

        return v;
    }

    @Override
    public void fillValues(final int position, View convertView) {
        TextView dev_name = (TextView) convertView.findViewById(R.id.dev_name);
        TextView dev_info = (TextView) convertView.findViewById(R.id.dev_info);
        ImageView dev_image = (ImageView) convertView.findViewById(R.id.dev_image);
        DevModel model = devList.get(position);
        dev_name.setText(model.name);

        if (model.online == DevModel.EnumOnlineState.Online){
            dev_info.setText(Html.fromHtml(model.sn+"[<font color=blue>Lan</font>]"+model.ip));
//            fragIntentListDev.intentToListDiActivity();
                if (Const.isIntent&&Const.isIntentFrag) {
                    Intent intent = new Intent(mContext, LiveVidActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("index",0);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }

        }
        else if (model.online == DevModel.EnumOnlineState.Offline){
            dev_info.setText(Html.fromHtml(model.sn+"[<font color=red>离线</font>]"+model.ip));

        }
        else{
            dev_info.setText(Html.fromHtml(model.sn+"[<font color=#4876FF>正在获取状态</font>]"+model.ip));
        }

        convertView.findViewById(R.id.trash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mButtonClickListner != null){
                    mButtonClickListner.OnDeleteClicked(view, position);
                }
                closeItem(position);
            }
        });
        convertView.findViewById(R.id.hand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mButtonClickListner != null)
                    mButtonClickListner.OnChangeNameClicked(view, position);
                closeItem(position);
            }
        });



        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(SouthUtil.convertDpToPixel(50,mContext),SouthUtil.convertDpToPixel(50,mContext));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        layoutParams.setMargins(SouthUtil.convertDpToPixel(10,mContext),SouthUtil.convertDpToPixel(10,mContext),0,SouthUtil.convertDpToPixel(10,mContext));
        //layoutParams.addRule(RelativeLayout., RelativeLayout.TRUE);
        dev_image.setLayoutParams(layoutParams);
        dev_image.setImageResource(R.drawable.logo);
//        }
    }


    public void setOnButtonClickListner(OnButtonClickListner buttonClickListner) {
        this.mButtonClickListner = buttonClickListner;
    }


    public interface OnButtonClickListner {
        void OnDeleteClicked(View view, int position);
        void OnChangeNameClicked(View view, int position);
    }
    OnButtonClickListner mButtonClickListner = null;


}
