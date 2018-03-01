/*Transsion Top Secret*/
package com.aleaf.eleastictyview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

/**
 * Created by tory on 2017/6/13.
 */

public class ElasticityViewHelper {

    private static final String TAG = "ElasticityViewHelper";
    private static final boolean ENABLE_LOG_V = true;

    protected static final int HORIZONTAL = ElasticityScrollable.HORIZONTAL;
    protected static final int VERTICAL = ElasticityScrollable.VERTICAL;

    protected static final float DEFAULT_MAX_SCALE = 1.10f;

    protected static final int STATE_NORMAL = 0;
    protected static final int STATE_DRAG_TOP_OR_LEFT = 1;
    protected static final int STATE_DRAG_BOTTOM_OR_RIGHT = 2;
    protected static final int STATE_SPRING_BACK = 3;
    protected static final int STATE_FLING = 4;
    protected int mState = STATE_NORMAL;

    protected static final int DEF_RELEASE_BACK_ANIM_DURATION = 250; // ms
    protected static final int DEF_FLING_BACK_ANIM_DURATION = 250;
    protected int mReleaseBackAnimDuration;
    protected int mFlingBackAnimDuration;

    protected static final int INVALID_POINTER = -1;

    protected int mTouchSlop;
    protected int mMaximumVelocity;
    protected int mOrientation; // horizontal or vertical
    protected float mLastMotionPos; // x-coordinate or y-coordinate of last event, base on mOrientation
    protected int mMaxOverScrollOffset;
    protected float mMaxOverScrollScale;
    protected float mFrom;
    protected float mOffset;
    protected int mActivePointerId = INVALID_POINTER;

    protected boolean mEnableSpringEffectWhenDrag;
    protected boolean mEnableSpringEffectWhenFling;

    private Interpolator mSpringScaleInterpolator;
    private Animation springAnimation;
    private Interpolator releaseBackAnimInterpolator;
    private Interpolator flingBackAnimInterpolator;

    protected ViewGroup mView;
    protected ElasticityScrollable mSpringView;


    public ElasticityViewHelper(@NonNull ViewGroup view) {
        mView = view;
        if(!(view instanceof ElasticityScrollable)){
            throw new RuntimeException("ElasticityViewHelper init view must implement ElasticityScrollable");
        }
        mSpringView = (ElasticityScrollable) view;
    }

    protected void init(AttributeSet attrs, int defStyleAttr) {
        mView.setWillNotDraw(false);
        mView.setClipToPadding(false);

        Context context = mView.getContext();
        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        mMaximumVelocity = vc.getScaledMaximumFlingVelocity();


        mMaxOverScrollOffset = dp2px(context, 300);
        mMaxOverScrollScale = DEFAULT_MAX_SCALE;
        mOrientation = VERTICAL;
        mReleaseBackAnimDuration = DEF_RELEASE_BACK_ANIM_DURATION;
        mFlingBackAnimDuration =  DEF_FLING_BACK_ANIM_DURATION;
        mEnableSpringEffectWhenDrag = true;
        mEnableSpringEffectWhenFling = true;

        initAnimation();
        if (ENABLE_LOG_V){
            logv("init mSlop=" + mTouchSlop);
        }
    }

    /**
     * 设置回弹方向
     * @param orientation
     */
    public void setOrientation(@ElasticityScrollable.Orientation int orientation){
        mOrientation = orientation;
    }

    /**
     * 设置最大的放大倍数
     * @param maxOverScrollScale
     */
    public void setMaxOverScrollScale(@FloatRange(from=1.0, to=1.5f)
                                              float maxOverScrollScale){
        mMaxOverScrollScale = maxOverScrollScale;
    }

    /**
     * 设置是否可以回弹, 默认为true
     * @param enable
     */
    public void setEnableSpringEffectWhenDrag(boolean enable) {
        mEnableSpringEffectWhenDrag = enable;
    }

    /**
     * 设置自动滑到边界时候是否可以回弹, 默认为true
     * @param enable
     */
    public void setEnableSpringEffectWhenFling(boolean enable) {
        mEnableSpringEffectWhenFling = enable;
    }

    /**
     * 设置边界时候的回弹动画时间, 默认为{@link #DEF_RELEASE_BACK_ANIM_DURATION}
     * @param duration
     */
    public void setReleaseBackAnimDuration(int duration) {
        mReleaseBackAnimDuration = duration;
    }

    /**
     * 设置自动滑动到边界时候的回弹动画时间, 默认为{@link #DEF_FLING_BACK_ANIM_DURATION}
     * @param duration
     */
    public void setFlingBackAnimDuration(int duration) {
        mFlingBackAnimDuration = duration;
    }



    protected void setState(int newState) {
        if (mState != newState) {
            mState = newState;
        }
    }

    protected boolean isDragged() {
        return mState == STATE_DRAG_TOP_OR_LEFT || mState == STATE_DRAG_BOTTOM_OR_RIGHT;
    }

    protected boolean isDraggedTopOrLeft() {
        return mState == STATE_DRAG_TOP_OR_LEFT;
    }

    protected boolean isDraggedBottomOrRight() {
        return mState == STATE_DRAG_BOTTOM_OR_RIGHT;
    }


    public boolean enableSpringEffectWhenDrag(){
        return mEnableSpringEffectWhenDrag;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if(ENABLE_LOG_V){
            logv("onInterceptTouchEvent action = "+action);
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionPos = mOrientation == VERTICAL ? ev.getY() : ev.getX();
                mActivePointerId = ev.getPointerId(0);
                // If STATE_SPRING_BACK, we intercept the event and stop the animation.
                // If STATE_FLING, we do not intercept and allow the animation to finish.
                if (mState == STATE_SPRING_BACK) {
                    if (mOffset != 0) {
                        mView.clearAnimation();
                        setState(mOffset > 0 ? STATE_DRAG_TOP_OR_LEFT : STATE_DRAG_BOTTOM_OR_RIGHT);
                    } else {
                        setState(STATE_NORMAL);
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId == INVALID_POINTER) {
                    break;
                }
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    break;
                }
                final float pos = mOrientation == VERTICAL ? ev.getY(pointerIndex) : ev.getX(pointerIndex);
                final float posDiff = pos - mLastMotionPos;
                mLastMotionPos = pos;
                if (!isDragged()) {
                    boolean canScrollUpOrLeft = canScrollUp() || canScrollLeft() ;
                    boolean canScrollDownOrRight =  canScrollDown() || canScrollRight();

                    if(ENABLE_LOG_V){
                        logv("onInterceptTouchEvent canScrollUpOrLeft = "+canScrollUpOrLeft
                                + ", canScrollDownOrRight = "+canScrollDownOrRight
                                + ", posDiff = "+posDiff
                                + ", mLastMotionPos = " + mLastMotionPos
                                + ", "
                                + ", scrollY="+mView.getScrollY());
                    }

                    if (canScrollUpOrLeft && canScrollDownOrRight) {
                        break;
                    }

                    if ((Math.abs(posDiff) >= mTouchSlop) ||
                            interceptScrollDragging(canScrollUpOrLeft , canScrollDownOrRight, posDiff)) {
                        boolean isOverScroll = false;
                        if (!canScrollUpOrLeft && posDiff > 0) {
                            setState(STATE_DRAG_TOP_OR_LEFT);
                            isOverScroll = true;
                        } else if (!canScrollDownOrRight && posDiff < 0) {
                            setState(STATE_DRAG_BOTTOM_OR_RIGHT);
                            isOverScroll = true;
                        }
                        if (isOverScroll) {
                            // Prevent touch effect on item
                            MotionEvent fakeCancelEvent = MotionEvent.obtain(ev);
                            fakeCancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                            mSpringView.superOnTouchEvent(fakeCancelEvent);
                            fakeCancelEvent.recycle();
                            mSpringView.superAwakenScrollBars();

                            final ViewParent parent = mView.getParent();
                            if (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(true);
                            }
                        }
                    }
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_UP: {
                onSecondaryPointerUp(ev);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER;
                break;
            }
        }
        if(ENABLE_LOG_V){
            logv("onInterceptTouchEvent isDragged = "+isDragged());
        }
        return isDragged();

    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if(ENABLE_LOG_V){
            logv("onTouchEvent action = "+action);
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionPos = mOrientation == VERTICAL ? ev.getY() : ev.getX();
                mActivePointerId = ev.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId == INVALID_POINTER) {
                    break;
                }
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    break;
                }
                final float pos = mOrientation == VERTICAL ? ev.getY(pointerIndex) : ev.getX(pointerIndex);
                final float posDiff = pos - mLastMotionPos;
                mLastMotionPos = pos;
                if (!isDragged()) {
                    boolean canScrollUpOrLeft = canScrollUp() || canScrollLeft() ;
                    boolean canScrollDownOrRight =  canScrollDown() || canScrollRight();

                    if(ENABLE_LOG_V){
                        logv("onTouchEvent canScrollUpOrLeft = "+canScrollUpOrLeft
                                + ", canScrollDownOrRight = "+canScrollDownOrRight
                                + ", posDiff = "+posDiff
                                + ", mLastMotionPos = " + mLastMotionPos);
                    }

                    if (canScrollUpOrLeft && canScrollDownOrRight) {
                        break;
                    }

                    if ((Math.abs(posDiff) >= mTouchSlop)||
                            interceptScrollDragging(canScrollUpOrLeft , canScrollDownOrRight, posDiff)) {
                        boolean isOverScroll = false;
                        if (!canScrollUpOrLeft && posDiff > 0) {
                            setState(STATE_DRAG_TOP_OR_LEFT);
                            isOverScroll = true;
                        } else if (!canScrollDownOrRight && posDiff < 0) {
                            setState(STATE_DRAG_BOTTOM_OR_RIGHT);
                            isOverScroll = true;
                        }
                        if (isOverScroll) {
                            // Prevent touch effect on item
                            MotionEvent fakeCancelEvent = MotionEvent.obtain(ev);
                            fakeCancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                            mSpringView.superOnTouchEvent(fakeCancelEvent);
                            fakeCancelEvent.recycle();
                            mSpringView.superAwakenScrollBars();

                            final ViewParent parent = mView.getParent();
                            if (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(true);
                            }
                        }
                    }
                }
                if (isDragged()) {
                    if(Math.abs(mOffset + posDiff) >= mMaxOverScrollOffset){
                        mOffset = mMaxOverScrollOffset * (mOffset < 0 ? -1 : 1);
                    }else{
                        mOffset += posDiff;
                    }

                    // correct mOffset
                    if ((isDraggedTopOrLeft() && mOffset <= 0) || (isDraggedBottomOrRight() && mOffset >=0)) {
                        setState(STATE_NORMAL);
                        mOffset = 0;
                        // return to touch item
                        MotionEvent fakeDownEvent = MotionEvent.obtain(ev);
                        fakeDownEvent.setAction(MotionEvent.ACTION_DOWN);
                        mSpringView.superOnTouchEvent(fakeDownEvent);
                        fakeDownEvent.recycle();
                    }
                    invalidate();
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(ev);
                mLastMotionPos = mOrientation == VERTICAL ? ev.getY(index) : ev.getX(index);
                mActivePointerId = ev.getPointerId(index);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP: {
                onSecondaryPointerUp(ev);
                final int index = ev.findPointerIndex(mActivePointerId);
                if (index != -1) {
                    mLastMotionPos = mOrientation == VERTICAL ? ev.getY(index) : ev.getX(index);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mOffset != 0) {
                    if(ENABLE_LOG_V){
                        logv("release mOffset="+mOffset);
                    }
                    // Spring back
                    mFrom = mOffset;
                    startReleaseAnimation();
                    setState(STATE_SPRING_BACK);
                }
                mActivePointerId = INVALID_POINTER;
            }
        }
        if(ENABLE_LOG_V){
            logv("onTouchEvent isDragged = "+isDragged() + ", mOffset = "+mOffset);
        }
        return isDragged();
    }

    /**
     * 手滑动过程中碰到边界，此时是否应该拦截
     * @param canScrollUpOrLeft
     * @param canScrollDownOrRight
     * @param posDiff
     * @return
     */
    protected boolean interceptScrollDragging(boolean canScrollUpOrLeft,
                                              boolean canScrollDownOrRight,
                                              float posDiff) {
        if(mSpringView.isBeingDragged()){
            if(!canScrollUpOrLeft && posDiff > 0){
                return true;
            }
            if(!canScrollDownOrRight &&  posDiff < 0){
                return true;
            }
        }
        return false;
    }

    /**
     * 手释放后自动滑动到边界时回弹
     * @param velocityX 横向速度: 正数向左滑动，负数向右滑动
     * @param velocityY 纵向滑动: 正数向上滑动，负数向下滑动
     */
    public void absorbGlows(int velocityX, int velocityY) {
        logv("absorbGlows velocityX = " + velocityX + ", velocityY = " + velocityY);
        if (mEnableSpringEffectWhenFling && mState != STATE_FLING) {
            final int v = mOrientation == VERTICAL ? velocityY : velocityX;
            if(mMaximumVelocity > 0){
                mFrom = - mMaxOverScrollOffset * v / mMaximumVelocity;
            }else{
                mFrom = -v * (1f/40);
                if(Math.abs(mFrom) > mMaxOverScrollOffset){
                    mFrom = mMaxOverScrollOffset * (mFrom < 0 ? -1 : 1);
                }
            }
            if(ENABLE_LOG_V){
                logv("absorbGlows v = " + v + ", mMaxOverScrollOffset = "+ mMaxOverScrollOffset
                        + " mMaximumVelocity = "+ mMaximumVelocity +", mFrom = " + mFrom);
            }
            startFlingAnimation();
            setState(STATE_FLING);
        }
    }

    /**
     * view移除时候取消动画
     */
    public void onDetachedFromWindow() {
        if (mState != STATE_NORMAL) {
            if(ENABLE_LOG_V){
                logv("onDetachedFromWindow mOffset = " + mOffset );
            }
            mOffset = 0;
            mView.clearAnimation();
            setState(STATE_NORMAL);
            invalidate();
        }
    }

    protected boolean canScrollUp(){
        return ViewCompat.canScrollVertically(mView, -1);
    }

    protected boolean canScrollDown(){
        return ViewCompat.canScrollVertically(mView, 1);
    }

    protected boolean canScrollLeft(){
        return ViewCompat.canScrollHorizontally(mView, -1);
    }

    protected boolean canScrollRight(){
        return ViewCompat.canScrollHorizontally(mView, 1);
    }

    private void initAnimation() {
        springAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                mOffset = mFrom * interpolatedTime;
                if (hasEnded()) {
                    mOffset = 0;
                    setState(STATE_NORMAL);
                }
                if(ENABLE_LOG_V){
                    logv("springAnimation mOffset="+mOffset);
                }
                invalidate();
            }
        };

        releaseBackAnimInterpolator = new Interpolator() {
            @Override
            public float getInterpolation(float v) {
                return (float) Math.cos(Math.PI * v / 2);
            }
        };

        flingBackAnimInterpolator = new Interpolator() {
            @Override
            public float getInterpolation(float v) {
                return (float) Math.sin(Math.PI * v);
            }
        };

        mSpringScaleInterpolator = new DecelerateInterpolator();
    }

    private void invalidate() {
        mView.invalidate();
    }


    private void startReleaseAnimation() {
        springAnimation.setDuration(mReleaseBackAnimDuration);
        springAnimation.setInterpolator(releaseBackAnimInterpolator);
        mView.startAnimation(springAnimation);
    }

    protected void startFlingAnimation() {
        springAnimation.setDuration(mFlingBackAnimDuration);
        springAnimation.setInterpolator(flingBackAnimInterpolator);
        mView.startAnimation(springAnimation);
    }


    public void logd(String msg){
        Log.d(TAG, msg);
    }

    public void logv(String msg) {
        Log.v(TAG, msg);
    }

    public void loge(String msg) {
            Log.e(TAG, msg);
    }

    public int getHeight(){
        return mView.getHeight();
    }

    public int getWidth(){
        return mView.getWidth();
    }

    public void draw(Canvas canvas) {
        if (mState == STATE_NORMAL) {
            mSpringView.superDraw(canvas);
        } else {
            final int sc = canvas.save();

            // scale the canvas
            final float factor = Math.abs(mOffset) / (float) mMaxOverScrollOffset;
            final float fixFactor = /*isDragged() ? mSpringScaleInterpolator.getInterpolation(factor) :*/
                                    factor;
            final float scale = 1 +  fixFactor * (mMaxOverScrollScale - 1);
            if (mOrientation == VERTICAL) {
                final int viewHeight = getHeight();
                canvas.scale(1, scale, 0, mOffset >= 0 ? 0 : (viewHeight + mView.getScrollY()));
            } else {
                final int viewWidth = getWidth();
                canvas.scale(scale, 1, mOffset >= 0 ? 0 : (viewWidth + mView.getScrollX()), 0);
            }

            mSpringView.superDraw(canvas);
            canvas.restoreToCount(sc);
        }
    }

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
