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
        setContentView(R.layout.activity_beat_map);
        String noteData = umi.getIntentString("data");
//        String name = umi.getIntentString("name");
        scrollView = (NestedScrollView)findViewById(R.id.scrollView);
        VerticalSeekBar seekBar = (VerticalSeekBar)findViewById(R.id.seekBar);
        ScrollBindHelper.bind(seekBar, scrollView);
        BeatMapView beatMapView = (BeatMapView)findViewById(R.id.beat_map);
        beatMapView.setData(noteData);
        setActionBarTitle(R.string.beat_map_view);
        setActionBarTxt(String.format("%d Notes", beatMapView.getData().get(0).status));
        initActionBar(ACTIONBAR_TYPE_TXT);
        TextView abTxt = (TextView)findViewById(R.id.actionBar_txtBtn);
        abTxt.setTextSize(14);
        anchor = beatMapView.getFistNoteY() - 450;
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
