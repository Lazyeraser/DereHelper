package com.lazyeraser.imas.cgss.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;

import com.alexvasilkov.gestures.GestureController;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.lazyeraser.imas.cgss.viewmodel.FullScreenImageVM;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;

/**
 * Created by lazyeraser on 2017/11/28.
 */

public class FullScreenImageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_full_screen_img)
                .setVariable(com.lazyeraser.imas.derehelper.BR.viewModel, new FullScreenImageVM(this, umi.getIntentString("picUrl")));
        GestureImageView gestureImageView = (GestureImageView)getBView(R.id.big_pic);
        ViewCompat.setTransitionName(gestureImageView, "big_pic");
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

            }

            @Override
            public boolean onDoubleTap(@NonNull MotionEvent event) {
                return false;
            }
        });
    }

    @Override
    protected void backBtnAction() {
        ActivityCompat.finishAfterTransition(this);
    }
}
