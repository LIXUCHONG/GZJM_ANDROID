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

    WebserviceLoader Instance;
    Message messageResult;

    public WebserviceLoader(Context context, String method, JSONObject Paras) {
        super(context);
        this.context = context;
        webMethod = method;
        methodParas = Paras;

        Instance = this;
    }

    public WebserviceLoader(Context context) {
        super(context);
        this.context = context;

        Instance = this;
    }

    public void setWebMethod(String webMethod) {
        this.webMethod = webMethod;
    }

    public void setMethodParas(JSONObject methodParas) {
        this.methodParas = methodParas;
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

            if (messageResult.what == KingdeeK3WiseWebServiceHelper.INVOKE_SUCCESS) {
                AudioPlayer.playSuccessSound();
            }

            return messageResult;
        } catch (Exception ex) {
            Message msg = new Message();
            msg.what = KingdeeK3WiseWebServiceHelper.EXCEPTION;
            msg.obj = ex.getMessage();
            return msg;
        }
    }

    @Override
    public void onCanceled(Message data) {
        messageResult = null;
    }
}
