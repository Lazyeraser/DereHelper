package com.lazyeraser.imas.cgss.utils.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;


import com.lazyeraser.imas.derehelper.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lazye on 2017/3/3.
 * 底部弹出菜单
 * addBtn（String 按钮文字，int 按钮顺序）添加按钮
 * OnBtnClickListener.onBtnClick(int 按钮顺序) 根据按钮顺序监听按钮点击事件
 */
public class BottomPopMenu extends AlertDialog {

    private View view;
    private Context mContext;
    private LinearLayout btnLayout;
    private LinearLayout.LayoutParams lp;
    private List<OnBtnClickListener> onBtnClickListener = new ArrayList<>();
    private Map<Integer, Button> buttonMap = new HashMap<>();

    public BottomPopMenu(Context context) {
        super(context, R.style.dialog_theme);
        this.mContext = context;
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        Window window = getWindow();
        window.setWindowAnimations(R.style.Animation_Bottom_Dialog); //设置进出动画
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
    }

    private void init(){
        view = LayoutInflater.from(mContext).inflate(R.layout.umi_bottom_popup_menu, null);
        setCanceledOnTouchOutside(true);

        btnLayout = (LinearLayout)view.findViewById(R.id.bottom_menu_buttons);
        Button cancel = (Button) view.findViewById(R.id.bottom_menu_cancel);
        lp = (LinearLayout.LayoutParams) cancel.getLayoutParams();
        lp.topMargin = 10;
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    public BottomPopMenu addBtn(String btnTxt, final int i){
        Button button = new Button(mContext);
        buttonMap.put(i, button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                onBtnClickListener.forEach(onBtnClickListener1 -> onBtnClickListener1.onBtnClick(i));
            }
        });
        button.setBackgroundResource(R.drawable.umi_bg_corners_solid);
        button.setLayoutParams(lp);
        button.setText(btnTxt);
        button.setTextColor(Color.WHITE);
        btnLayout.addView(button);
        return this;
    }

    public void setBtnTxt(int i, String txt){
        buttonMap.get(i).setText(txt);
    }

    public interface OnBtnClickListener{
        void onBtnClick(int i);
    }

    public void onMenuClick(OnBtnClickListener onBtnClickListener) {
        this.onBtnClickListener.add(onBtnClickListener);
    }
}
