package com.lazyeraser.imas.cgss.viewmodel;

import android.databinding.ObservableField;

import com.lazyeraser.imas.main.BaseActivity;
import com.lazyeraser.imas.main.BaseViewModel;

/**
 * Created by lazyeraser on 2017/11/28.
 */

public class FullScreenImageVM extends BaseViewModel {

    public final ObservableField<String> cardSpreadUrl = new ObservableField<>();

    public FullScreenImageVM(BaseActivity mContext, String url) {
        super(mContext);
        cardSpreadUrl.set(url);
    }

}
