package com.lazyeraser.imas.main;

import android.databinding.BindingConversion;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.kelin.mvvmlight.command.ReplyCommand;
import com.lazyeraser.imas.cgss.utils.Utils;
import com.lazyeraser.imas.cgss.utils.view.MultiLineChooseLayout;
import com.lazyeraser.imas.cgss.utils.view.SettingOptionView;
import com.lazyeraser.imas.cgss.view.CharaDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by lazyeraser on 2017/9/14.
 * bind自定义适配器
 */

public class BindingAdapter {

    @android.databinding.BindingAdapter(value = {"clickCommand"})
    public static void clickAction(View view, Action1<View> command) {
        view.setOnClickListener(command::call);
    }

    @android.databinding.BindingAdapter(value = {"imageUrl", "placeholder"}, requireAll = false)
    public static void loadImage(ImageView view, String imageUrl, @IdRes Integer placeholder) {
        if (placeholder != null){
            Picasso.with(view.getContext()).load(imageUrl).placeholder(placeholder).into(view);
        }else {
            Picasso.with(view.getContext()).load(imageUrl).into(view);
        }
    }

    @android.databinding.BindingAdapter(value = {"imgR"})
    public static void imgR(ImageView view, @DrawableRes int imgR) {
        Picasso.with(view.getContext()).load(imgR).into(view);
    }

    @android.databinding.BindingAdapter({"onSelChange"})
    public static void onSelChange(MultiLineChooseLayout view, ReplyCommand<List<String>> onSelChange) {
        view.setOnItemClickListener((position, text) -> onSelChange.execute(view.getAllItemSelectedTextWithListArray()));
    }

    @android.databinding.BindingAdapter({"onSelChangeIndex"})
    public static void onSelChangeIndex(MultiLineChooseLayout view, ReplyCommand<List<Integer>> onSelChangeIndex) {
        view.setOnItemClickListener((position, text) -> onSelChangeIndex.execute(view.getAllItemSelectedIndex()));
    }

    @android.databinding.BindingAdapter({"onListItemClickedWithView"})
    public static void onListItemClickedWithView(View view, ReplyCommand<Pair<Integer, View>> command) {
        if (view instanceof ListView){
            ((ListView)view).setOnItemClickListener(((parent, view1, position, id) -> command.execute(Pair.create(position, view1))));
        }else if (view instanceof GridView){
            ((GridView)view).setOnItemClickListener(((parent, view1, position, id) -> command.execute(Pair.create(position, view1))));
        }
    }

    @android.databinding.BindingAdapter({"onCheckChanged"})
    public static void onCheckChanged(View view, ReplyCommand<Boolean> command) {
        if (view instanceof CheckBox){
            ((CheckBox)view).setOnCheckedChangeListener((buttonView, isChecked) -> command.execute(isChecked));
        }else if (view instanceof SwitchCompat){
            ((SwitchCompat)view).setOnCheckedChangeListener((buttonView, isChecked) -> command.execute(isChecked));
        }else if (view instanceof SettingOptionView){
            ((SettingOptionView)view).getSwitchCompat().setOnCheckedChangeListener((buttonView, isChecked) -> command.execute(isChecked));
        }
    }

    @android.databinding.BindingAdapter({"checked"})
    public static void checked(View view, boolean checked) {
        if (view instanceof CheckBox){
            ((CheckBox)view).setChecked(checked);
        }else if (view instanceof SwitchCompat){
            ((SwitchCompat)view).setChecked(checked);
        }else if (view instanceof SettingOptionView){
            ((SettingOptionView)view).getSwitchCompat().setChecked(checked);
        }
    }

    @android.databinding.BindingAdapter({"titleTxt"})
    public static void titleTxt(View view, Object title) {
        if (view instanceof SettingOptionView){
            if (title instanceof String){
                ((SettingOptionView)view).getTitleView().setText((String)title);
            }else if (title instanceof Integer){
                ((SettingOptionView)view).getTitleView().setText((Integer)title);
            }
        }
    }

    @android.databinding.BindingAdapter(value = {"charaID", "placeholder"}, requireAll = false)
    public static void loadCharaIcon(ImageView view, Integer charaID, @IdRes Integer placeholder) {
        if (charaID == null || charaID == 0)
            return;
        String imageUrl = String.format(SStaticR.charaIconUrl, charaID);
        if (placeholder != null){
            Picasso.with(view.getContext()).load(imageUrl).placeholder(placeholder).into(view);
        }else {
            Picasso.with(view.getContext()).load(imageUrl).into(view);
        }
    }

    @android.databinding.BindingAdapter({"goDetailWithCharaId"})
    public static void goDetailWithCharaId(View view, int charaID) {
        if (charaID == 0)
            return;
        view.setOnClickListener(v ->
                Base.jumpWithTran((BaseActivity)view.getContext(), CharaDetailActivity.class, v, "chara_icon", new Pair<>("charaID", String.valueOf(charaID))));
    }


    @BindingConversion
    public static String dateToString(Date date) {
        return Utils.formatDate(date);
    }

    @BindingConversion
    public static String intToString(int integer) {
        return integer == 0 ? "" : String.valueOf(integer);
    }

    @BindingConversion
    public static String doubleToString(double d) {
        return d == 0 ? "" : String.valueOf(d);
    }
}
