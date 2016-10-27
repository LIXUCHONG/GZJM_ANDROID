package com.jimei.k3wise_mobile.Component;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Message;
import android.support.v4.content.AsyncTaskLoader;

import com.jimei.k3wise_mobile.Util.KingdeeK3WiseWebServiceHelper;

import org.json.JSONObject;

/**
 * Created by lee on 2016/10/26.
 */

public class WebserviceLoader extends AsyncTaskLoader<Message> {

    Context context;
    String webMethod;
    JSONObject methodParas;

    ProgressDialog progressDialog;

    WebserviceLoader Instance;
    Message messageResult;

    public WebserviceLoader(Context context, String method, JSONObject Paras) {
        super(context);
        this.context = context;
        webMethod = method;
        methodParas = Paras;

        Instance=this;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        super.forceLoad();
    }

    @Override
    public Message loadInBackground() {
        // 返回登录结果
        try {
//            showProgress();
            messageResult = KingdeeK3WiseWebServiceHelper.Invoke(context, webMethod, methodParas);
//            progressDialog.dismiss();
            return messageResult;
        } catch (Exception ex) {
            Message msg = new Message();
            msg.what = KingdeeK3WiseWebServiceHelper.EXCEPTION;
            msg.obj = ex.getMessage();
            return msg;
        }
    }

    private void showProgress(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("等待");
        progressDialog.setMessage("读取数据...请等待");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(Build.VERSION.SDK_INT>=16) {
                    if(!WebserviceLoader.super.isLoadInBackgroundCanceled()) {
                        WebserviceLoader.super.cancelLoadInBackground();
                    }
                }else {
                    WebserviceLoader.super.cancelLoad();
                }

                messageResult=null;
            }
        });
        progressDialog.show();
    }
}
