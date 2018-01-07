package com.lazyeraser.imas.cgss.view;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;

import com.lazyeraser.imas.cgss.entity.Song;
import com.lazyeraser.imas.cgss.utils.JsonUtils;
import com.lazyeraser.imas.cgss.viewmodel.SongVM;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;

/**
 * Created by lazyeraser on 2017/11/5.
 * Card Detail
 */

public class SongDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(R.string.song_detail);
        initActionBar(ACTIONBAR_TYPE_NULL);
        Song song = JsonUtils.getBeanFromJson(umi.getIntentString("data"), Song.class);
        setBinding(R.layout.activity_song_detail)
                .setVariable(com.lazyeraser.imas.derehelper.BR.viewModel, new SongVM(this, song));
        ViewCompat.setTransitionName(getBView(R.id.song_jacket), "song_jacket");
    }

    @Override
    protected void backBtnAction() {
        ActivityCompat.finishAfterTransition(this);
    }
}
