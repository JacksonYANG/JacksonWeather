package com.jacksonyang.jacksonweather.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.jacksonyang.jacksonweather.R;

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
}
