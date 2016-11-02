package com.jimei.k3wise_mobile.Util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.Nullable;

import com.jimei.k3wise_mobile.Component.AudioPlayer;
import com.jimei.k3wise_mobile.Component.WebserviceLoader;

/**
 * Created by lee on 2016/9/9.
 */
public class ShowDialog {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void MessageDialog(Context context, String msg,final Runnable run_ok) {
        AudioPlayer.playSuccessSound();
        new AlertDialog.Builder(context).setTitle("提示").setMessage(msg)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(run_ok!=null){
                            run_ok.run();
                        }
                    }
                })
                .setNegativeButton("OK",null)
                .show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void MessageDialog(Context context, String msg) {
        MessageDialog(context,msg,null);
    }

    public static void ExceptionDialog(Context context, String msg) {
        ExceptionDialog(context,msg,null);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void ExceptionDialog(Context context, String msg,final Runnable run_ok) {
        AudioPlayer.playWarningSound();
        new AlertDialog.Builder(context).setTitle("错误").setMessage(msg)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(run_ok!=null){
                            run_ok.run();
                        }
                    }
                })
                .setNegativeButton("OK", null)
                .show();
    }

    public static void WarningDialog(Context context, String msg) {
        AudioPlayer.playAttentionSound();
        new AlertDialog.Builder(context).setTitle("提示").setMessage(msg)
                .setNegativeButton("OK", null)
                .show();
    }

    public static void YesNoDialog(Context context, String msg, final Runnable run_true, final Runnable run_false) {
        AudioPlayer.playAttentionSound();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if(run_false!=null) {
                            run_false.run();
                        }
                    }
                })
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if(run_true!=null) {
                            run_true.run();
                        }
                    }
                })
                .show();
    }

    static public ProgressDialog showLoaderProgressDialog(final Context context, final WebserviceLoader webserviceLoader, final boolean allowDismiss){
        final ProgressDialog progressDialog = new ProgressDialog(context){
            @Override
            public void onBackPressed() {
                if(allowDismiss) {
                    this.dismiss();
                    if (Build.VERSION.SDK_INT >= 16) {
                        webserviceLoader.cancelLoadInBackground();
                    } else {
                        webserviceLoader.cancelLoad();
                    }
                }
            }
        };
        progressDialog.setTitle("等待");
        progressDialog.setMessage("数据传输中...请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        if(allowDismiss) {
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog.onBackPressed();
                }
            });
        }
        progressDialog.show();

        return progressDialog;
    }

    static public ProgressDialog showLoaderProgressDialog(Context context, final WebserviceLoader webserviceLoader){
        return showLoaderProgressDialog(context,webserviceLoader,true);
    }
}
