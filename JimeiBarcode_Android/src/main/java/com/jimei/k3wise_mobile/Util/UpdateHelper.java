package com.jimei.k3wise_mobile.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.jimei.k3wise_mobile.Component.WebserviceTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lee on 2016/10/19.
 */

public class UpdateHelper {
    private WebserviceTask loadUpgradeInfoTask = null;
    private Context context;
    private ProgressDialog dlgProgress;
    private int progressValue;
    private String apkSavePath;

    private int serviceVersionCode;
    private String serviceVersionName;
    private String serviceApkUrl;
    private String apkStoragePath;
    private String apkName;

    private boolean cancelUpgrade = false;

    public UpdateHelper(Context context) {
        this.context = context;
    }

    public class LoadUpgradeInfoTask extends WebserviceTask {

        LoadUpgradeInfoTask(Context context, String method, JSONObject Paras) {
            super(context, method, Paras);
        }

        @Override
        protected void onPostExecute(final Message msg) {

            loadUpgradeInfoTask = null;
            super.onPostExecute(msg);
            switch (msg.what) {
                case KingdeeK3WiseWebServiceHelper.INVOKE_SUCCESS:
                    try {
                        GetUpgradeInfo(msg.obj.toString());
                        if (checkUpdate()) {
                            showNoticeDialog();
                        } else {
                            ShowDialog.MessageDialog(context, "已是最新版本");
                        }
                    } catch (Exception ex) {
                        ShowDialog.ExceptionDialog(context, ex.getMessage());
                        return;
                    }
                    break;
                case KingdeeK3WiseWebServiceHelper.INVOKE_NULL:
                    ShowDialog.WarningDialog(context, "更新配置信息读取失败");
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            loadUpgradeInfoTask = null;
        }

        private void GetUpgradeInfo(String jsonStr) throws Exception {
            JSONArray jsonArray = new JSONArray(jsonStr);

            for (int i = 0; i < jsonArray.length(); i++) {
                String key = jsonArray.getJSONObject(i).getString("Key");

                switch (key){
                    case "versionCode":
                        serviceVersionCode = jsonArray.getJSONObject(i).getInt("Value");
                        break;
                    case "versionName":
                        serviceVersionName = jsonArray.getJSONObject(i).getString("Value");
                        break;
                    case "url":
                        serviceApkUrl = String.format(jsonArray.getJSONObject(i).getString("Value"),CommonHelper.getServiceAddress(context));
                        break;
                    case  "insPath":
                        apkStoragePath = jsonArray.getJSONObject(i).getString("Value");
                        break;
                    case  "fileName":
                        apkName = jsonArray.getJSONObject(i).getString("Value");
                        break;
                }
            }
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 正在下载
                case 0:
                    // 设置进度条位置
                    dlgProgress.setProgress(progressValue);
                    break;
                case 1:
                    // 安装文件
                    dlgProgress.setMessage("下载完成，开始安装");
                    installApk();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private boolean checkUpdate() {
        return serviceVersionCode > CommonHelper.getVersionCode();
    }

    private void showNoticeDialog() {
        ShowDialog.YesNoDialog(context, String.format("检测到最新版本[%s]\n是否开始更新？",serviceVersionName), new Runnable() {
            @Override
            public void run() {
                showDownDialog();
            }
        }, null);
    }

    /**
     * 显示软件下载对话框
     */
    private void showDownDialog() {

        dlgProgress=new ProgressDialog(context);
        dlgProgress.setTitle("系统升级");
        dlgProgress.setMessage("更新中...请等待");
        dlgProgress.setIndeterminate(false);
        dlgProgress.setCancelable(false);
        dlgProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dlgProgress.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cancelUpgrade = true;
            }
        });
        dlgProgress.show();

        downloadApk();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
        new downloadApkThread().start();
    }

    /**
     * 下载文件线程
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    apkSavePath = Environment.getExternalStorageDirectory().toString() + apkStoragePath;
                    URL url = new URL(serviceApkUrl);
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(apkSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(apkSavePath, apkName);
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        progressValue = (int) (((float) count / length) * 100);
                        // 更新进度
                        mHandler.sendEmptyMessage(0);
                        if (numread <= 0) {
                            // 下载完成
                            mHandler.sendEmptyMessage(1);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpgrade);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (Exception e) {
                ShowDialog.ExceptionDialog(context,e.getMessage());
            }
            // 取消下载对话框显示
            dlgProgress.dismiss();
        }
    }

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(apkSavePath, apkName);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        context.startActivity(i);
    }

    public void Upgrade() {
        loadUpgradeInfoTask = new LoadUpgradeInfoTask(context, "GetAppUpgradeInfo", null);
        loadUpgradeInfoTask.execute();
    }
}
