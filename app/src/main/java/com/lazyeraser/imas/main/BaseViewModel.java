package com.lazyeraser.imas.main;


import com.kelin.mvvmlight.base.ViewModel;
import com.kelin.mvvmlight.messenger.Messenger;

/**
 * Created by lazyeraser on 2017/9/14.
 */

public class BaseViewModel implements ViewModel {

    public Base umi;

    public BaseActivity mContext;

    public BaseViewModel(BaseActivity mContext) {
        this.mContext = mContext;
        umi = mContext.umi;
    }

    void onDestroy(){
        Messenger.getDefault().unregister(mContext);
    }
}
