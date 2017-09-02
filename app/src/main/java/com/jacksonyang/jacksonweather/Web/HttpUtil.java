package com.jacksonyang.jacksonweather.Web;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by 35390 on 2017/9/2.
 */

//与服务器进行交互
public class HttpUtil {

    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        okHttpClient.newCall(request).enqueue(callback);

    }
}
