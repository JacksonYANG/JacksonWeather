package com.jacksonyang.jacksonweather.Base;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bumptech.glide.util.LruCache;

/**
 * Created by 35390 on 2017/9/25.
 */

public class loadPicture {

    private LruCache<String,Bitmap> memoryCache;//设置内存缓存图片

    public LruCache<String, Bitmap> getMemoryCache() {
        return memoryCache;
    }

    public void setMemoryCache(LruCache<String, Bitmap> memoryCache) {
        this.memoryCache = memoryCache;
    }

    //压缩过大的文件,调用calculateBitmap压缩
    public static Bitmap decodeBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight){
        //先将inJustDecodeBounds设置为true获取图片的大小
        final BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeResource(res,resId,options);
        options.inSampleSize=calculateBitmap(options,reqWidth,reqHeight);
        //使用inSampleSize再次获取图片
        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeResource(res,resId,options);

    }

    //定义图片的长宽高
    public static int calculateBitmap(BitmapFactory.Options options,int reqWidth,int reqHeight){
        //源图片长和高
        final int height=options.outHeight;
        final int width=options.outWidth;
        int inSampleSize=1;
        if(height>reqHeight||width>reqWidth){
            //计算出实际长和高
            final int heightRatio=Math.round((float) height/(float) reqHeight);
            final int widthRatio=Math.round((float) width/(float) reqWidth);
            if(heightRatio<widthRatio){
                inSampleSize=heightRatio;
            } else{
                inSampleSize=widthRatio;
            }
        }
        return inSampleSize;
    }

    //内存缓存技术，添加图片至缓存中
    public void addBitmapInMemory(String key,Bitmap bitmap){
        if(getBitmapFromMemory(key)==null){
            memoryCache.put(key,bitmap);
        }
    }

    //从缓存中获取Bitmap
    public Bitmap getBitmapFromMemory(String key){
        return memoryCache.get(key);
    }
}
