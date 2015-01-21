package com.easyparking;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.easyparking.helper.Helper;
import com.easyparking.helper.Config;

public class Addpos1Activity extends BaseActivity implements
		OnMapClickListener, OnCameraChangeListener, InfoWindowAdapter ,OnGeocodeSearchListener{
	public MapView mMapView;
	private AMap aMap;
	private LatLng curpos;
	private float ZoomIndex = 14;
	private GeocodeSearch geocoderSearch;
	private TextView info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// this.getActionBar().hide();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addposstep1);
		mMapView = (MapView) findViewById(R.id.lmap);
		mMapView.onCreate(savedInstanceState);// 必须要写
		initBtns();
	}

	@Override
	protected void onStart() {
		init();
		super.onStart();
	}

	public void init() {
		if (aMap == null) {
			aMap = mMapView.getMap();
			aMap.setOnCameraChangeListener(this);
			aMap.setOnMapClickListener(this);
			aMap.setInfoWindowAdapter(this);
			geocoderSearch = new GeocodeSearch(this);
			geocoderSearch.setOnGeocodeSearchListener(this);
			curpos = new LatLng(Double.parseDouble(Helper.getSetting(this, "city_lat")),Double.parseDouble(Helper.getSetting(this, "city_lng")));
			//onMapClick(StaticInfos.GUANGZHOU);
			onMapClick(curpos);
		}
	}

	public void initBtns() {
		Button nextbtn = (Button) findViewById(R.id.addspot_nextstep);
		nextbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Addpos1Activity.this,
						Addpos2Activity.class);
				startActivity(intent);
			}
		});
		
		ImageButton back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(201);
				finish();
			}
		});
	}

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data){
	// setResult(101,new Intent());
	// super.onActivityResult(requestCode, resultCode, data);
	// finish();
	// }
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
		// deactivate();
	}

	@Override
	public void onMapClick(LatLng ll) {
		// TODO Auto-generated method stub
		aMap.clear();
		curpos = ll;
		Config.setSpotPos(curpos);

		Marker marker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.position(curpos).title("中心点").draggable(true).period(50)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
		aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curpos, ZoomIndex));
		marker.showInfoWindow();
		info.setText("...");
		RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(ll.latitude, ll.longitude), 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
		
		// aMap.addCircle(new CircleOptions().center(curpos).radius(1000)
		// .strokeColor(Color.argb(50, 1, 1, 1))
		// .fillColor(Color.argb(50, 1, 1, 1)).strokeWidth(0));
	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {
		// TODO Auto-generated method stub
		ZoomIndex = arg0.zoom;

	}

	@Override
	public View getInfoContents(Marker marker) {
		// TODO Auto-generated method stub
		View infoContent = getLayoutInflater().inflate(R.layout.parking_info,
				null);
		// render(marker, infoContent);
		info = (TextView) infoContent
				.findViewById(R.id.parking_info_title);
		info.setText(marker.getTitle());
		return infoContent;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		View infoContent = getLayoutInflater().inflate(R.layout.parking_info,
				null);
		// render(marker, infoContent);
		info = (TextView) infoContent
				.findViewById(R.id.parking_info_title);
		info.setText(marker.getTitle());
		return infoContent;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		hideProgress();
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.setResult(201);
			this.finish();
		}
		return true;
		// return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		// TODO Auto-generated method stub
		String error = "...";
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				info.setText(result.getRegeocodeAddress().getFormatAddress());
				// savebtn.setClickable(true);
				error = "";
				
			} else {
				// ToastUtil.show(GeocoderActivity.this, R.string.no_result);
				error = "没有找到相应地点";
			}
		} else if (rCode == 27) {
			// ToastUtil.show(GeocoderActivity.this, R.string.error_network);
			error = "网络异常";
		} else if (rCode == 32) {
			// ToastUtil.show(GeocoderActivity.this, R.string.error_key);
			error = "高德地图所需的秘钥不正确";
		} else {

		}
		if(!error.equals("")){
			info.setText(error);
		}
		hideProgress();
	}
}
