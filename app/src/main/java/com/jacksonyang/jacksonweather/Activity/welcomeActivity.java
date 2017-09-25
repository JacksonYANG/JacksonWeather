package com.jacksonyang.jacksonweather.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jacksonyang.jacksonweather.R;
import com.jacksonyang.jacksonweather.Web.HttpUtil;
import static com.jacksonyang.jacksonweather.Base.loadPicture.*;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class welcomeActivity extends AppCompatActivity {
    private ThreadDelay threadDelay;
    private boolean isrun=false;
    private ImageView theBingPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        theBingPicture=(ImageView) findViewById(R.id.the_bing_picture);
        isrun=true;
        threadDelay=new ThreadDelay();
        threadDelay.start();
    }

    private class ThreadDelay extends Thread{
        @Override
        public void run() {
            if(isrun){
                try{
                    Thread.sleep(1500);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            handler.sendEmptyMessage(1);
        }
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    isrun=false;
                    threadDelay.interrupt();
                    threadDelay=null;
                    Intent intent=new Intent(welcomeActivity.this,chooseArea.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    //加载必应图片
    public void loadPicture(){
        String requestBingPicture="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPicture, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingpicture=response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(welcomeActivity.this).load(bingpicture).into(theBingPicture);
                    }
                });
            }
        });
    }
}
