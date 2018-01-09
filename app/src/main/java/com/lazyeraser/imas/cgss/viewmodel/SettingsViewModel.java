package com.lazyeraser.imas.cgss.viewmodel;

import android.databinding.ObservableBoolean;

import com.kelin.mvvmlight.command.ReplyCommand;
import com.lazyeraser.imas.cgss.utils.SharedHelper;
import com.lazyeraser.imas.cgss.utils.Utils;
import com.lazyeraser.imas.main.BaseActivity;
import com.lazyeraser.imas.main.BaseViewModel;

/**
 * Created by lazyeraser on 2017/11/30.
 */

public class SettingsViewModel extends BaseViewModel {

    public final ObservableBoolean autoData = new ObservableBoolean(umi.getSP(SharedHelper.KEY_AUTO_DATA));
    public final ObservableBoolean autoApp = new ObservableBoolean(umi.getSP(SharedHelper.KEY_AUTO_APP));
    public final ObservableBoolean defaultTran = new ObservableBoolean(umi.getSP(SharedHelper.KEY_DEFAULT_TRAN));
    public final ObservableBoolean analytics = new ObservableBoolean(umi.getSP(SharedHelper.KEY_ANALYTICS_ON));
    public final ObservableBoolean rProxy = new ObservableBoolean(umi.getSP(SharedHelper.KEY_USE_REVERSE_PROXY));

    public final ReplyCommand<Boolean> onAutoDataSwitchCheck = new ReplyCommand<>(check -> saveSP(SharedHelper.KEY_AUTO_DATA, check));

    public final ReplyCommand<Boolean> onAutoAppSwitchCheck = new ReplyCommand<>(check -> saveSP(SharedHelper.KEY_AUTO_APP, check));

    public final ReplyCommand<Boolean> onProxyCheck = new ReplyCommand<>(check -> saveSP(SharedHelper.KEY_DEFAULT_TRAN, check));

    public final ReplyCommand<Boolean> onDefaultTranSwitchCheck = new ReplyCommand<>(check -> saveSP(SharedHelper.KEY_DEFAULT_TRAN, check));

    public final ReplyCommand<Boolean> onAnalyticsCheck = new ReplyCommand<>(check -> {
        saveSP(SharedHelper.KEY_ANALYTICS_ON, check);
        if (check)
            Utils.turnOnGA(mContext);
    });

    private void saveSP(String key, boolean value){
        umi.spSave(key, value ? "true" : "false");
    }

    public SettingsViewModel(BaseActivity mContext) {
        super(mContext);
    }

}
