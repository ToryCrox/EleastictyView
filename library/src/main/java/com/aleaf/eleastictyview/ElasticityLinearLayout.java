/*Transsion Top Secret*/
package com.aleaf.eleastictyview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by tory on 2017/6/16.
 */
public class ElasticityLinearLayout extends LinearLayout implements ElasticityScrollable {

    protected ElasticityViewHelper mElasticityViewHelper;

    public ElasticityLinearLayout(Context context) {
        this(context, null);
    }

    public ElasticityLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ElasticityLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mElasticityViewHelper = new ElasticityViewHelper(this);
        mElasticityViewHelper.init(attrs, defStyleAttr);
        setOrientation(getOrientation());
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
        if(mElasticityViewHelper != null){ //此方法会在mSpringViewHelper初始化之前调用
            mElasticityViewHelper.logv("SpringLinearLayout setOrientation="+orientation);
            mElasticityViewHelper.setOrientation(orientation == LinearLayout.HORIZONTAL ?
                    ElasticityScrollable.HORIZONTAL : ElasticityScrollable.VERTICAL);
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
