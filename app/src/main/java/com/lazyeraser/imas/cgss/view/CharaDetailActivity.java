package com.lazyeraser.imas.cgss.view;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;

import com.lazyeraser.imas.cgss.entity.Chara;
import com.lazyeraser.imas.cgss.utils.JsonUtils;
import com.lazyeraser.imas.cgss.viewmodel.CharaViewModel;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;

/**
 * Created by lazyeraser on 2017/11/5.
 * Card Detail
 */

public class CharaDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(R.string.idol_info);
        initActionBar(ACTIONBAR_TYPE_NULL);
        Chara chara = JsonUtils.getBeanFromJson(umi.getIntentString("theChara"), Chara.class);
        setBinding(R.layout.activity_chara_detail)
                .setVariable(com.lazyeraser.imas.derehelper.BR.viewModel, new CharaViewModel(this, chara));
        ViewCompat.setTransitionName(getBView(R.id.chara_icon), "chara_icon");
    }

    @Override
    protected void backBtnAction() {
        ActivityCompat.finishAfterTransition(this);
    }
}
