package com.jimei.k3wise_mobile.Util;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.jimei.k3wise_mobile.BO.LoginUser;
import com.jimei.k3wise_mobile.SettingsActivity;
import com.jimei.k3wise_mobile.Util.CommonHelper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Created by lee on 2016/9/5.
 */
public class KingdeeK3WiseWebServiceHelper {
    public static final int LOGIN_SUCCESS = 1;
    public static final int LOGIN_FAIL = 2;
    public static final int INVOKE_SUCCESS = 3;
    public static final int INVOKE_NULL = 4;
    public static final int ADMIN_LOGIN_SUCCESS = 5;
    public static final int CONNECTION_ERROR = 0;
    public static final int EXCEPTION = -1;

    //K3WISE website url
//    public static String K3WISE_WEBSITE_URL = "http://192.168.0.200:80/JimeiBarcode_WebServices/AndroidService.asmx/";
    public static String K3WISE_WEBSITE_URL = "http://%s:%s/JimeiBarcode_WebServices/AndroidService.asmx/%s";

    //cookie
    public static String COOKIE_VALUE = null;

    //设置站点地址
    private static String GetRequestUrl(Context context, String method){
        String ip=PreferencesHelper.Get(context,"http_connect_ip");
        String port = PreferencesHelper.Get(context,"http_connect_port");
        return String.format(K3WISE_WEBSITE_URL, ip, port, method);
    }

    //初始化HTTP连接
    private static HttpURLConnection InitURLConn(Context context, String method, JSONObject paras)
            throws Exception {
//        URL full_url = new URL(K3WISE_WEBSITE_URL.concat(method));
        URL full_url = new URL(GetRequestUrl(context, method));

        HttpURLConnection Connection = (HttpURLConnection) full_url.openConnection();
        Connection.setChunkedStreamingMode(0);
        Connection.setConnectTimeout(90 * 1000);
        Connection.setReadTimeout(90 * 1000);
        //使用cookie
//        if (COOKIE_VALUE != null) {
//            Connection.setRequestProperty("Cookie", COOKIE_VALUE);
//        }

        Connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");

        //设置连接类型
        Connection.setRequestMethod("POST");

        //是否使用缓存
        Connection.setUseCaches(false);

        //是否向HTTP连接输出
        Connection.setDoOutput(true);

        DataOutputStream outputStream = new DataOutputStream(Connection.getOutputStream());

        JSONObject jObj = new JSONObject();
        jObj.put("method", method);
        jObj.put("Platform", "Android");
        jObj.put("versionCode", CommonHelper.getVersionCode());
        jObj.put("versionName", CommonHelper.getVersionName());
        jObj.put("userid", LoginUser.Id);

        if(paras!=null) {
            Iterator keys = paras.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                jObj.put(key, URLEncoder.encode(paras.getString(key),"UTF-8"));
            }
        }

        outputStream.writeBytes(jObj.toString());
        outputStream.flush();
        outputStream.close();

        return Connection;
    }

    public static Message Invoke(Context context, String method, JSONObject paras) throws Exception {

        Message msgResult = new Message();

        HttpURLConnection connectionInvoke = InitURLConn(context, method, paras);

        if (connectionInvoke.getResponseCode() == 200) {

            // 获取Cookie
//                for (String key1 : connectionInvoke.getHeaderFields().keySet()) {
//                    if (key1 != null && key1.equalsIgnoreCase("Set-Cookie")) {
//                        String tempCookieVal = connectionInvoke.getHeaderField(key1);
//                        if (tempCookieVal.startsWith("kdservice-sessionid")) {
//                            COOKIE_VALUE = tempCookieVal;
//                            break;
//                        }
//                    }
//                }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connectionInvoke.getInputStream(), "utf-8"));

            String line;
            String sResult = "";

            while ((line = reader.readLine()) != null) {
                sResult += line;
            }

            if (sResult.length() > 0) {
                msgResult.obj = sResult;
                msgResult.what = INVOKE_SUCCESS;
            } else {
                msgResult.what = INVOKE_NULL;
            }

            reader.close();
        } else {
            msgResult.obj = (new BufferedReader(new InputStreamReader(connectionInvoke.getErrorStream(), "utf-8"))).toString();
            msgResult.what = CONNECTION_ERROR;
        }

        connectionInvoke.disconnect();

        return msgResult;
    }
}
