/*Transsion Top Secret*/
package com.aleaf.eleastictyview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by tory on 2017/6/15.
 */

public class ElasticityScrollView extends AbsorbScrollView implements ElasticityScrollable {

    protected ElasticityScrollViewHelper mElasticityViewHelper;

    public ElasticityScrollView(Context context) {
        this(context, null);
    }

    public ElasticityScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ElasticityScrollView(Context context, AttributeSet attrs, int defStyle) {
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
        if (mElasticityViewHelper.enableSpringEffectWhenDrag()) {
            mElasticityViewHelper.onTouchEvent(e);
            return true;
        }
        return super.onTouchEvent(e) ;
    }

    @Override
    public void draw(Canvas canvas) {
        mElasticityViewHelper.draw(canvas);
    }


    public void absorbGlows(int velocityX, int velocityY){
        super.absorbGlows(velocityX, velocityY);
        mElasticityViewHelper.absorbGlows(velocityX, velocityY);
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mElasticityViewHelper.onDetachedFromWindow();
    }
}
