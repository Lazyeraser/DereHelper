package com.lazyeraser.imas.cgss.utils.view;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lazyeraser.imas.derehelper.R;

/**
 * Created by lazyeraser on 2018/1/7.
 */

public class SettingOptionView extends RelativeLayout {

    private SwitchCompat switchCompat;
    private TextView titleView;

    public SettingOptionView(Context context) {
        super(context);
        init(context);
    }

    public SettingOptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SettingOptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.item_setting_option, this);
        switchCompat = (SwitchCompat)findViewById(R.id.setting_opt_switch);
        titleView = (TextView)findViewById(R.id.setting_opt_title);
        setOnClickListener(v -> switchCompat.toggle());
    }

    public SwitchCompat getSwitchCompat() {
        return switchCompat;
    }

    public TextView getTitleView() {
        return titleView;
    }
}
