package com.jacksonyang.jacksonweather.Activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jacksonyang.jacksonweather.R;
import com.jacksonyang.jacksonweather.Web.HttpUtil;
import com.jacksonyang.jacksonweather.Web.JsonCommand;
import com.jacksonyang.jacksonweather.gson.DailyForcast;
import com.jacksonyang.jacksonweather.gson.Weather;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ShowWeather extends AppCompatActivity {

    private ImageView bingPicture;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView windDirectText;
    private TextView qualityText;
    private TextView pm25Text;
    private TextView comfortBrief;
    private TextView comfortText;
    private TextView carwashBrief;
    private TextView carwashText;
    private TextView dressBrief;
    private TextView dressText;
    private TextView fluBrief;
    private TextView fluText;
    private TextView sportBrief;
    private TextView sportText;
    private TextView travelBrief;
    private TextView travelText;
    private TextView uvBrief;
    private TextView uvText;
    private LinearLayout forecastLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_weather);

        //设置背景图片
        bingPicture=(ImageView) findViewById(R.id.bing_picture);
        String bingpic=sharedPreferences.getString("bing_picture",null);
        if(bingpic!=null){
            Glide.with(this).load(bingpic).into(bingPicture);
        } else{
            loadBingPicture();
        }

        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info);
        windDirectText=(TextView) findViewById(R.id.wind_direct);
        qualityText = (TextView) findViewById(R.id.quality_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortBrief = (TextView) findViewById(R.id.comfort_brief);
        comfortText =(TextView) findViewById(R.id.comfort_txt);
        carwashBrief=(TextView) findViewById(R.id.carwash_brief);
        carwashText=(TextView) findViewById(R.id.carwash_text);
        dressBrief=(TextView) findViewById(R.id.dress_brief);
        dressText=(TextView) findViewById(R.id.dress_text);
        fluBrief=(TextView) findViewById(R.id.flu_brief);
        fluText=(TextView) findViewById(R.id.flu_text);
        sportBrief=(TextView) findViewById(R.id.sport_brief);
        sportText=(TextView) findViewById(R.id.sport_text);
        travelBrief=(TextView) findViewById(R.id.travel_brief);
        travelText=(TextView) findViewById(R.id.travel_text);
        uvBrief=(TextView) findViewById(R.id.uv_brief);
        uvText=(TextView) findViewById(R.id.uv_text);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String weatherString = sharedPreferences.getString("weather", null);
        if (weatherString != null) {
            //有缓存到本地
            Weather weather = JsonCommand.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            //无缓存
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }

    //本地没有缓存天气数据，从服务器查询天气数据
    public void requestWeather(final String weatherId){
        String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=84d408fda3d2496b8a817937a488de88";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShowWeather.this,"获取天气信息失败，请检查网络连接",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=JsonCommand.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(ShowWeather.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else{
                            Toast.makeText(ShowWeather.this,"获取天气信息失败，请检查网络连接",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        loadBingPicture();
    }

    //处理本地缓存的天气数据并且展示
    public void showWeatherInfo(Weather weather){
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime.split(" ")[1];
        String degree=weather.now.temperature+"C";
        String weatherInfo=weather.now.nowWeather.info;
        String windDirect=weather.now.wind.direct;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        windDirectText.setText(windDirect);
        forecastLayout.removeAllViews();
        for(DailyForcast dailyForcast:weather.forcastList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast,forecastLayout,false);
            TextView dateText=(TextView) findViewById(R.id.date_text);
            TextView dayWeatherText=(TextView) findViewById(R.id.dayWeather_text);
            TextView nightWeatherText=(TextView) findViewById(R.id.nightWeather_text);
            TextView minText=(TextView) findViewById(R.id.min_text);
            TextView maxText=(TextView) findViewById(R.id.max_text);
            dateText.setText(dailyForcast.date);
            dayWeatherText.setText(dailyForcast.more.dayWeather);
            nightWeatherText.setText(dailyForcast.more.nightWeather);
            minText.setText(dailyForcast.temperature.min);
            maxText.setText(dailyForcast.temperature.max);
            forecastLayout.addView(view);
        }
        if(weather.aqi!=null){
            pm25Text.setText(weather.aqi.city.pm25);
            qualityText.setText(weather.aqi.city.qualitity);
        }
        String McomfortBrief=weather.suggestion.comfort.brief;
        String McomfortText=weather.suggestion.comfort.info;
        String McarwashBrief=weather.suggestion.carWash.brief;
        String McarwashText=weather.suggestion.carWash.info;
        String MdressBrief=weather.suggestion.dress.brief;
        String MdressText=weather.suggestion.dress.info;
        String MfluBrief=weather.suggestion.flu.brief;
        String MfluText=weather.suggestion.flu.info;
        String MsportBrief=weather.suggestion.sport.brief;
        String MsportText=weather.suggestion.sport.info;
        String MtravelBrief=weather.suggestion.travel.brief;
        String MtravelText=weather.suggestion.travel.info;
        String MuvBrief=weather.suggestion.ultraViolate.brief;
        String MuvText=weather.suggestion.ultraViolate.info;
        comfortBrief.setText(McomfortBrief);
        comfortText.setText(McomfortText);
        carwashBrief.setText(McarwashBrief);
        carwashText.setText(McarwashText);
        dressBrief.setText(MdressBrief);
        dressText.setText(MdressText);
        fluBrief.setText(MfluBrief);
        fluText.setText(MfluText);
        sportBrief.setText(MsportBrief);
        sportText.setText(MsportText);
        travelBrief.setText(MtravelBrief);
        travelText.setText(MtravelText);
        uvBrief.setText(MuvBrief);
        uvText.setText(MuvText);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    //加载必应图片
    public void loadBingPicture(){
        String requestBingPicture="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPicture, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingpicture=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(ShowWeather.this).edit();
                editor.putString("bing_picture",bingpicture);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(ShowWeather.this).load(bingpicture).into(bingPicture);
                    }
                });
            }
        });
    }
}
