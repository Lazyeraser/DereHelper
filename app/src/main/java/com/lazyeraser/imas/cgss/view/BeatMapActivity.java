package com.lazyeraser.imas.cgss.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lazyeraser.imas.cgss.utils.view.BeatMapView;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;

/**
 * Created by lazyeraser on 2018/1/6.
 */

public class BeatMapActivity extends BaseActivity {

    private int anchor;

    private boolean init = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beat_map);
        String noteData = umi.getIntentString("data");
//        String name = umi.getIntentString("name");
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
        ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView);
        if (scrollView.getScrollY() == 0 && !init){
            scrollView.smoothScrollTo(0, anchor);
            init = true;
        }
//        scrollView.scrollTo(0, Integer.MAX_VALUE);

    }
}
