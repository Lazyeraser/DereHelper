package com.lazyeraser.imas.cgss.utils.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.lazyeraser.imas.derehelper.R;

public class LoadingDialog extends AlertDialog {

	private ProgressBar progressBar;

	public LoadingDialog(Context context) {
		super(context, R.style.dialog_theme_no_dim);
	}

//	public LoadingDialog(Context context, int style) {
//		super(context, R.style.dialog_theme_no_dim);
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_loading);
		this.setCanceledOnTouchOutside(false);
		progressBar = (ProgressBar)findViewById(R.id.progress_dialog);
	}

	@Override
	public void show() {
		super.show();
		progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void hide() {
		super.hide();
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void dismiss() {
		super.dismiss();
		progressBar.setVisibility(View.GONE);
	}
}
