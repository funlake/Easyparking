package com.easyparking.helper;

import java.util.Timer;

import android.support.v4.view.ViewPager;

import com.amap.api.maps2d.model.LatLng;
import com.easyparking.MainActivity;
import com.easyparking.MyapplyActivity;
import com.easyparking.MyspotActivity;
import com.easyparking.ProfilerActivity;

public class Config {
	//local api
	public static final String Server_URL = "http://192.168.3.3:9527/";
	//official api
	//public static final String Server_URL = "http://115.28.72.145:9527/";
	//address
	public static final LatLng BEIJING = new LatLng(39.90403, 116.407525);// 北京市经纬度
	public static final LatLng ZHONGGUANCUN = new LatLng(39.983456, 116.3154950);// 北京市中关村经纬度
	public static final LatLng SHANGHAI = new LatLng(31.238068, 121.501654);// 上海市经纬度
	public static final LatLng FANGHENG = new LatLng(39.989614, 116.481763);// 方恒国际中心经纬度
	public static final LatLng CHENGDU = new LatLng(30.679879, 104.064855);// 成都市经纬度
	public static final LatLng XIAN = new LatLng(34.341568, 108.940174);// 西安市经纬度
	public static final LatLng ZHENGZHOU = new LatLng(34.7466, 113.625367);// 郑州市经纬度
	public static final LatLng GUANGZHOU = new LatLng(23.107172,113.324295);//广州市坐标
	
	public static ViewPager v1;
	public static ViewPager v2;
	public static MainActivity core;
	public static MyapplyActivity myapply;
	public static MyspotActivity myspot;
	public static ProfilerActivity profile;
	public static LatLng spotPos;
	
	public static Timer timer = new Timer();
	public static boolean scheduleStatus;

	
	public static void setSpotPos(LatLng p){
		spotPos = p;
	}
	
	public static LatLng getSpotPos(){
		return spotPos;
	}
}