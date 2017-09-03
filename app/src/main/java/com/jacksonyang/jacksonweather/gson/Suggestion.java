package com.jacksonyang.jacksonweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 35390 on 2017/9/2.
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    public class Comfort{

        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String info;
    }

    @SerializedName("cw")
    public CarWash carWash;

    public class CarWash{
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String info;
    }

    @SerializedName("drsg")
    public Dress dress;

    public class Dress{
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String info;
    }

    public Flu flu;
    public class Flu{
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String info;
    }

    public Sport sport;
    public class Sport{
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String info;
    }

    @SerializedName("trav")
    public Travel travel;

    public class Travel{
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String info;
    }

    @SerializedName("uv")
    public UltraViolate ultraViolate;

    public class UltraViolate{
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String info;
    }
}
