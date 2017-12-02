package com.lazyeraser.imas.main;

import android.support.annotation.IdRes;
import android.support.v4.util.Pair;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.kelin.mvvmlight.command.ReplyCommand;
import com.lazyeraser.imas.cgss.utils.view.MultiLineChooseLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lazyeraser on 2017/9/14.
 * bind自定义适配器
 */

public class BindingAdapter {

    @android.databinding.BindingAdapter(value = {"imageUrl", "placeholder"}, requireAll = false)
    public static void loadImage(ImageView view, String imageUrl, @IdRes Integer placeholder) {
        if (placeholder != null){
            Picasso.with(view.getContext()).load(imageUrl).placeholder(placeholder).into(view);
        }else {
            Picasso.with(view.getContext()).load(imageUrl).into(view);
        }
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
        }
    }

}
