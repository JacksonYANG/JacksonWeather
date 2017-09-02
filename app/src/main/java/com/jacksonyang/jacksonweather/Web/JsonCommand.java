package com.jacksonyang.jacksonweather.Web;

import android.text.TextUtils;

import com.jacksonyang.jacksonweather.database.City;
import com.jacksonyang.jacksonweather.database.County;
import com.jacksonyang.jacksonweather.database.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 35390 on 2017/9/2.
 */

//解析并且处理JSON数据
public class JsonCommand {

    //解析省份数据
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvince=new JSONArray(response);
                for(int i=0;i<allProvince.length();i++){
                    JSONObject provinceObject=allProvince.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setId(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析市级数据
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCity=new JSONArray(response);
                for(int i=0;i<allCity.length();i++){
                    JSONObject JSONcity=allCity.getJSONObject(i);
                    City city=new City();
                    city.setCityName(JSONcity.getString("name"));
                    city.setCityCode(JSONcity.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;

            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析县级数据
    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounty=new JSONArray(response);
                for(int i=0;i<allCounty.length();i++){
                    JSONObject countyObject=allCounty.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
