package com.lazyeraser.imas.cgss.utils.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;


public class ExRecyclerViewInScv extends RecyclerView {

    
    public ExRecyclerViewInScv(Context context) {
        super(context);
    }

    public ExRecyclerViewInScv(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ExRecyclerViewInScv(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
