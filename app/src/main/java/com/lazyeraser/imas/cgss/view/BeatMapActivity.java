package com.lazyeraser.imas.cgss.view;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.widget.TextView;

import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.lazyeraser.imas.cgss.utils.view.BeatMapView;
import com.lazyeraser.imas.cgss.utils.view.ScrollBindHelper;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;

/**
 * Created by lazyeraser on 2018/1/6.
 */

public class BeatMapActivity extends BaseActivity {

    private int anchor;
    private boolean init = false;

    private NestedScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_beat_map);
    }

    @Override
    protected void initView() {
        String noteData = umi.getIntentString("data");
//        String name = umi.getIntentString("name");
        scrollView = (NestedScrollView)findViewById(R.id.scrollView);
        BeatMapView beatMapView = (BeatMapView) findViewById(R.id.beat_map);
        beatMapView.setData(noteData);

        VerticalSeekBar seekBar = (VerticalSeekBar)findViewById(R.id.seekBar);
        ScrollBindHelper.bind(seekBar, scrollView);
        anchor = beatMapView.getFistNoteY() - 450;
        initToolbar(R.id.toolBar, getString(R.string.beat_map_view) + "/" + beatMapView.getData().get(0).status + " Notes");
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();

        if (scrollView.getScrollY() == 0 && !init){
            scrollView.smoothScrollTo(0, anchor);
            init = true;
        }
//        scrollView.scrollTo(0, Integer.MAX_VALUE);

    }
}
