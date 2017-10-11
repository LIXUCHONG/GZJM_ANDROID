package com.jimei.k3wise_mobile.Util;

import android.content.Context;

import com.jimei.k3wise_mobile.BuildConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lee on 2016/9/5.
 */
public class CommonHelper {
    public static long lastClickTime = 1 * 1000;

    //中文转换Unicode
    public static String chineseToUnicode(String str) {
        String result = "";

        for (char chr : str.toCharArray()) {
            int chr_i = chr;
            if (chr_i >= 19968 && chr_i <= 171941) {// 汉字范围 \u4e00-\u9fa5 (中文)
                result += "\\u" + Integer.toHexString(chr_i);
            } else {
                result += chr;
            }
        }

        return result;
    }

    public static int charToInt(byte ch)
    {
        int val = 0;
        if (ch >= 0x30 && ch <= 0x39)
        {
            val = ch - 0x30;
        }
        else if (ch >= 0x41 && ch <= 0x46)
        {
            val = ch - 0x41 + 10;
        }
        return val;
    }

    //获取版本号
    public static int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    //获取版本号
    public static String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    //是否过快重复点击
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        return timeD <= 1000;
    }

    //将对象实例反射为map
    public static Map<String, Object> getObjMap(Object obj) {

        Map<String, Object> map = new HashMap<String,Object>();
        // System.out.println(obj.getClass());
        // 获取f对象对应类中的所有属性域
        Field[] fields = obj.getClass().getFields();
        for (int i = 0, len = fields.length; i < len; i++) {
            String varName = fields[i].getName();
            try {
                // 获取原来的访问控制权限
                boolean accessFlag = fields[i].isAccessible();
                // 修改访问控制权限
                fields[i].setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object o = fields[i].get(obj);
                if (o != null)
                    map.put(varName, o);
                // System.out.println("传入的对象中包含一个如下的变量：" + varName + " = " + o);
                // 恢复访问控制权限
                fields[i].setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        return map;
    }

    public static Object deepClone (Object obj) throws Exception {
        //将对象写到流里
        ByteArrayOutputStream bo=new ByteArrayOutputStream();
        ObjectOutputStream oo=new ObjectOutputStream(bo);
        oo.writeObject(obj);
        //从流里读出来
        ByteArrayInputStream bi=new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi=new ObjectInputStream(bi);
        return(oi.readObject());
    }

    public static String getServiceAddress(Context context){
        return String.format("%s:%s",PreferencesHelper.Get(context,"http_connect_ip"),PreferencesHelper.Get(context,"http_connect_port"));
    }
}
