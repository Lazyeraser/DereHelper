package com.lazyeraser.imas.cgss.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.SStaticR;

import java.util.Locale;

/**
 * Created by lazyeraser on 2017/9/14.
 */

public class Utils {

    public static String emptyLessString(Context context, String s){
        return TextUtils.isEmpty(s) ? context.getString(R.string.empty) : s;
    }

    //隐藏所有键盘
    public static void hideAllInput(Activity activity){
        InputMethodManager imm =  (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public static boolean isChinese(){
        return Locale.getDefault().getLanguage().endsWith("zh");
    }

    @SafeVarargs
    public static <T> boolean varArgsContain(T key, T... args){
        for(T t : args){
            if (key.equals(t)) return true;
        }
        return false;
    }

    //设置snackBar的文字颜色
    public static void setSnackbarMsgTextColor(Snackbar snackbar, int color) {
        View view = snackbar.getView();
        ((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(color);
    }

    //设置snackBar的Action文字颜色
    public static void setSnackbarActTextColor(Snackbar snackbar, int color) {
        View view = snackbar.getView();
        ((TextView) view.findViewById(R.id.snackbar_action)).setTextColor(color);
    }
    public static void mPrint(String str){
        if (SStaticR.isDebug){
            System.out.println(str);
        }
    }
    public static boolean checkEmpty(String... str){
        for (String s : str){
            if (TextUtils.isEmpty(s)) {
                return false;
            }
        }
        return true;
    }


    public static void setLVEmptyView(ListView listView, String txt) {
        TextView emptyView = new TextView(listView.getContext());
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setText(txt);
        emptyView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
    }

    public static void setGridViewHeightBasedOnChildren(GridView gridView, int colNum) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        int lines = (int) Math.ceil((double) listAdapter.getCount() / colNum);

        for (int i = 0; i < lines; i++) {
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + (gridView.getVerticalSpacing() * (lines - 1));
        gridView.setLayoutParams(params);
    }

}
