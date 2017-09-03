package com.jacksonyang.jacksonweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 35390 on 2017/9/2.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public NowWeather nowWeather;

    public class NowWeather{

        @SerializedName("txt")
        public String info;
    }

    @SerializedName("wind")
    public Wind wind;

    public class Wind{
        @SerializedName("dir")
        public String direct;

    }
}
