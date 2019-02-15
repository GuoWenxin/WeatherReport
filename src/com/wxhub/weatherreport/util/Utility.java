package com.wxhub.weatherreport.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.json.JSONObject;

import android.R.string;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.wxhub.weatherreport.db.CoolWeatherDB;
import com.wxhub.weatherreport.model.City;
import com.wxhub.weatherreport.model.County;
import com.wxhub.weatherreport.model.Province;

public class Utility {

	/*
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handlerProvincesResponse(CoolWeatherDB coolWeatherDB,String response)
	{
		if (!TextUtils.isEmpty(response)) {
			String [] allProvinceStrings=response.split(",");
			if (allProvinceStrings!=null && allProvinceStrings.length>0) {
				for (String p:allProvinceStrings) {
					String [] arrayString=p.split("\\|");
					Province province=new Province();
					province.setProvinceCode(arrayString[0]);
					province.setProvinceName(arrayString[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	/*
	 * 解析和处理服务器返回的市级数据
	 */
	public synchronized static boolean handlerCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int proviceId)
	{
		if (!TextUtils.isEmpty(response)) {
			String [] allCities=response.split(",");
			if (allCities!=null && allCities.length>0) {
				for (String p:allCities) {
					String [] arrayString=p.split("\\|");
					City city=new City();
					city.setCityCode(arrayString[0]);
					city.setCityName(arrayString[1]);
					city.setProvinceId(proviceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	/*
	 * 解析和处理服务器返回的县级数据
	 */
	public synchronized static boolean handlerCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId)
	{
		if (!TextUtils.isEmpty(response)) {
			String [] allConties=response.split(",");
			if (allConties!=null && allConties.length>0) {
				for (String p:allConties) {
					String [] arrayString=p.split("\\|");
					County county=new County();
					county.setCountyCode(arrayString[0]);
					county.setCountyName(arrayString[1]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	public static void handlerWeatherResponse(Context context,String response)
	{
		try {
			JSONObject jsonObject=new JSONObject(response);
			JSONObject weatherinfo=jsonObject.getJSONObject("weatherinfo");
			String cityName=weatherinfo.getString("city");
			String weatherCodeString=weatherinfo.getString("cityid");
			String temp1=weatherinfo.getString("temp1");
			String temp2=weatherinfo.getString("temp2");
			String weatherDesp=weatherinfo.getString("weather");
			String publishTimeString=weatherinfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCodeString, temp1, temp2, weatherDesp, publishTimeString);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
}
