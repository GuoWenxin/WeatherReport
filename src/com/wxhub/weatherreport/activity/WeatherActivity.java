package com.wxhub.weatherreport.activity;
import com.wxhub.weatherreport.R;
import com.wxhub.weatherreport.service.AutoUpdateService;
import com.wxhub.weatherreport.util.HttpCallbackListener;
import com.wxhub.weatherreport.util.HttpUtil;
import com.wxhub.weatherreport.util.Utility;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity  implements OnClickListener{

	private Button switchCityButton;
	private Button refreshWeather;
	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDespTextView;
	private TextView temp1TextView;
	private TextView temp2TextView;
	private TextView currentDateTextView;
	@Override
	protected void onCreate(Bundle saveInstance) {
		super.onCreate(saveInstance);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		switchCityButton=(Button)findViewById(R.id.switch_city);
		switchCityButton.setOnClickListener(this);
		refreshWeather=(Button)findViewById(R.id.refresh_weather);
		refreshWeather.setOnClickListener(this);
		weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);
		cityNameText=(TextView)findViewById(R.id.city_name);
		publishText=(TextView)findViewById(R.id.publish_text);
		weatherDespTextView=(TextView)findViewById(R.id.weather_desp);
		temp1TextView=(TextView)findViewById(R.id.temp1);
		temp2TextView=(TextView)findViewById(R.id.temp2);
		currentDateTextView=(TextView)findViewById(R.id.current_date);
		
		String countyCodeString=getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCodeString)) {
			publishText.setText(R.string.syncing);
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCodeString);
		}
		else {
			ShowWeather();
		}
	}
	/*
	 * 查询县级代号对应的天气代号
	 */
	private void queryWeatherCode(String countyCode)
	{
		String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFromServer(address, "countyCode");
	}
	/*
	 * 查询天气代号对应的天气
	 */
	private void queryWeatherInfo(String WeatherCode) {
		String address="http://www.weather.com.cn/data/cityinfo/"+WeatherCode+".html";
		queryFromServer(address, "weatherCode");
	}
	
	private void queryFromServer(final String address,final String type)
	{
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						//从服务器返回的数据中解析出天气代号
						String [] arrayStrings=response.split("\\|");
						if (arrayStrings!=null && arrayStrings.length==2) {
							String weatherCodeString=arrayStrings[1];
							queryWeatherInfo(weatherCodeString);
						}
					}
				}
				else if("weatherCode".equals(type)){
					Utility.handlerWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ShowWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						publishText.setText(R.string.syncfail);
					}
				});
			}
		});
	}
	
	private void ShowWeather()
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1TextView.setText(prefs.getString("temp1", ""));
		temp2TextView.setText(prefs.getString("temp2", ""));
		weatherDespTextView.setText(prefs.getString("weather_desp", ""));
		publishText.setText(getString(R.string.today)+prefs.getString("publish_time", "")+getString(R.string.publish));
		currentDateTextView.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
		Intent intent=new Intent(this,AutoUpdateService.class);
		startService(intent);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent=new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText(R.string.syncing);
			SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCodeString=prefs.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCodeString)) {
				queryWeatherInfo(weatherCodeString);
			}
			break;
		default:
			break;
		}
	}
}
