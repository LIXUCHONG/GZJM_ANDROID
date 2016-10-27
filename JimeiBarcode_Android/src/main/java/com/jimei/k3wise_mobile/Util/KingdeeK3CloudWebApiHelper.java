package com.jimei.k3wise_mobile.Util;

import android.os.Message;

import com.jimei.k3wise_mobile.Util.CommonHelper;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
/**
 * Created by lee on 2016/8/6.
 */

public class KingdeeK3CloudWebApiHelper {

    public static final int LOGIN_SUCCESS = 1;
    public static final int LOGIN_FAIL = 2;
    public static final int INVOKE_SUCCESS = 3;
    public static final int INVOKE_NULL = 4;
    public static final int ADMIN_LOGIN_SUCCESS = 5;
    public static final int CONNECTION_ERROR = 0;
    public static final int EXCEPTION = -1;

    //K3CLOUD website url
    public static String K3CLOUD_WEBSITE_URL = "http://192.168.0.108/K3Cloud/";

    //cookie
    public static String COOKIE_VALUE = null;

    private static Map KINGDEE_METHOD_MAP = new HashMap();

    static {
        KINGDEE_METHOD_MAP.put("Save", "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Save.common.kdsvc");
        KINGDEE_METHOD_MAP.put("View", "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.View.common.kdsvc");
        KINGDEE_METHOD_MAP.put("Submit", "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Submit.common.kdsvc");
        KINGDEE_METHOD_MAP.put("Audit", "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Audit.common.kdsvc");
        KINGDEE_METHOD_MAP.put("UnAudit", "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.UnAudit.common.kdsvc");
        KINGDEE_METHOD_MAP.put("StatusConvert", "Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.StatusConvert.common.kdsvc");
    }


    //初始化HTTP连接
    private static HttpURLConnection InitURLConn(String method_url, JSONArray paras)
            throws Exception {
        URL full_url = new URL(K3CLOUD_WEBSITE_URL.concat(method_url));
        HttpURLConnection Connection = (HttpURLConnection) full_url.openConnection();

        //使用cookie
        if (COOKIE_VALUE != null) {
            Connection.setRequestProperty("Cookie", COOKIE_VALUE);
        }

        Connection.setRequestProperty("Content-Type", "application/json");

        //设置连接类型
        Connection.setRequestMethod("POST");

        //是否使用缓存
        Connection.setUseCaches(false);

        //是否向HTTP连接输出
        Connection.setDoOutput(true);

        DataOutputStream outputStream = new DataOutputStream(Connection.getOutputStream());

        UUID uuid = UUID.randomUUID();

        int hashCode = uuid.toString().hashCode();

        JSONObject jObj = new JSONObject();
        jObj.put("format", 1);
        jObj.put("useragent", "ApiClient");
        jObj.put("rid", hashCode);
        jObj.put("parameters", CommonHelper.chineseToUnicode(paras.toString()));
        jObj.put("timestamp", new Date().toString());
        jObj.put("v", "1.0");

        outputStream.writeBytes(jObj.toString());
        outputStream.flush();
        outputStream.close();

        return Connection;
    }

    // Login
    public static Message Login(String dbId, String user, String pwd, int lang){
        boolean bResult = false;
        Message msgResult = new Message();
        String sUrl = "Kingdee.BOS.WebApi.ServicesStub.AuthService.ValidateUser.common.kdsvc";

        JSONArray jParas = new JSONArray();
        jParas.put(dbId);// 帐套Id
        jParas.put(user);// 用户名
        jParas.put(pwd);// 密码
        jParas.put(lang);// 语言

        try {
            HttpURLConnection connection = InitURLConn(sUrl, jParas);

            if (connection.getResponseCode() == 200) {

                // 获取Cookie
                for (String key1 : connection.getHeaderFields().keySet()) {
                    if (key1 != null && key1.equalsIgnoreCase("Set-Cookie")) {
                        String tempCookieVal = connection.getHeaderField(key1);
                        if (tempCookieVal.startsWith("kdservice-sessionid")) {
                            COOKIE_VALUE = tempCookieVal;
                            break;
                        }
                    }
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null && line.contains("\"LoginResultType\":1")) {
                    bResult = true;
                    break;
                }

                if (bResult) {
                    msgResult.what = LOGIN_SUCCESS;
                } else {
                    msgResult.what = LOGIN_FAIL;
                }

                reader.close();
            } else {
                msgResult.what = CONNECTION_ERROR;
            }

            connection.disconnect();
        }
        catch (Exception ex){
            msgResult.obj=ex.getMessage();
            msgResult.what = EXCEPTION;
        }

        return msgResult;
    }

    private static Message Invoke(String deal, String formId, String content)
            throws Exception {
        String sUrl = KINGDEE_METHOD_MAP.get(deal).toString();
        Message msgResult = new Message();

        JSONArray jParas = new JSONArray();
        jParas.put(formId);
        jParas.put(content);

        HttpURLConnection connectionInvoke = InitURLConn(sUrl, jParas);

        if (connectionInvoke.getResponseCode() == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connectionInvoke.getInputStream()));

            String line;
            String sResult = "";

            while ((line = reader.readLine()) != null) {
                sResult += new String(line.getBytes(), "utf-8");
            }

            if (sResult.length() > 0) {
                msgResult.obj = sResult;
                msgResult.what = INVOKE_SUCCESS;
            } else {
                msgResult.what = INVOKE_NULL;
            }

            reader.close();
        } else {
            msgResult.what = CONNECTION_ERROR;
        }

        connectionInvoke.disconnect();
        return msgResult;
    }

    // Save
    public static Message Save(String formId, String content) throws Exception {
        return Invoke("Save", formId, content);
    }

    // View
    public static Message View(String formId, String content) throws Exception {
        return Invoke("View", formId, content);
    }

    // Submit
    public static Message Submit(String formId, String content) throws Exception {
        return Invoke("Submit", formId, content);
    }

    // Audit
    public static Message Audit(String formId, String content) throws Exception {
        return Invoke("Audit", formId, content);
    }

    // UnAudit
    public static Message UnAudit(String formId, String content) throws Exception {
        return Invoke("UnAudit", formId, content);
    }

    // StatusConvert
    public static Message StatusConvert(String formId, String content) throws Exception {
        return Invoke("StatusConvert", formId, content);
    }
}
