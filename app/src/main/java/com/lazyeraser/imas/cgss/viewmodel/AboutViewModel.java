package com.lazyeraser.imas.cgss.viewmodel;

import android.content.Intent;
import android.databinding.ObservableField;
import android.net.Uri;

import com.kelin.mvvmlight.command.ReplyCommand;
import com.lazyeraser.imas.derehelper.BuildConfig;
import com.lazyeraser.imas.main.BaseActivity;
import com.lazyeraser.imas.main.BaseViewModel;

/**
 * Created by lazyeraser on 2017/11/30.
 */

public class AboutViewModel extends BaseViewModel {

    public final ObservableField<String> versionName = new ObservableField<>();

    public final ReplyCommand gitUrlClick = new ReplyCommand(() -> {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("https://github.com/lazyeraser/DereHelper");
        intent.setData(content_url);
        mContext.startActivity(intent);
    });

    public AboutViewModel(BaseActivity mContext) {
        super(mContext);
        versionName.set(" " + BuildConfig.VERSION_NAME);
    }

}
