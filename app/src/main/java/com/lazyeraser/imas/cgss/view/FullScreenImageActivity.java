package com.lazyeraser.imas.cgss.view;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;

import com.alexvasilkov.gestures.GestureController;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.lazyeraser.imas.cgss.utils.Utils;
import com.lazyeraser.imas.cgss.utils.view.BottomPopMenu;
import com.lazyeraser.imas.cgss.viewmodel.FullScreenImageVM;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;
import com.lazyeraser.imas.retrofit.ExceptionHandler;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lazyeraser on 2017/11/28.
 */

public class FullScreenImageActivity extends BaseActivity {


    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = umi.getIntentString("picUrl");
        setBinding(R.layout.activity_full_screen_img)
                .setVariable(com.lazyeraser.imas.derehelper.BR.viewModel, new FullScreenImageVM(this, url));
        GestureImageView gestureImageView = (GestureImageView)getBView(R.id.big_pic);
        ViewCompat.setTransitionName(gestureImageView, "big_pic");
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        gestureImageView.getController().setLongPressEnabled(true);
        gestureImageView.getController().setOnGesturesListener(new GestureController.OnGestureListener() {
            @Override
            public void onDown(@NonNull MotionEvent event) {

            }

            @Override
            public void onUpOrCancel(@NonNull MotionEvent event) {

            }

            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent event) {
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent event) {
                backBtnAction();
                return false;
            }

            @Override
            public void onLongPress(@NonNull MotionEvent event) {
                BottomPopMenu menu = new BottomPopMenu(mContext);
                menu.addBtn(getString(R.string.save_to_gallery), 0);
                menu.onMenuClick(i -> {
                    if (i == 0){
                        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }else {
                            savePic();
                        }
                    }
                });
                menu.show();
            }

            @Override
            public boolean onDoubleTap(@NonNull MotionEvent event) {
                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                savePic();
            }
        }
    }

    private void savePic(){
        umi.showLoading();
        try {
            Observable<Boolean> bitmapObservable = Observable.create(sub -> {
                try {
                    Bitmap bitmap = Picasso.with(mContext).load(url).get();
                    sub.onNext(Utils.saveImageToGallery(mContext, bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sub.onCompleted();
            });
            bitmapObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success -> {
                        umi.dismissLoading();
                        umi.makeToast(success ? R.string.save_success : R.string.save_error);
                    }, ExceptionHandler::handleException);
        }catch (Exception e){
            umi.makeToast(R.string.save_error);
            e.printStackTrace();
        }
    }

    @Override
    protected void backBtnAction() {
        ActivityCompat.finishAfterTransition(this);
    }
}
