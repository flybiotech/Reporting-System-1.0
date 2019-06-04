package com.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebIconDatabase;
import android.widget.ImageView;

import com.activity.R;

import java.util.WeakHashMap;

/**
 * Created by dell on 2018/4/4.
 */

public class ImageViewRotation extends android.support.v7.widget.AppCompatImageView {




    public ImageViewRotation(Context context) {
        this(context,null);
    }

    public ImageViewRotation(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ImageViewRotation(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);



    }

    @Override
    protected void onDraw(Canvas canvas) {
//        int top = getTop();
//        int left = getLeft();
//        int right = getRight();
//        int bottom = getBottom();
//        float centralX = (right - left) / 2.0f;
//        float centralY = (bottom - top) / 2.0f;
        canvas.save();//保存旋转前画布状态
        canvas.rotate(0, getMeasuredWidth()/2, getMeasuredHeight()/2);
        super.onDraw(canvas);
        canvas.restore();//取去出旋转前保存的状态
    }
}
