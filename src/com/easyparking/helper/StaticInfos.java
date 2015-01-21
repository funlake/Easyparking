package com.easyparking.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TreeMap;

import android.support.v4.view.ViewPager;

import com.amap.api.maps2d.model.LatLng;
import com.easyparking.BaseActivity;
import com.easyparking.MyapplyActivity;
import com.easyparking.MyspotActivity;
import com.easyparking.ProfilerActivity;

public class StaticInfos {

	public static final String Server_URL = "http://192.168.0.103:9527/";
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
	public static BaseActivity core;
	public static MyapplyActivity myapply;
	public static MyspotActivity myspot;
	public static ProfilerActivity profile;
	public static LatLng spotPos;
	
	public static Timer timer = new Timer();
	public static boolean scheduleStatus;
	public static final Map<Integer,Integer> MetersOption = new TreeMap<Integer,Integer>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(1,1000);
			put(2,2000);
			put(3,3000);
			put(4,4000);
			put(5,5000);
		}
	}; 
	public static ArrayList<String> getMetersOption(){
		final ArrayList<String> mp = new ArrayList<String>();
		Iterator<?> iter = MetersOption.entrySet().iterator();  
		while (iter.hasNext()) {  
		    Map.Entry entry = (Map.Entry) iter.next();  
		    Object key = entry.getKey();  
		    mp.add(key.toString()+"公里内"); 
		}  
		return mp;
	}
	
	public static void setSpotPos(LatLng p){
		spotPos = p;
	}
	
	public static LatLng getSpotPos(){
		return spotPos;
	}
}