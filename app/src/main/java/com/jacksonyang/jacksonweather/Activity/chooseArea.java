package com.jacksonyang.jacksonweather.Activity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.jacksonyang.jacksonweather.R;

public class chooseArea extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);

        //判断是否有网络
        ConnectivityManager connectivityManager=(ConnectivityManager) chooseArea.this.getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null){
            Toast.makeText(chooseArea.this,"没有网络连接",Toast.LENGTH_SHORT).show();
        }
    }
}
