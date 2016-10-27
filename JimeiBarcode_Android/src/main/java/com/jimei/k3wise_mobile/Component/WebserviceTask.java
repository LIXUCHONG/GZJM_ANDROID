package com.jimei.k3wise_mobile.Component;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.view.View;

import com.jimei.k3wise_mobile.Util.KingdeeK3WiseWebServiceHelper;
import com.jimei.k3wise_mobile.Util.ShowDialog;

import org.json.JSONObject;

/**
 * Created by lee on 2016/9/9.
 */
public class WebserviceTask extends AsyncTask<Void, Void, Message> {

    Context context;
    String Method;
    JSONObject jParas=new JSONObject();

    public WebserviceTask(Context context, String method, JSONObject Paras) {
        this.context = context;
        Method = method;
        jParas = Paras;
    }

    @Override
    protected Message doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.

        // 返回登录结果
        try {
            return KingdeeK3WiseWebServiceHelper.Invoke(context, Method, jParas);
//            Message msg = new Message();
//            msg.what = KingdeeK3WiseWebServiceHelper.INVOKE_SUCCESS;
//            msg.obj = "";
//            return msg;
        }catch (Exception ex) {
            Message msg = new Message();
            msg.what = KingdeeK3WiseWebServiceHelper.EXCEPTION;
            msg.obj = ex.getMessage();
            return msg;
        }
    }

    @Override
    protected void onPostExecute(final Message msg) {
        switch (msg.what) {
            case KingdeeK3WiseWebServiceHelper.CONNECTION_ERROR:
                ShowDialog.ExceptionDialog(context,msg.obj.toString());
                break;
            case KingdeeK3WiseWebServiceHelper.EXCEPTION:
                ShowDialog.ExceptionDialog(context,msg.obj.toString());
                break;
            case KingdeeK3WiseWebServiceHelper.INVOKE_SUCCESS:
                AudioPlayer.playSuccessSound();
                break;
        }
    }
}
