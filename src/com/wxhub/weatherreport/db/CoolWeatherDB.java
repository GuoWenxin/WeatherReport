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
	 * ���ݿ���
	 */
	public static final String DB_NAME="cool_weather";
	/*
	 * ���ݿ�汾
	 */
	public static final int VERSION=1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;
	/*
	 * �����캯��˽�л�
	 */
	private CoolWeatherDB(Context context)
	{
		CoolWeatherOpenHelper dbhelp=new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db=dbhelp.getWritableDatabase();
	}
	/*
	 * ��ȡcoolWeatherDB��ʵ��
	 */
	public synchronized static CoolWeatherDB getInstance(Context context)
	{
		if (coolWeatherDB==null) {
			coolWeatherDB=new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	/*
	 * ��Provinceʵ���洢�����ݿ�
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
	 * �����ݿ��ȡȫ������ʡ����Ϣ
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
	 * ��cityʵ���洢�����ݿ�
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
	 * �����ݿ��ȡĳʡ�����еĳ�����Ϣ
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
	 * ��Countyʵ���洢�����ݿ�
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
	 * �����ݿ��ж�ȡѡ�еĳ����µ��ص�����
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