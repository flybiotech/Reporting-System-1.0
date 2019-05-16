package com.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by dell on 2018/3/9.
 */

public class RotateTextView extends android.support.v7.widget.AppCompatTextView {


    public RotateTextView(Context context) {
        super(context);
    }

    public RotateTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

       //倾斜90度
        canvas.rotate(-90, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        super.onDraw(canvas);
    }


}
