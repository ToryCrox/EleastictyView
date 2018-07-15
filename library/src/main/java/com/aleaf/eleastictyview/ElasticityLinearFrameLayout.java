package com.aleaf.eleastictyview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * @author tory
 * @date 2018/3/3
 */
public class ElasticityLinearFrameLayout extends FrameLayout implements ElasticityScrollable {

    protected ElasticityViewHelper mElasticityViewHelper;

    public ElasticityLinearFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public ElasticityLinearFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ElasticityLinearFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mElasticityViewHelper = new ElasticityViewHelper(this);
        mElasticityViewHelper.init(attrs, defStyleAttr);
    }

    public void setOrientation(@Orientation int orientation) {
        if(mElasticityViewHelper != null){
            mElasticityViewHelper.logv("ElasticityLinearFrameLayout setOrientation="+orientation);
            mElasticityViewHelper.setOrientation(orientation);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (mElasticityViewHelper.enableSpringEffectWhenDrag()
                && mElasticityViewHelper.onInterceptTouchEvent(e)) {
            return true;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mElasticityViewHelper.enableSpringEffectWhenDrag()) {
            //在支持弹性的时候需要返回true，否则无法拦截到事件
            mElasticityViewHelper.onTouchEvent(e);
            return true;
        }
        return super.onTouchEvent(e) ;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mElasticityViewHelper.onSizeChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mElasticityViewHelper.onDetachedFromWindow();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(Canvas canvas) {
        mElasticityViewHelper.draw(canvas);
    }

    @Override
    public boolean isBeingDragged() {
        return false;
    }

    @Override
    public void superDraw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    public boolean superOnTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    @Override
    public void superAwakenScrollBars() {
        super.awakenScrollBars();
    }
}
