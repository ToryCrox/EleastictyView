/*Transsion Top Secret*/
package com.aleaf.eleastictyview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.AbsorbRecyclerView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by tory on 2017/6/14.
 */

public class ElasticityRecyclerView extends AbsorbRecyclerView implements ElasticityScrollable {

    protected ElasticityRecyclerViewHelper mElasticityViewHelper;

    public ElasticityRecyclerView(Context context) {
        this(context, null);
    }

    public ElasticityRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ElasticityRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mElasticityViewHelper = new ElasticityRecyclerViewHelper(this);
        mElasticityViewHelper.init(attrs, defStyle);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (mElasticityViewHelper != null && layout != null) {
            mElasticityViewHelper.setOrientation(layout.canScrollHorizontally() ?
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
        if (mElasticityViewHelper.enableSpringEffectWhenDrag()
                && mElasticityViewHelper.onTouchEvent(e)) {
            return true;
        }
        return super.onTouchEvent(e) ;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(Canvas canvas) {
        mElasticityViewHelper.draw(canvas);
    }

    @Override
    public void absorbGlows(int velocityX, int velocityY){
        mElasticityViewHelper.absorbGlows(velocityX, velocityY);
    }


    @Override
    public boolean isBeingDragged() {
        return getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING;
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
