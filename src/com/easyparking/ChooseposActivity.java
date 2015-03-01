package com.easyparking;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.easyparking.helper.Helper;
import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;
import com.easyparking.helper.Config;
//import com.amapv1.apis.util.Constants;
//定位所需类库
//假如用到位置提醒功能，需要import该类
//如果使用地理围栏功能，需要import如下类

public class ChooseposActivity extends BaseActivity implements LocationSource,
		AMapLocationListener, OnMapClickListener, OnCameraChangeListener,
		OnGeocodeSearchListener, OnMarkerClickListener {
	public MapView mMapView;
	private AMap aMap;
	private UiSettings mUiSettings;
	private LocationManagerProxy mAMapLocationManager;
	private OnLocationChangedListener mListener;
	private LatLng curpos;
	private float ZoomIndex = 14;
	private LinearLayout options;
	private Spinner meterspinner;
	private double radius = 1000;
	private TextView timeFrom;
	// time from
	private int fromHour;
	private int fromMinute;
	private TextView timeTo;
	// time to
	private int toHour;
	private int toMinute;
	static final int TIME_FROM = 0;
	static final int TIME_TO = 1;
	private TextView parkingTitle;
	private boolean status;// true - 已经在申请,不能再移动视角;false-可申请
	// timer
	// private Timer timer = new Timer();
	static final int TIMER_TIMEOUT = 1000;// 1s 后开始执行timer schedule
	static final int TIMER_LOOP = 1000 * 10;// 10 seconds 一次轮回执行timer
											// schedule,因为要执行http请求
	// private boolean StaticInfos.scheduleStatus = true;
	private HashMap<String,Boolean> spotMarkers = new HashMap<String,Boolean>();
	private OnItemSelectedListener meterItemClick = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			Integer key = Integer.parseInt(meterspinner.getSelectedItem()
					.toString().replaceAll("[^\\d]", ""));
			radius = Helper.MetersOption.get(key);
			if (null != curpos) {
				// Toast.makeText(ChooseposActivity.this,"hehe",
				// Toast.LENGTH_SHORT).show();
				onMapClick(curpos);
			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	};
	// private String postParams; // post parametersapply parking slot
	HashMap<String, Dialog> cfms = new HashMap<String, Dialog>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// this.getActionBar().hide();
		super.onCreate(savedInstanceState);
		// Toast.makeText(MainActivity.this,"Start view",
		// Toast.LENGTH_LONG).show();
		// hide action bar
		// getSupportActionBar().hide();
		setContentView(R.layout.choosepos);
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);// 必须要写
		init();
		initOptions();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mMapView.getMap();
			mUiSettings = aMap.getUiSettings();
			mUiSettings.setZoomGesturesEnabled(false);
			// aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(StaticInfos.GUANGZHOU,ZoomIndex));
			curpos = new LatLng(Double.parseDouble(Helper.getSetting(this, "city_lat")),Double.parseDouble(Helper.getSetting(this, "city_lng")));
			//onMapClick(StaticInfos.GUANGZHOU);
			onMapClick(curpos);
			// aMap.setLocationSource(this);
			// aMap.setMyLocationEnabled(true);
			aMap.setOnMapClickListener(this);
			aMap.setOnCameraChangeListener(this);
			aMap.setOnMarkerClickListener(this);
			// aMap.setTrafficEnabled(true);

			// aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
			// aMap.setLocationSource(arg0)
		}
	}

	private void initOptions() {
		ImageButton back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stopSchedule();
				setResult(101);
				finish();
			}
		});
		// final LinearLayout tab = (LinearLayout)findViewById(R.id.parkingab);
		parkingTitle = (TextView) findViewById(R.id.parking_title);
		options = (LinearLayout) findViewById(R.id.options);
		// set meter spinner
		meterspinner = (Spinner) findViewById(R.id.meter_spinner);
		// final ArrayAdapter am = ArrayAdapter.createFromResource(this,
		// R.array.meters,android.R.layout.simple_spinner_item);
		final ArrayAdapter<String> am = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				Helper.getMetersOption());
		am.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		meterspinner.setAdapter(am);
		meterspinner.setOnItemSelectedListener(meterItemClick);
		// set from time
		final Calendar c = Calendar.getInstance();
		fromHour = c.get(Calendar.HOUR_OF_DAY);
		fromMinute = c.get(Calendar.MINUTE);
		timeFrom = (TextView) findViewById(R.id.parking_time_from);
		timeFrom.setText((fromHour > 9 ? fromHour : "0"+fromHour) + ":" + (fromMinute > 9 ? fromMinute : "0"+fromMinute));
		timeFrom.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(TIME_FROM);
			}
		});
		timeTo = (TextView) findViewById(R.id.parking_time_to);
		c.add(Calendar.HOUR_OF_DAY, 1);
		toHour = c.get(Calendar.HOUR_OF_DAY);
		toMinute = c.get(Calendar.MINUTE);
		timeTo.setText((toHour > 9 ? toHour : "0"+toHour) + ":" + (toMinute > 9 ? toMinute : "0"+toMinute));
		timeTo.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(TIME_TO);
			}
		});
		// options = tab.getHeight();
		// options.setVisibility(View.GONE);
	}

	private TimePickerDialog.OnTimeSetListener fromTimeListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			fromHour = hourOfDay;
			fromMinute = minute;
			timeFrom.setText((fromHour > 9 ? fromHour : "0"+fromHour) + ":" + (fromMinute > 9 ? fromMinute : "0"+fromMinute));
		}
	};
	private TimePickerDialog.OnTimeSetListener toTimeListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			toHour = hourOfDay;
			toMinute = minute;
			timeTo.setText((toHour > 9 ? toHour : "0"+toHour) + ":" + (toMinute > 9 ? toMinute : "0"+toMinute));
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_FROM:
			final TimePickerDialog tf = new TimePickerDialog(this,
					fromTimeListener, fromHour, fromMinute, false);
			tf.setTitle("停车开始");
			return tf;

		case TIME_TO:
			final TimePickerDialog tt = new TimePickerDialog(this,
					toTimeListener, toHour, toMinute, false);
			tt.setTitle("停车结束");
			return tt;
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case 0:
			((TimePickerDialog) dialog).updateTime(fromHour, fromMinute);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		stopSchedule();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
		// startSchedule();
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
		deactivate();
		stopSchedule();
		super.onPause();
	}

	@Override
	protected void onStop() {
		stopSchedule();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		//Toast.makeText(ChooseposActivity.this, "Start requesting",
				//Toast.LENGTH_LONG).show();
		// new HttpConnection().get("user",callbackListener);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 2000, 20, this);
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
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
		if (mListener != null && location != null) {
			// TODO Auto-generated method stub
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			curpos = new LatLng(geoLat, geoLng);

			aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curpos,
					ZoomIndex));
			aMap.clear();
			// aMap.addCircle(new CircleOptions().center(ltg)
			// .radius(1000).strokeColor(Color.argb(50, 1, 1, 1))
			// .fillColor(Color.argb(50, 1, 1, 1)).strokeWidth(10));
			aMap.setMyLocationEnabled(false);
			// Toast.makeText(this, geoLat.toString()+","+geoLng.toString(),
			// Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onMapClick(LatLng ll) {
		// TODO Auto-generated method stub
		if (!status) {
			aMap.clear();
			curpos = ll;
			aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
					.position(curpos).title("center")
					// .draggable(true)
					// .period(50)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.marker)));

			aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curpos,
					ZoomIndex));
			aMap.addCircle(new CircleOptions().center(curpos).radius(radius)
					.strokeColor(Color.argb(50, 1, 1, 1))
					.fillColor(Color.argb(50, 1, 1, 1)).strokeWidth(0));
		}
	}

	public void resetMap() {
		aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(curpos)
				.title("center")

				// .draggable(true)
				// .period(50)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

		aMap.addCircle(new CircleOptions().center(curpos).radius(radius)
				.strokeColor(Color.argb(50, 1, 1, 1))
				.fillColor(Color.argb(50, 1, 1, 1)).strokeWidth(0));
	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		// TODO Auto-generated method stub
		// ZoomIndex = arg0.zoom;
	}

	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {
		// TODO Auto-generated method stub

		ZoomIndex = arg0.zoom;
	}

	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	public void onToggleApplyClick(View view) {
		status = ((ToggleButton) view).isChecked();
		
		if (status) {
			if(!isOpenNetwork()){
				showErrorMessage(getString(R.string.net_not_available));
				((ToggleButton) view).setText(getString(R.string.search));
				return;
			}
			else{
				parkingTitle.setText("搜索停车位...");
				view.setBackgroundDrawable(ChooseposActivity.this.getResources()
						.getDrawable(R.drawable.toggle_click));
				// mUiSettings.setAllGesturesEnabled(false);
				mUiSettings.setZoomControlsEnabled(false);
				startSchedule();
			}
			// Enable vibrate
		} else {
			// Disable vibrate
			parkingTitle.setText("选择停车区域");
			view.setBackgroundDrawable(ChooseposActivity.this.getResources()
					.getDrawable(R.drawable.bghl));
			// mUiSettings.setAllGesturesEnabled(true);
			mUiSettings.setZoomControlsEnabled(true);
			stopSchedule();
			//onMapClick(curpos);
		}
	}

	Handler httpHandler = new Handler() {
		public void handleMessage(Message msg) {
			//radius search parking lot
			if (msg.what == 1) {
				// tvShow.setText(Integer.toString(i++));
				new HttpRequest(new HttpCallback() {
					public void run(String res) {
//						 Toast.makeText(ChooseposActivity.this,
//						 res,Toast.LENGTH_LONG).show();
						JSONObject r;

						try {
							r = new JSONObject(res);
							if ("error".equals(r.getString("code"))) {
								showErrorMessage(r.getString("msg"));
							} else {
								aMap.clear();
								if (r.getInt("total") > 0) {
									// onMapClick(curpos);
									JSONArray rr = r.getJSONArray("result");
									JSONObject row;
									int len = rr.length();
									//if(len > 0){
										
									//}
									for (int i = 0; i < len; i++) {
										row = rr.getJSONObject(i);
										JSONObject loc = row
												.getJSONObject("loc");
										// Log.i("spots",loc.toString());
										// aMap.clear();
										// Toast.makeText(ChooseposActivity.this,
										// row.toString(),
										// Toast.LENGTH_SHORT).show();
										//if(!spotMarkers.containsKey(loc.getDouble("latitude")+","+loc.getDouble("longitude"))){
											aMap.addMarker(new MarkerOptions()
													.anchor(0.5f, 0.5f)
													.position(
															new LatLng(
																	loc.getDouble("latitude"),
																	loc.getDouble("longitude")))
													.title(row.toString())
													// .draggable(true)
													// .period(50)
													.icon(BitmapDescriptorFactory
															.fromResource(R.drawable.marker2))
	
											);
											//spotMarkers.put(loc.getDouble("latitude")+","+loc.getDouble("longitude"),true);
										//}
									}
								}
								resetMap();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							//Toast.makeText(ChooseposActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
							showErrorMessage("错误:"+e.getMessage()+",请检查您的网络连接");
							
						}

					}
				}).execute("/spot_radius_find/" + curpos.longitude + "/"
						+ curpos.latitude + "/" + (radius / 1000)+"/"+Helper.getUid(ChooseposActivity.this), "GET", "");
			}
			super.handleMessage(msg);
		};
	};

	// set up timer
	public void setUpSchedule() {
		if (Config.scheduleStatus) {
			Config.timer = new Timer();
			Config.timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					// 取消定时器,http请求成功后再次开启定时器
					// Looper.prepare();
					Log.i("http", "开始请求");
					Message message = new Message();
					message.what = 1;
					httpHandler.sendMessage(message);
					// timer.cancel();
					// Looper.loop();
					// setUpSchedule();
				}
			}, TIMER_TIMEOUT, TIMER_LOOP);
		}
	}

	public void stopSchedule() {
		Config.scheduleStatus = false;
		Config.timer.cancel();
	}

	public void startSchedule() {
		Config.scheduleStatus = true;
		setUpSchedule();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		// Toast.makeText(ChooseposActivity.this, marker.getTitle(),
		// Toast.LENGTH_SHORT).show();
		String res = marker.getTitle();
		if (!res.equals("center")) {
			// Toast.makeText(ChooseposActivity.this, marker.getTitle(),
			// Toast.LENGTH_SHORT).show();
			// if(cfms.containsKey(id)){
			// cfms.get(id).show();
			// }
			// else{
			//showConfirmApplyDlg(res,marker);
			// }
			Intent intent = new Intent(ChooseposActivity.this,
					SearchedspotActivity.class);
			intent.putExtra("lnglat", marker.getPosition().longitude+","+marker.getPosition().latitude);
			intent.putExtra("parking_time",timeFrom.getText()+","+timeTo.getText());
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent,101);
		}
		return false;
	}

	public void showConfirmApplyDlg(String res,final Marker marker) {
		final Dialog cdlg = new Dialog(this);
		cdlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		cdlg.setContentView(R.layout.apply_confirm);
		final TextView adr = (TextView) cdlg.findViewById(R.id.cfm_address);
		final TextView desc = (TextView) cdlg.findViewById(R.id.cfm_desc);
		final Button cancelBtn = (Button) cdlg
				.findViewById(R.id.confirm_cancel_btn);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cdlg.dismiss();
			}
		});
		
		try {
			Button applyBtn = (Button) cdlg.findViewById(R.id.confirm_apply_btn);
			final JSONObject row = new JSONObject(res);
			adr.setText("地址:" + row.getString("address"));
			desc.setText("描述:" + row.getString("desc"));
			final String postParams = "spot_id=" + row.getString("_id")
					+ "&uid=" + row.getString("uid");
			applyBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					cdlg.dismiss();
					// 嵌套请求，有问题?
					new HttpRequest(new HttpCallback() {

						@Override
						public void run(String res) {
							// TODO Auto-generated method stub
							//stopSchedule();
							//startSchedule();
							try {
								JSONObject r = new JSONObject(res);
								if("error".equals(r.getString("code"))){
									showErrorMessage(r.getString("msg"));
								}
								else{
									showSuccessMessage(r.getString("msg"));
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								showErrorMessage(e.getMessage());
							}
//							Toast.makeText(ChooseposActivity.this,res, Toast.LENGTH_SHORT).show();
//							marker.setTitle("center");
//							Intent intent = new Intent(ChooseposActivity.this,
//									SettingActivity.class);
//							startActivity(intent);
						}
					}).execute("/apply_add", "POST", postParams);
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			showErrorMessage(e.getMessage());
		}
		// new HttpRequest(new HttpCallback() {
		// public void run(String res) {
		// // Toast.makeText(MainActivity.this,
		// // res,Toast.LENGTH_LONG).show();
		// Log.i("http_result", "marker:" + res);
		// try {
		// JSONObject r = new JSONObject(res);
		// int total = r.getInt("total");
		// if (total > 0) {
		// JSONArray info = r.getJSONArray("result");
		// //final JSONObject data = (JSONObject) info.getJSONObject(0);
		// final String postParams =
		// "spot_id="+data.getString("_id")+"&uid="+data.getString("uid");
		// adr.setText("地址:" + data.getString("address"));
		// desc.setText("描述:" + data.getString("desc"));
		//
		// }
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// Toast.makeText(ChooseposActivity.this, e.getMessage(),
		// Toast.LENGTH_SHORT).show();
		// }
		// // initList();
		// }
		// }).execute("/spot_get/" + id, "GET", null);
		cdlg.show();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.setResult(101);
			this.finish();
		}
		return true;
		// return super.onKeyDown(keyCode, event);
	}
}