package com.jacksonyang.jacksonweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 35390 on 2017/9/2.
 */

public class AQI {
    public AQICity city;

    public class AQICity{
        public String pm25;

        @SerializedName("qlty")
        public String qualitity;
    }
}
