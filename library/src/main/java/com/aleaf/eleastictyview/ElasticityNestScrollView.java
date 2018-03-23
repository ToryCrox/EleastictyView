package com.aleaf.eleastictyview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * description: 弹性ScrollView, 但实际上不继承ScrollView，在惯性滑动到边界时弹性幅度会根据实际速度
 */

public class ElasticityNestScrollView extends AbsorbNestedScrollView implements ElasticityScrollable {

    protected ElasticityScrollViewHelper mElasticityViewHelper;

    public ElasticityNestScrollView(Context context) {
        this(context, null);
    }

    public ElasticityNestScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ElasticityNestScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mElasticityViewHelper = new ElasticityScrollViewHelper(this);
        mElasticityViewHelper.init(attrs, defStyle);
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
        if (mElasticityViewHelper.enableSpringEffectWhenDrag()
                && mElasticityViewHelper.onTouchEvent(e)) {
            return true;
        }
        return super.onTouchEvent(e) ;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mElasticityViewHelper.onDetachedFromWindow();
    }

    @Override
    public void absorbGlows(int velocityX, int velocityY){
        super.absorbGlows(velocityX, velocityY);
        mElasticityViewHelper.absorbGlows(velocityX, velocityY);
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(Canvas canvas) {
        mElasticityViewHelper.draw(canvas);
    }

    @Override
    public boolean isBeingDragged() {
        return mIsBeingDragged;
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
