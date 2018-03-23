package com.aleaf.eleastictyview;

import android.graphics.Canvas;
import android.support.annotation.IntDef;
import android.view.MotionEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tory on 2017/6/14.
 */

public interface ElasticityScrollable {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({HORIZONTAL, VERTICAL})
    public @interface Orientation {
    }

    /**
     * 滑动过程碰到边界，需要实现此接口
     * 如: RecyclerView :getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING;
     * @return the scrollable view is dragged
     */
    public boolean isBeingDragged();

    /**
     * implement: call view: super.draw(canvas)
     * @param canvas
     */
    public void superDraw(Canvas canvas);

    /**
     * implement: call view: super.onTouchEvent(MotionEvent ev)
     * @param ev
     * @return
     */
    public boolean superOnTouchEvent(MotionEvent ev);

    /**
     * implement: call view: super.superAwakenScrollBars()
     * @param
     * @return
     */
    public void superAwakenScrollBars();
}
