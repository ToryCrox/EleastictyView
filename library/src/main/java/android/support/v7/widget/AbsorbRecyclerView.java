/*Transsion Top Secret*/
package android.support.v7.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by tao.xu2 on 2017/6/14.
 */

public class AbsorbRecyclerView extends RecyclerView {
    protected AbsorbRecyclerView(Context context) {
        this(context, null);
    }

    protected AbsorbRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    protected AbsorbRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void absorbGlows(int velocityX, int velocityY) {
    }

    public void clearRecycler(){
        mRecycler.clear();
        getRecycledViewPool().clear();
        invalidateItemDecorations();
    }
}
