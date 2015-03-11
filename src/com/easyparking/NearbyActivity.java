package com.easyparking;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.easyparking.helper.Helper;
import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;
import com.easyparking.helper.ShakeDetector;
import com.easyparking.helper.ShakeDetector.OnShakeListener;

@SuppressLint("InlinedApi")
public class NearbyActivity extends BaseActivity implements
		AMapLocationListener,OnMarkerClickListener {
	private ShakeDetector mShakeDetector;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Button gotomypage;
	private LocationManagerProxy mLocationProxy;
	private ListView nearbylist;
	private Double geoLat;
	private Double geoLng;
	private MapView mapView;
	private AMap aMap;

	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearby);
		initShake();
		initMap(savedInstanceState);
		initViews();
		showInfoMessage("摇一摇，查看附近车位");
		// showErrorMessage(Helper.getDeviceId(ShakeActivity.this));
		// Window window = this.getWindow();
		// window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		// window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// window.setStatusBarColor(this.getResources().getColor(R.color.Mainbg));
	}

	private void initLocation() {
		// TODO Auto-generated method stub
		showProgress("正在定位。。。");
		mLocationProxy = LocationManagerProxy.getInstance(this);
		mLocationProxy.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 2000, 10, this);
//		mLocationProxy.requestLocationUpdates(
//				LocationManagerProxy.GPS_PROVIDER, 2000, 10, this);

	}

	private void initShake() {
		// TODO Auto-generated method stub
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mShakeDetector = new ShakeDetector(new OnShakeListener() {
			@Override
			public void onShake() {
				// Do stuff!
				searchPos();
			}
		});
	}

	private void searchPos() {
		aMap.clear();
		initLocation();
	}

	/**
	 * 初始化AMap对象
	 */
	private void initMap(Bundle savedInstanceState) {
		mapView = (MapView) findViewById(R.id.nearbymap);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		if (aMap == null) {
			aMap = mapView.getMap();
		}
	}

	private void initMarker(LatLng ll) {
		aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.position(ll).draggable(true).period(50)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
		aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 18));
	}

	private void initViews() {
		// nearbylist = (ListView) findViewById(R.id.nearbylist);
		// TODO Auto-generated method stub
		// searchPos = (Button) findViewById(R.id.search_pos);
		//
		// searchPos.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// searchPos();
		// }
		// });
		gotomypage = (Button) findViewById(R.id.gotomypage);

		gotomypage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				Intent intent = new Intent(NearbyActivity.this,
						MainActivity.class);
				startActivityForResult(intent, 1011);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});
		searchPos();
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		// TODO Auto-generated method stub
		if (location != null) {
			geoLat = location.getLatitude();
			geoLng = location.getLongitude();
			initMarker(new LatLng(geoLat, geoLng));
			showProgress("搜索车位中...");
			new HttpRequest(new HttpCallback() {

				@Override
				public void run(String res) {
					// TODO Auto-generated method stub
					try {
						JSONObject r = new JSONObject(res);
						if ("success".equals(r.getString("code"))) {
							listdata = r.getJSONArray("result");
							if (listdata.length() > 0) {
								// nearbylist.setAdapter(new NearbyAdapter(
								// NearbyActivity.this));
								JSONObject row;
								int len = listdata.length();

								for (int i = 0; i < len; i++) {
									row = listdata.getJSONObject(i);
									JSONObject loc = row.getJSONObject("loc");

									aMap.addMarker(new MarkerOptions()
											.anchor(0.5f, 0.5f)
											.position(
													new LatLng(
															loc.getDouble("latitude"),
															loc.getDouble("longitude")))
											// .draggable(true)
											// .period(50)
											.icon(BitmapDescriptorFactory
													.fromResource(R.drawable.marker2))

									);

								}

							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						showErrorMessage("错误:" + e.getMessage() + ",请检查您的网络连接");
					}
				}

			}).execute("/spot_regular_find/" + geoLng + "/" + geoLat + "/"
					+ Helper.getDeviceId(NearbyActivity.this), "GET", "");
			// String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
			// + "\n精    度    :" + location.getAccuracy() + "米"
			// + "\n定位方式:" + location.getProvider() + "\n定位时间:" + AMapUtil
			// .convertToTime(location.getTime()));
			// showSuccessMessage(str);
			if (mLocationProxy != null) {
				mLocationProxy.removeUpdates(this);
				mLocationProxy.destory();
			}
			mLocationProxy = null;
			hideProgress();
		}
	}
	@Override
	public boolean onMarkerClick(Marker marker) {
		return false;
	}
	public int getDistance(double lng1, double lat1, double lng2, double lat2) {

		double PI = Math.PI;
		double R = 6371229;
		double x, y, distance;

		x = (lng2 - lng1) * PI * R * Math.cos(((lat1 + lat2) / 2) * PI / 180)
				/ 180;

		y = (lat2 - lat1) * PI * R / 180;

		distance = Math.hypot(x, y);

		return (int) distance;

	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(mShakeDetector, mAccelerometer,
				SensorManager.SENSOR_DELAY_UI);
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mShakeDetector);
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
}
