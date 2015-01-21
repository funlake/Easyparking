package com.easyparking.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings.Secure;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Helper {
	private static final int MODE_PRIVATE = 1;
	private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");

	public static boolean isFirstRun(Context c) {
		SharedPreferences sharedPreferences = c.getSharedPreferences("setting",
				MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		boolean isFirstAccess = sharedPreferences
				.getBoolean("isFirstRun", true);
		if (isFirstAccess) {
			Log.i("debug", "第一次运行" + getUid(c));
			editor.putBoolean("isFirstRun", false);
			editor.commit();

		} else {
			Log.i("debug", "不是第一次运行" + getUid(c));
		}
		return isFirstAccess;
	}

	public static String getUid(Context c) {
		return Helper.getSetting(c, "userid");
	}

	public static void saveSetting(Context c, String k, String v) {
		SharedPreferences sharedPreferences = c.getSharedPreferences("setting",
				MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(k, v);
		editor.commit();
	}

	public static void saveSetting(Context c, String k, Float v) {
		SharedPreferences sharedPreferences = c.getSharedPreferences("setting",
				MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putFloat(k, v);
		editor.commit();
	}

	public static String getSetting(Context c, String k) {
		SharedPreferences sharedPreferences = c.getSharedPreferences("setting",
				MODE_PRIVATE);
		return sharedPreferences.getString(k, "");
	}

	public static String getDeviceId(Context c) {
		String uid;
		TelephonyManager tm = (TelephonyManager) c
				.getSystemService(Context.TELEPHONY_SERVICE);
		uid = tm.getDeviceId();
		if (uid == null) {
			uid = Secure.getString(c.getContentResolver(), Secure.ANDROID_ID);
		}
		return uid;
	}

	public static String showDateTime(String timeValue) {
		String tv[] = timeValue.split(" ");
		String d = tv[0];
		Calendar today = Calendar.getInstance();
		// today
		if (d.equals(DATE_FORMAT.format(today.getTime()))) {
			return "今日 " + tv[1];
		}
		// yesterday
		today.add(Calendar.DAY_OF_MONTH, -1);
		Date yesterday = today.getTime();
		if (d.equals(DATE_FORMAT.format(yesterday))) {
			return "昨日 " + tv[1];
		}
		// the day before yesterday
		today.add(Calendar.DAY_OF_MONTH, -1);
		Date daybeforeyes = today.getTime();
		if (d.equals(DATE_FORMAT.format(daybeforeyes))) {
			return "前日 " + tv[1];
		}

		Calendar today2 = Calendar.getInstance();
		// tomorrow
		today2.add(Calendar.DAY_OF_MONTH, 1);
		Date tomorrow = today2.getTime();
		if (d.equals(DATE_FORMAT.format(tomorrow))) {
			return "明日 " + tv[1];
		}
		String ds[] = tv[0].split("-");
		// same year
		if (ds[0].equals(YEAR_FORMAT.format(tomorrow))) {
			return ds[1] + "." + ds[2] + " " + tv[1];
		} else {
			return ds[0] + "." + ds[1] + "." + ds[2] + " " + tv[1];
		}
		// return timeValue;
	}

	public static void sendSMS(String phoneNumber, String message) {
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, null, null);
	}

	public static final Map<Integer, Integer> MetersOption = new TreeMap<Integer, Integer>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(1, 1000);
			put(2, 2000);
			put(3, 3000);
			put(4, 4000);
			put(5, 5000);
		}
	};

	public static ArrayList<String> getMetersOption() {
		final ArrayList<String> mp = new ArrayList<String>();
		Iterator<?> iter = MetersOption.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			mp.add(key.toString() + "公里内");
		}
		return mp;
	}
}
