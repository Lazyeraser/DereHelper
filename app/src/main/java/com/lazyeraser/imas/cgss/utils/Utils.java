package com.lazyeraser.imas.cgss.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.AnyRes;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lazyeraser on 2017/9/14.
 */

public class Utils {

    public static void turnOnGA(Context context){
        SStaticR.ANALYTICS_ON = true;
    }

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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
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

    public static Bitmap readBitMap(Context context, @AnyRes int resId) {

        BitmapFactory.Options opt = new BitmapFactory.Options();

        opt.inPreferredConfig = Bitmap.Config.RGB_565;

        opt.inPurgeable = true;

        opt.inInputShareable = true;

        InputStream is = context.getResources().openRawResource(resId);

        return BitmapFactory.decodeStream(is, null, opt);
    }
    public static Date stringToDate(String strDate, String pattern) {

        if (pattern == null) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        try {
            return new SimpleDateFormat(pattern).parse(strDate);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate;
        formatDate = sdf.format(date);
        return formatDate;
    }

    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "CGSS");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "Dere_" + System.currentTimeMillis() + ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        // 其次把文件插入到系统图库
        /*try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
        return true;
    }

    public static String getFileMD5(File f) {
        BigInteger bi = null;
        try {
            byte[] buffer = new byte[8192];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(f);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bi == null ? "" : bi.toString(16);
    }

    public static String getFileMD5(String path) {
        return getFileMD5(new File(path));
    }

    public static boolean igCaseContain(String s1, String s2){
        return s2.toLowerCase().contains(s1.toLowerCase());
    }

    public static String getVoice(long object_id, long use, long index) {
        long a = (object_id << 40) | ((use & 0xFF) << 24) | ((index & 0xFF) << 16) | 0x11AB;

        a &= 0xFFFFFFFFFFFFFFFFL;
        a ^= 0x1042FC1040200700L;
        String basename = Long.toHexString(a).substring(1);

        return String.format("va2/%s.mp3", basename);
    }
}
