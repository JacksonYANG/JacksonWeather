package com.jacksonyang.jacksonweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 35390 on 2017/9/2.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
