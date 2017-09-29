package com.jacksonyang.jacksonweather.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jacksonyang.jacksonweather.Base.GetPermission;
import com.jacksonyang.jacksonweather.R;
import com.jacksonyang.jacksonweather.Web.HttpUtil;

import net.youmi.android.AdManager;
import net.youmi.android.nm.sp.SplashViewSettings;
import net.youmi.android.nm.sp.SpotListener;
import net.youmi.android.nm.sp.SpotManager;
import net.youmi.android.nm.sp.SpotRequestListener;
import net.youmi.android.nm.cm.*;

import static com.jacksonyang.jacksonweather.Base.loadPicture.*;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class welcomeActivity extends AppCompatActivity {

    private GetPermission getPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        //Android6.0以上需要设置权限
        getPermission = new GetPermission(this);
        getPermission.setApplyPermissionListener(new GetPermission.onApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {
                Log.i("Permission", "All permission are granted");
                runSplash();
            }
        });
        if (Build.VERSION.SDK_INT <= 23) {
            //低于Android6.0，可以直接运行
            Log.d("Permisson", "The version of the SDK is lower than 6.0,can run at once");
            runSplash();
        } else {
            if (getPermission.isAllPermissionGranted()) {
                Log.d("Permission", "All permissions are granted");
                runSplash();
            } else {
                Log.d("Permission", "Some permissions are not granted yet,apply them!");
                getPermission.applyPermission();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getPermission.onRequestPermissionResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getPermission.onActivityResult(requestCode,resultCode,data);
    }

    //初始化SDK加展示广告
    private void runSplash(){
        AdManager.getInstance(this).init("bdcc263a2d8b6426", "7181905303c8265d", true);
        preLoad();//预加载开屏
        setupSplashAd();
    }

    //预加载广告
    private void preLoad(){
        SpotManager.getInstance(this).requestSpot(new SpotRequestListener() {
            @Override
            public void onRequestSuccess() {
                Log.d("Ad","Advertisement Success!");
            }

            @Override
            public void onRequestFailed(int errorCode) {
                Log.e("Ad","Advertisement Failed!");
                switch (errorCode){
                    case ErrorCode.NON_NETWORK:
                        Toast.makeText(welcomeActivity.this,"没有网络连接",Toast.LENGTH_SHORT).show();
                        break;
                    case ErrorCode.NON_AD:
                        Toast.makeText(welcomeActivity.this,"暂无广告内容",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(welcomeActivity.this,"请稍后再试",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    //设置开屏广告
    private void setupSplashAd(){
        //创建开屏容器
        final RelativeLayout splashLayout=(RelativeLayout) findViewById(R.id.splash_layout);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ABOVE,R.id.view_divider);

        //对开屏广告进行设置
        SplashViewSettings splashViewSettings=new SplashViewSettings();
        splashViewSettings.setAutoJumpToTargetWhenShowFailed(true);
        splashViewSettings.setTargetClass(chooseArea.class);
        splashViewSettings.setSplashViewContainer(splashLayout);

        //展示开屏广告
        SpotManager.getInstance(this).showSplash(this, splashViewSettings, new SpotListener() {
            @Override
            public void onShowSuccess() {
                Log.d("Ad","Advertisement shows successfully!");
            }

            @Override
            public void onShowFailed(int errorCode) {
                Log.e("Ad","Advertisement shows failed!");
                switch (errorCode){
                    case ErrorCode.NON_NETWORK:
                        Toast.makeText(welcomeActivity.this,"没有网络连接",Toast.LENGTH_SHORT).show();
                        break;
                    case ErrorCode.NON_AD:
                        Toast.makeText(welcomeActivity.this,"暂时没有视频广告",Toast.LENGTH_SHORT).show();
                        break;
                    case ErrorCode.RESOURCE_NOT_READY:
                        Toast.makeText(welcomeActivity.this,"开屏资源还没准备好",Toast.LENGTH_SHORT).show();
                        break;
                    case ErrorCode.SHOW_INTERVAL_LIMITED:
                        Toast.makeText(welcomeActivity.this,"开屏展示间隔限制",Toast.LENGTH_SHORT).show();
                        break;
                    case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                        Toast.makeText(welcomeActivity.this,"开屏控件处在不可见状态",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(welcomeActivity.this,"请稍后再试",Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onSpotClosed() {
                Log.d("Spot","Spot is shut down");
            }

            @Override
            public void onSpotClicked(boolean b) {
                Log.d("Spot","The advertisement is clicked");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpotManager.getInstance(this).onDestroy();
    }


}
