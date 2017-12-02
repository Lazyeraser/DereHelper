package com.lazyeraser.imas.cgss.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.SStaticR;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateManager {

    private String curVersion;
    private String newVersion;
    private int curVersionCode;
    private int newVersionCode;
    private String updateInfo;
    private UpdateCallback callback;
    private Context mContext;

    private float progress;
    private int maxlength;
    private Boolean hasNewVersion; //是否有新版本
    private Boolean canceled;

    //存放更新APK文件的路径
    public static String UPDATE_DOWNLOAD_URL;
    //存放更新APK文件相应的版本说明路径
    public static final String UPDATE_CHECKURL = SStaticR.SERVER_URL_UPDATE + "version.json";

    public static final String UPDATE_SAVE_NAME = "derehelper.apk";
    private static final int UPDATE_CHECKCOMPLETED = 1; //检测更新
    private static final int UPDATE_DOWNLOADING = 2;    //下载
    private static final int UPDATE_DOWNLOAD_ERROR = 3; //下载错误
    private static final int UPDATE_DOWNLOAD_COMPLETED = 4;  //下载完成
    private static final int UPDATE_DOWNLOAD_CANCELED = 5;   //下载取消

    //从服务器上下载apk存放文件夹
    private String savefolder = "/sdcard/";

    private SweetAlertDialog updateProgressDialog;

    public UpdateManager(Context context, UpdateCallback updateCallback) {
        mContext = context;
        callback = updateCallback;
        canceled = false;
        getCurVersion();
    }

    public UpdateManager(Context context){
        mContext = context;
        callback = defaultCallBack;
        canceled = false;
        getCurVersion();
    }

    public String getNewVersionName()
    {
        return newVersion;
    }

    public String getUpdateInfo()
    {
        return updateInfo;
    }

    //获取当前已安装应用版本信息
    private void getCurVersion() {
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0);
            curVersion = pInfo.versionName;
            curVersionCode = pInfo.versionCode;
        } catch (NameNotFoundException e) {
            Log.e("update", e.getMessage());
            curVersion = "1.0.0";
            curVersionCode = 1;
        }
    }

    private boolean needNoUpdateHint = false;
    private String verjson, versionInfo;
    //检查更新
    public void checkUpdate(boolean needNoUpdateHint) {
        this.needNoUpdateHint = needNoUpdateHint;
        Utils.mPrint("checkUpdate:" + needNoUpdateHint);
        hasNewVersion = false;
        //获取服务器端版本信息
        verjson = "";
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(UPDATE_CHECKURL)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                verjson = response.body().string();
                try {
                    if (!TextUtils.isEmpty(verjson)) {
                        JSONObject obj = new JSONObject(verjson);
                        try {
                            newVersionCode = Integer.parseInt(obj.getString("verCode"));
                            newVersion = obj.getString("verName");
                            versionInfo = new String(obj.getString("verInfo").getBytes(), "UTF-8");
                            UPDATE_DOWNLOAD_URL = obj.getString("url");
                            updateInfo = "";
                            Utils.mPrint("update info:" + JsonUtils.getJsonFromBean(obj));
                            if (newVersionCode > curVersionCode) {
                                hasNewVersion = true;
                            }
                        } catch (Exception e) {
                            newVersionCode = -1;
                            newVersion = "";
                            updateInfo = "";
                        }
                        updateHandler.sendEmptyMessage(UPDATE_CHECKCOMPLETED);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    //安装apk
    public void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(
                Uri.fromFile(new File(savefolder, UPDATE_SAVE_NAME)),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void downloadPackage()
    {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(UPDATE_DOWNLOAD_URL);

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.connect();
                    maxlength = conn.getContentLength(); //获取文件大小
                    InputStream is = conn.getInputStream();


                    File ApkFile = new File(savefolder, UPDATE_SAVE_NAME);

                    if(ApkFile.exists())
                    {
                        ApkFile.delete();
                    }


                    FileOutputStream fos = new FileOutputStream(ApkFile);

                    int total = 0;
                    byte buf[] = new byte[1024*1024];
                    int numread = 0;

                    do{
                        numread = is.read(buf);
                        total += numread;
                       // progress =(int)(((float)total / maxlength) * 100); //下载进度
                        progress = (float)total / maxlength;

                        updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOADING));
                        if(numread <= 0){

                            updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_COMPLETED);
                            break;
                        }
                        fos.write(buf,0,numread);
                    }while(!canceled);
                    if(canceled)
                    {
                        updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_CANCELED);
                    }
                    fos.close();
                    is.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();

                    updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR,e.getMessage()));
                } catch(IOException e){
                    e.printStackTrace();
                    updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR,e.getMessage()));
                }

            }
        }.start();
    }

    public void cancelDownload()
    {
        canceled = true;
    }

    private Handler updateHandler = new Handler(msg -> {
        switch (msg.what) {
            case UPDATE_CHECKCOMPLETED:
                callback.checkUpdateCompleted(hasNewVersion, newVersion + "\n" + versionInfo);
                break;
            case UPDATE_DOWNLOADING:

                callback.downloadProgressChanged(progress, maxlength);
                break;
            case UPDATE_DOWNLOAD_ERROR:

                callback.downloadCompleted(false, msg.obj.toString());
                break;
            case UPDATE_DOWNLOAD_COMPLETED:

                callback.downloadCompleted(true, "");
                break;
            case UPDATE_DOWNLOAD_CANCELED:

                callback.downloadCanceled();
            default:
                break;
        }
        return true;
    });

    public interface UpdateCallback {
        public void checkUpdateCompleted(Boolean hasUpdate,
                                         CharSequence updateInfo);

        public void downloadProgressChanged(float progress, int maxlength);
        public void downloadCanceled();
        public void downloadCompleted(Boolean sucess, CharSequence errorMsg);
    }

    private UpdateManager.UpdateCallback defaultCallBack = new UpdateManager.UpdateCallback()
    {
        public void downloadProgressChanged(float progress, int maxlength) {

            if (updateProgressDialog != null
                    && updateProgressDialog.isShowing()) {
                //updateProgressDialog.getProgressHelper().set(maxlength/(1024*1024));
                updateProgressDialog.getProgressHelper().setInstantProgress(progress);
            }

        }

        public void downloadCompleted(Boolean sucess, CharSequence errorMsg) {
            if (updateProgressDialog != null
                    && updateProgressDialog.isShowing()) {
                updateProgressDialog.dismiss();
            }
            if (sucess) {
                update();
            } else {
                new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText(mContext.getString(R.string.dialog_error_title))
                        .setContentText(mContext.getString(R.string.dialog_downfailed_msg))
                        .setConfirmText(mContext.getString(R.string.dialog_downfailed_btnnext))
                        .setCancelText(mContext.getString(R.string.dialog_update_btnnext))
                        .setConfirmClickListener((dialog) -> downloadPackage())
                        .show();
            }
        }

        public void downloadCanceled()
        {
            // TODO Auto-generated method stub

        }

        //检查更新
        public void checkUpdateCompleted(Boolean hasUpdate,
                                         CharSequence updateInfo) {
            if (hasUpdate) {
                new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText(mContext.getString(R.string.dialog_update_title))
                        .setContentText(mContext.getString(R.string.dialog_update_msg)
                                +updateInfo)
                        .setConfirmText(mContext.getString(R.string.dialog_update_btnupdate))
                        .setCancelText(mContext.getString(R.string.dialog_update_btnnext))
                        .setConfirmClickListener((dialog) -> {
                            updateProgressDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
                            updateProgressDialog
                                    .setTitleText(mContext.getString(R.string.dialog_downloading_msg));
//                                updateProgressDialog.setIndeterminate(false);
//                                updateProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                                updateProgressDialog.setMax(100);
                            updateProgressDialog.getProgressHelper().setProgress(0);
                            updateProgressDialog.setCancelable(false);
                            updateProgressDialog.show();

                            downloadPackage();
                        })
                        .show();
            }else {
                Boolean isconnect;
                ConnectionDetector connectionDetector = new ConnectionDetector(mContext.getApplicationContext());
                isconnect = connectionDetector.isConnectingToInternet();
                if (!isconnect){
                    Toast.makeText(mContext, R.string.un_connect , Toast.LENGTH_SHORT).show();
                }else if (needNoUpdateHint){
                    new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText(mContext.getString(R.string.no_update))
                            .show();
                }

            }
        }
    };
    
}