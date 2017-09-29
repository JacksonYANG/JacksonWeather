package com.jacksonyang.jacksonweather.Base;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import javax.security.auth.callback.PasswordCallback;

/**
 * 获取危险权限
 * 获取相关危险权限如READ_PHONE,CAMERA,WRITE_EXTERNAL_STORAGE,ACCESS_FINE_LOCATION
 */

public class GetPermission {

    private final static String TAG="GetPermission";//调试信息

    private final static int READ_PHONE_STATE_CODE=1;
    private final static int CAMERA_CODE=2;
    private final static int WRITE_EXTERNAL_STORAGE_CODE=3;
    private final static int ACCESS_FINE_LOCATION_CODE=4;
    private final static int REQUEST_OPEN_SETTING_CODE=5;

    //创建权限名称列表
    private PermissionModel[] permissionModels=new PermissionModel[]{
            new PermissionModel("电话", Manifest.permission.READ_PHONE_STATE,"我们需要读取手机信息来标识您的身份",READ_PHONE_STATE_CODE),
            new PermissionModel("存储空间",Manifest.permission.WRITE_EXTERNAL_STORAGE,"我们需要您的存储空间以方便我们更好的为您提供天气信息",WRITE_EXTERNAL_STORAGE_CODE),
            new PermissionModel("相机",Manifest.permission.CAMERA,"我们需要使用您的相机以便您上传您的图像",CAMERA_CODE),
            new PermissionModel("位置",Manifest.permission.ACCESS_FINE_LOCATION,"我们需要获取您的位置信息以便于为您提供更优质的服务",ACCESS_FINE_LOCATION_CODE)
    };

    //创建一个活动
    private Activity activity;

    //构造函数
    public GetPermission(Activity activity) {
        this.activity = activity;
    }

    //权限监听
    private onApplyPermissionListener permissionListener;

    public void setApplyPermissionListener(onApplyPermissionListener applyPermissionListener) {
        permissionListener = applyPermissionListener;
    }


    //先构建一个权限名称类
    private static class PermissionModel{
        //权限名称
        public String name;

        //权限
        public String permission;

        //解释
        public String explain;

        //请求码
        public int requestCode;

        public PermissionModel(String name, String permission, String explain,int requestCode) {
            this.name = name;
            this.permission = permission;
            this.explain = explain;
            this.requestCode=requestCode;
        }
    }

    //在Android6.0+上申请权限
    public void applyPermission(){
        try{
            for(final PermissionModel permissionModel:permissionModels){
                if(PackageManager.PERMISSION_GRANTED!= ContextCompat.checkSelfPermission(activity,permissionModel.permission)){
                    ActivityCompat.requestPermissions(activity,new String[]{permissionModel.permission},permissionModel.requestCode);
                    return;
                }
            }
            //获取全部信息后，直接调用全部申请权限已完成接口
            if(permissionListener!=null){
                permissionListener.onAfterApplyAllPermission();
            }
        } catch (Throwable e){
            Log.e(TAG,"",e);
        }
    }

    //重写onRequestPermissionResult
    public void onRequestPermissionResult(int requestCode, String[] permissions, final int[] grantResults){
        switch (requestCode){
            case READ_PHONE_STATE_CODE:
            case WRITE_EXTERNAL_STORAGE_CODE:
                //如果拒绝，进入二次请求
                if(PackageManager.PERMISSION_GRANTED!=grantResults[0]){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(activity,permissions[0])){
                        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                        builder.setTitle("权限申请").setMessage(findPermissionExplain(permissions[0]));
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                applyPermission();
                            }
                        });
                        builder.setCancelable(false);
                        builder.show();
                    }
                    else{
                        //这里是三次或以上请求，直接引导进入系统界面进行设定
                        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                        builder.setTitle("权限申请").setMessage("请在打开的窗口中开启"+findPermissionName(permissions[0])+"权限,以便正常使用本应用");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openSettings(REQUEST_OPEN_SETTING_CODE);

                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.finish();
                            }
                        });
                        builder.setCancelable(false);
                        builder.show();
                    }
                    return;
                }
                //此时用户已经同意本次权限使用
                if(isAllPermissionGranted()){
                    if(permissionListener!=null){
                        permissionListener.onAfterApplyAllPermission();
                    }
                } else{
                    applyPermission();
                }
                break;
        }
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case REQUEST_OPEN_SETTING_CODE:
                if(isAllPermissionGranted()){
                    if(permissionListener!=null){
                        permissionListener.onAfterApplyAllPermission();
                    }
                } else{
                    activity.finish();
                }
                break;
        }
    }

    //检查是否所有权限都已经获取到
    public boolean isAllPermissionGranted(){
        for(PermissionModel permissionModel:permissionModels){
            if(PackageManager.PERMISSION_GRANTED!=ActivityCompat.checkSelfPermission(activity,permissionModel.permission)){
                return false;
            }
        }
        return true;
    }

    //查找权限
    private String findPermissionExplain(String permission){
        if(permissionModels!=null){
            for(PermissionModel permissionModel:permissionModels){
                if(permissionModel!=null&&permissionModel.permission!=null&&permissionModel.permission.equals(permission)){
                    return permissionModel.explain;
                }
            }
        }
        return null;
    }

    //查找权限名称
    private String findPermissionName(String permission){
        if(permissionModels!=null){
            for(PermissionModel permissionModel:permissionModels){
                if(permissionModel!=null&&permissionModel.permission!=null&&permissionModel.permission.equals(permission)){
                    return permissionModel.name;
                }
            }
        }
        return null;
    }

    private boolean openSettings(int requestCode){
        try{
            Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+activity.getPackageName()));
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            activity.startActivityForResult(intent,requestCode);
            return true;
        } catch (Throwable e){
            Log.e(TAG,"",e);
        }
        return false;
    }

    //构建一个接口已实现当所有权限申请完毕后的逻辑回调
    public interface onApplyPermissionListener{

        //所有权限申请完毕，在下面函数中回调
        void onAfterApplyAllPermission();
    }
}
