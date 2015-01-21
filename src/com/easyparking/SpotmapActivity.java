package com.easyparking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;


/**
 * AMapV1地图中介绍如何显示一个基本地图
 */
public class SpotmapActivity extends Activity implements InfoWindowAdapter {
	private MapView mapView;
	private AMap aMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spotmap);
		mapView = (MapView) findViewById(R.id.smap);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		initMap();
		initBtns();
	}
	
	

	private void initBtns() {
		// TODO Auto-generated method stub
		ImageButton back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}



	/**
	 * 初始化AMap对象
	 */
	private void initMap() {
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.setInfoWindowAdapter(this);
			Intent i = getIntent();
			String latlng[] = i.getStringExtra("latlng").split(",");
			String address = i.getStringExtra("address");
			LatLng p = new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
			initMarker(p,address);
			
		}
	}

	private void initMarker(LatLng ll,String addr){
		Marker marker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.position(ll).title(addr).draggable(true).period(50)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
		aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 17));
		marker.showInfoWindow();
	}
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
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
	public View getInfoContents(Marker marker) {
		// TODO Auto-generated method stub
		View infoContent = getLayoutInflater().inflate(R.layout.parking_info,
				null);
		// render(marker, infoContent);
		TextView info = (TextView) infoContent
				.findViewById(R.id.parking_info_title);
		info.setText(marker.getTitle());
		return infoContent;
	}



	@Override
	public View getInfoWindow(Marker marker) {
		// TODO Auto-generated method stub
		View infoContent = getLayoutInflater().inflate(R.layout.parking_info,
				null);
		// render(marker, infoContent);
		TextView info = (TextView) infoContent
				.findViewById(R.id.parking_info_title);
		info.setText(marker.getTitle());
		return infoContent;
	}

}
