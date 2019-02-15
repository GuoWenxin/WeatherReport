package com.wxhub.weatherreport.db;

import java.util.ArrayList;
import java.util.List;

import com.wxhub.weatherreport.model.City;
import com.wxhub.weatherreport.model.County;
import com.wxhub.weatherreport.model.Province;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.session.PlaybackState.CustomAction;

public class CoolWeatherDB {
	/*
	 * 数据库名
	 */
	public static final String DB_NAME="cool_weather";
	/*
	 * 数据库版本
	 */
	public static final int VERSION=1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;
	/*
	 * 将构造函数私有化
	 */
	private CoolWeatherDB(Context context)
	{
		CoolWeatherOpenHelper dbhelp=new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db=dbhelp.getWritableDatabase();
	}
	/*
	 * 获取coolWeatherDB的实例
	 */
	public synchronized static CoolWeatherDB getInstance(Context context)
	{
		if (coolWeatherDB==null) {
			coolWeatherDB=new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	/*
	 * 将Province实例存储到数据库
	 */
	public void saveProvince(Province province){
		if (province!=null) {
			ContentValues values=new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	/*
	 * 从数据库读取全国所有省份信息
	 */
	public List<Province> loadProvinces()
	{
		List<Province> list=new ArrayList<Province>();
		Cursor cursor=db.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province=new Province();
				province.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		return list;
	}
	/*
	 * 将city实例存储到数据库
	 */
	public void saveCity(City city) {
		// TODO Auto-generated method stub
		if (city!=null) {
			ContentValues values=new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code",city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}

	}
	/*
	 * 从数据库读取某省下所有的城市信息
	 */
	public List<City> loadCitys(int provinceId)
	{
		List<City> list=new ArrayList<City>();
		Cursor cursor=db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city=new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;
	}
	/*
	 * 将County实例存储进数据库
	 */
	public void saveCounty(County county)
	{
		if (county!=null) {
			ContentValues values=new ContentValues();
			
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}
	/*
	 * 从数据库中读取选中的城市下的县的数据
	 */
	public List<County> loadConty(int cityId) {
		List<County>list=new ArrayList<County>();
		Cursor cursor=db.query("County", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county=new County();
				county.setCityId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				list.add(county);
			} while (cursor.moveToNext());
		}
		return list;
	}
}
