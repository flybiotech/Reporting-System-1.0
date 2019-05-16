package com.shizhenbao.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by fly on 2017/3/30.
 */

public class BrushView extends View {
    private Paint brush = new Paint();
    private Path path = new Path();
    public Button btnEraseAll;
    public ViewGroup.LayoutParams params;


    public BrushView(Context context) {
        super(context);
        brush.setAntiAlias(true);
        brush.setColor(Color.BLUE);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(10f);
        btnEraseAll = new Button(context);
        btnEraseAll.setText("重启");
        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnEraseAll.setLayoutParams(params);
        btnEraseAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                path.reset();
                postInvalidate();//重新刷新view视图
            }
        });

    }

//触控的方法

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://第一次触摸屏幕
                path.moveTo(pointX, pointY);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(pointX, pointY);
                break;
            default:
                break;
        }
        postInvalidate();
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, brush);
    }
}
