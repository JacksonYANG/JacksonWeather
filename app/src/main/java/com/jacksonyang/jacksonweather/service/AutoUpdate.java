package com.jacksonyang.jacksonweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;

import com.jacksonyang.jacksonweather.Web.HttpUtil;
import com.jacksonyang.jacksonweather.Web.JsonCommand;
import com.jacksonyang.jacksonweather.gson.Weather;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdate extends Service {
    public AutoUpdate() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager=(AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour=3*60*60*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent intent1=new Intent(this,AutoUpdate.class);
        PendingIntent pendingIntent=PendingIntent.getService(this,0,intent1,0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
        return super.onStartCommand(intent,flags,startId);
    }

    //更新天气信息
    private void updateWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        if(weatherString!=null){
            //此时系统里面已经有缓存
            Weather weather= JsonCommand.handleWeatherResponse(weatherString);
            String weatherId=weather.basic.weatherId;

            String weatherUrl="http://guolin.tech/api/weather?cityid=?"+weatherId+"&key=84d408fda3d2496b8a817937a488de88";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText=response.body().string();
                    Weather weather1=JsonCommand.handleWeatherResponse(responseText);
                    if(weather1!=null&&"ok".equals(weather1.status)){
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdate.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    //更新bing图片
    private void updateBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdate.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });
    }
}
