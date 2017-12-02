package com.lazyeraser.imas.cgss.view;

import android.os.Bundle;

import com.lazyeraser.imas.cgss.viewmodel.SettingsViewModel;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;

/**
 * Created by lazyeraser on 2017/11/28.
 */

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(R.string.settings);
        initActionBar(ACTIONBAR_TYPE_NULL);
        setBinding(R.layout.activity_settings)
                .setVariable(com.lazyeraser.imas.derehelper.BR.viewModel, new SettingsViewModel(this));
    }
}
