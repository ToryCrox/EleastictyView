package com.aleaf.eleastictyview;

import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by tory on 2017/6/15.
 */

public class ElasticityScrollViewHelper extends ElasticityViewHelper {

    public ElasticityScrollViewHelper(@NonNull View view) {
        super(view);
    }

    @Override
    public void init(AttributeSet attrs, int defStyleAttr) {
        mView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mView.setWillNotDraw(false);
        super.init(attrs, defStyleAttr);
        setOrientation(ElasticityScrollable.VERTICAL);
    }
}
