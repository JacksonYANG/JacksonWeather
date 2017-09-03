package com.jacksonyang.jacksonweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 35390 on 2017/9/2.
 */

public class DailyForcast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    public class Temperature{
        public String max;
        public String min;
    }

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt_d")
        public String dayWeather;

        @SerializedName("txt_n")
        public String nightWeather;
    }
}
