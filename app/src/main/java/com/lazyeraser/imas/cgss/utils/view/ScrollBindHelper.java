package com.lazyeraser.imas.cgss.utils.view;

import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.SeekBar;

/**
 * Created by lazyeraser on 2018/1/9.
 */

public class ScrollBindHelper implements SeekBar.OnSeekBarChangeListener, NestedScrollView.OnScrollChangeListener {

    private final SeekBar seekBar;
    private final NestedScrollView scrollView;
    private final View scrollContent;

    //使用静态方法绑定并返回对象
    public static ScrollBindHelper bind (SeekBar seekBar, NestedScrollView scrollView) {
        ScrollBindHelper helper = new ScrollBindHelper(seekBar, scrollView);
        seekBar.setOnSeekBarChangeListener(helper);
        scrollView.setOnScrollChangeListener(helper);
        return helper;
    }

    private ScrollBindHelper (SeekBar seekBar, NestedScrollView scrollView) {
        this.seekBar = seekBar;
        this.scrollView = scrollView;
        this.scrollContent = scrollView.getChildAt(0);
    }

    //用户是否正在拖动SeekBar的标志
    private boolean isUserSeeking;

    //获取滚动范围
    private int getScrollRange () {
        return scrollContent.getHeight() - scrollView.getHeight();
    }

    @Override
    public void onScrollChange (NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        //用户拖动SeekBar时不触发ScrollView的回调
        if (isUserSeeking) {return;}

        //计算当前滑动位置相对于整个范围的百分比，并映射到SeekBar上
        int range = getScrollRange();
        seekBar.setProgress(range != 0 ? 100 - (scrollY * 100 / range) : 0);
    }

    @Override
    public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
        //当不是用户操作，也就是ScrollView的滚动隐射过来时不执行操作
        if (!fromUser) { return;}

        //将拖动的百分比换算成Y值，并映射到SrollView上。
        scrollView.scrollTo(0, (100 - progress) * getScrollRange() / 100);
    }

    @Override
    public void onStartTrackingTouch (SeekBar seekBar) {
        //标记用户正在拖动SeekBar
        isUserSeeking = true;
    }

    @Override
    public void onStopTrackingTouch (SeekBar seekBar) {
        //标记用户已经不再操作SeekBar
        isUserSeeking = false;
    }
}
