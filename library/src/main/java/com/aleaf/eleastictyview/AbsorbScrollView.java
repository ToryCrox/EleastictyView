/*Transsion Top Secret*/
/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.aleaf.eleastictyview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

/**
 * function: 为了适配弹性控件
 * description: 1. 记录手拖动状态mIsBeingDragged，手拖动到边缘时候需要
 *              2. 提供松手后惯性滑动到边缘时的弹性
 * Author:
 */
public class AbsorbScrollView extends ScrollView {

    private static final String TAG = "AbsorbScrollView";

    private static final int INVALID_POINTER = -1;
    private int mTouchSlop;
    private int mLastMotionY;
    private int mActivePointerId;
    protected boolean mIsBeingDragged = false;

    private boolean mIsBeingScrollTo = false;

    public AbsorbScrollView(Context context) {
        super(context);
    }

    public AbsorbScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsorbScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int actionMask = MotionEventCompat.getActionMasked(ev);
        switch (actionMask) {
            case MotionEvent.ACTION_MOVE: {
                /*
                * Locally do absolute value. mLastMotionY is set to the y value
                * of the down event.
                */
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    break;
                }

                final int pointerIndex = ev.findPointerIndex(activePointerId);
                if (pointerIndex == -1) {
                    Log.e(TAG, "Invalid pointerId=" + activePointerId
                            + " in onInterceptTouchEvent");
                    break;
                }

                final int y = (int) ev.getY(pointerIndex);
                final int yDiff = Math.abs(y - mLastMotionY);
                if (yDiff > mTouchSlop) {
                    mIsBeingDragged = true;
                    mLastMotionY = y;
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                final int y = (int) ev.getY();

                /*
                 * Remember location of down touch.
                 * ACTION_DOWN always refers to pointer index 0.
                 */
                mLastMotionY = y;
                mActivePointerId = ev.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                /* Release the drag */
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                secondaryPointerUp(ev);
                break;
        }

        /*
        * The only time we want to intercept motion events is if we are in the
        * drag mode.
        */
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int actionMasked = MotionEventCompat.getActionMasked(ev);
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                // Remember where the motion event started
                mLastMotionY = (int) ev.getY();
                mActivePointerId = ev.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                if (activePointerIndex == -1) {
                    Log.e(TAG, "Invalid pointerId=" + mActivePointerId + " in onTouchEvent");
                    break;
                }

                final int y = (int) ev.getY(activePointerIndex);
                int deltaY = mLastMotionY - y;
                if (!mIsBeingDragged && Math.abs(deltaY) > mTouchSlop) {
                    mIsBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    mActivePointerId = INVALID_POINTER;
                    mIsBeingDragged = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    mActivePointerId = INVALID_POINTER;
                    mIsBeingDragged = false;
                }
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = ev.getActionIndex();
                mLastMotionY = (int) ev.getY(index);
                mActivePointerId = ev.getPointerId(index);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_UP:
                secondaryPointerUp(ev);
                mLastMotionY = (int) ev.getY(ev.findPointerIndex(mActivePointerId));
                break;
        }

        return super.onTouchEvent(ev);
    }

    private void secondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & MotionEventCompat.ACTION_POINTER_INDEX_MASK)
                >> MotionEventCompat.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionY = (int) ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    int getScrollRange() {
        int scrollRange = 0;
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            scrollRange = Math.max(0,
                    child.getHeight() - (getHeight() - getPaddingBottom() - getPaddingTop()));
        }
        return scrollRange;
    }

    @Override
    public void scrollTo(int x, int y) {
        mIsBeingScrollTo = true;
        super.scrollTo(x, y);
        mIsBeingScrollTo = false;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        mIsBeingScrollTo = true;
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        mIsBeingScrollTo = false;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mIsBeingDragged || mIsBeingScrollTo){
            return;
        }
        int oldY = oldt;
        int y = t;
        int range = getScrollRange();
        if (y <= 0 && oldY > 0) {
            absorbGlows(0, -10000);
        }else if(y >= range && oldY < range){
            absorbGlows(0, 10000);
        }
    }

    /**
     * 松手后自动滑动到边缘时
     * 执行两次
     * add by tory
     * @param velocityX
     */
    protected void absorbGlows(int velocityX, int velocityY) {
    }
}
