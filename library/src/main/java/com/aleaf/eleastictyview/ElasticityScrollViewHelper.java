/*Transsion Top Secret*/
package com.aleaf.eleastictyview;

import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by tao.xu2 on 2017/6/15.
 */

public class ElasticityScrollViewHelper extends ElasticityViewHelper {

    AbsorbScrollView mScrollView;

    public ElasticityScrollViewHelper(@NonNull AbsorbScrollView view) {
        super(view);
        mScrollView = view;
    }

    @Override
    public void init(AttributeSet attrs, int defStyleAttr) {
        mView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mView.setWillNotDraw(false);
        super.init(attrs, defStyleAttr);
        setOrientation(ElasticityScrollable.VERTICAL);
    }
}
