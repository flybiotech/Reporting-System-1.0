package com.shizhenbao.UI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.activity.R;


/**
 * 卫星式菜单View
 *
 * @author caizhiming
 */
public class XCArcMenuView extends ViewGroup implements View.OnClickListener {


    private Status mStatus = Status.CLOSE;
    //主菜的单按钮
    private View mCButton;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private String str[] = null;

    /**
     * 菜单的状态枚举类
     *
     * @author caizhiming
     */
    public enum Status {
        OPEN, CLOSE
    }

    /**
     * 点击子菜单项的回调接口
     *
     * @author caizhiming
     */
    public interface OnMenuItemClickListener {
        void onClickMenu(View view, int pos);

        //改变控件的背景颜色
        void onChangeBackground(boolean isChange);
    }

    public void setOnMenuItemClickListener(
            OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }


    public XCArcMenuView(Context context) {
        this(context, null);

    }

    public XCArcMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public XCArcMenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        str = new String[]{context.getString(R.string.image_artword), context.getString(R.string.image_acetic_acid_white), context.getString(R.string.image_Lipiodol)};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (changed) {
            Log.i("TAG_12", "onLayout: ");
            layoutCButton();
            layoutMenuItems();
        }
    }


    /**
     * 布局主菜单项
     */
    private void layoutCButton() {

        mCButton = getChildAt(0);
        mCButton.setOnClickListener(this);
        int l = 0;
        int t = 0;
        int width = mCButton.getMeasuredWidth();
        int height = mCButton.getMeasuredHeight();
        mCButton.layout(l, t, l + width, t + height);
    }

    /**
     * 布局菜单项
     */
    private void layoutMenuItems() {
        // TODO Auto-generated method stub
        int count = getChildCount();
        for (int i = 0; i < count - 1; i++) {
            View child = getChildAt(i + 1);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            int l = 10 * (i + 1) + mCButton.getRight() + width * i;
            int t = (mCButton.getMeasuredHeight() - height) / 2;
            ;

            child.layout(l, t, l + width, t + height);
            child.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        mCButton = findViewById(R.id.id_button);
//        rotateCButton(v,0,0,400);
        toggleMenu(400);
    }

    /**
     * 切换菜单
     */
    public void toggleMenu(int duration) {
        Log.i("TAG_12", "toggleMenu: duration = " + duration + "  ,mStatus = " + mStatus);
        // 为menuItem添加平移动画和旋转动画
        int count = getChildCount();

        for (int i = 0; i < count - 1; i++) {
            final TextView childView = (TextView) getChildAt(i + 1);
            childView.setVisibility(View.VISIBLE);
            childView.setTextColor(Color.parseColor("#ffffffff"));
            int cl = 10 * (i + 1) + mCButton.getRight() + childView.getMeasuredWidth() * i;
            int ct = (mCButton.getMeasuredHeight() - childView.getMeasuredHeight()) / 2;



            AnimationSet animset = new AnimationSet(true);
            Animation tranAnim = null;
            Animation alphaAnim = null;

            // to open
            if (mStatus == Status.CLOSE) {
                tranAnim = new TranslateAnimation(-cl, 0, 0, 0);
                alphaAnim = new AlphaAnimation(0.0f, 1.0f);
                childView.setClickable(true);
                childView.setFocusable(true);
//                childView.setText(str[i]);


            } else
            // to close
            {
                tranAnim = new TranslateAnimation(0, -cl, 0, 0);
                alphaAnim = new AlphaAnimation(1.0f, 0.0f);
                childView.setClickable(false);
                childView.setFocusable(false);
                childView.setTextColor(Color.parseColor("#00000000"));

//                mOnMenuItemClickListener.onChangeBackground(false);
            }
            tranAnim.setFillAfter(true);
            tranAnim.setDuration(duration);
            tranAnim.setStartOffset((i * 100) / count);

            alphaAnim.setFillAfter(true);
            alphaAnim.setDuration(duration);
            alphaAnim.setStartOffset((i * 100) / count);


            tranAnim.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mStatus == Status.CLOSE) {

                        childView.setVisibility(View.GONE);
                    }
                }
            });
            /**
             * fromDegrees 从哪个角度 toDegrees 到哪个角度 pivotXType pivotXValue pivotYType
             * 注意这个地方的话 水平线为x轴 顺时针旋转从0度开始到360度 位置可以是180到360 也可是- 180 到 0
             *
             *  * pivotXValue
             * pivotYValue 总的说就是相对于x y坐标的位置 只要记住一点就行了 控件的左上角的坐标为（0,0） 右下角为（1,1）
             *
             * 参数说明：
             * float fromDegrees：旋转的开始角度。
             * float toDegrees：旋转的结束角度。
             * int pivotXType：X轴的伸缩模式，可以取值为ABSOLUTE、RELATIVE_TO_SELF、RELATIVE_TO_PARENT。
             * float pivotXValue：X坐标的伸缩值。
             * int pivotYType：Y轴的伸缩模式，可以取值为ABSOLUTE、RELATIVE_TO_SELF、RELATIVE_TO_PARENT。
             * float pivotYValue：Y坐标的伸缩值。
             *
             */
            // 旋转动画
//            RotateAnimation rotateAnim = new RotateAnimation(0, 360,
//                    Animation.RELATIVE_TO_SELF, 0.5f,
//                    Animation.RELATIVE_TO_SELF, 0.5f);
//            rotateAnim.setDuration(duration);
//            rotateAnim.setFillAfter(true);

//            animset.addAnimation(rotateAnim);


            animset.addAnimation(alphaAnim);
            animset.addAnimation(tranAnim);
            childView.startAnimation(animset);


            final int pos = i + 1;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnMenuItemClickListener != null)

                        //监听事件
                        mOnMenuItemClickListener.onClickMenu(childView, pos);

                    menuItemAnim();
                    changeStatus();

                }
            });
        }


        // 切换菜单状态
        changeStatus();

    }

    /**
     * 选择主菜单按钮
     */
    private void rotateCButton(View v, float start, float end, int duration) {
        // TODO Auto-generated method stub
        RotateAnimation anim = new RotateAnimation(start, end,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        anim.setDuration(duration);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }

    /**
     * 添加menuItem的点击动画
     */
    private void menuItemAnim() {
        for (int i = 0; i < getChildCount() - 1; i++) {

            TextView childView = (TextView) getChildAt(i + 1);
            int cl = 10 * (i + 1) + mCButton.getRight() + childView.getMeasuredWidth() * i;
//                childView.startAnimation(scaleBigAnim(300));
            childView.startAnimation(transBackAnim(300, cl,childView));
            childView.setTextColor(Color.parseColor("#00000000"));
//
            childView.setClickable(false);
            childView.setFocusable(false);
//            childView.setVisibility(View.GONE);

        }

    }

    /**
     * 为当前点击的Item设置 移动位置
     *
     * @param duration
     * @return
     */
    private Animation transBackAnim(int duration, int cl,TextView childView) {
        TranslateAnimation alphaAnim = new TranslateAnimation(0, -cl, 0, 0);

        alphaAnim.setDuration(duration);
        alphaAnim.setFillAfter(true);

        return alphaAnim;

    }

    /**
     * 为当前点击的Item设置变大和透明度降低的动画
     */
    private Animation scaleBigAnim(int duration) {
        AnimationSet animationSet = new AnimationSet(true);
//
//        ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//                0.5f);
        AlphaAnimation alphaAnim = new AlphaAnimation(1f, 0.0f);

//        animationSet.addAnimation(scaleAnim);
//        alphaAnim.addAnimation(alphaAnim);

        alphaAnim.setDuration(duration);
        alphaAnim.setFillAfter(true);
        return alphaAnim;

    }


    /**
     * 切换菜单状态
     */
    private void changeStatus() {
        mStatus = (mStatus == Status.CLOSE ? Status.OPEN
                : Status.CLOSE);
        mOnMenuItemClickListener.onChangeBackground(mStatus == Status.CLOSE);
    }

    /**
     * 是否处于展开状态
     *
     * @return
     */
    public boolean isOpen() {
        return mStatus == Status.OPEN;
    }


    //关闭动画
    public void close() {
        if (isOpen()) {
            toggleMenu(400);
        }
    }

}
