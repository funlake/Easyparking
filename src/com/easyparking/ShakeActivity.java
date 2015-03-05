package com.easyparking;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.easyparking.helper.AMapUtil;
import com.easyparking.helper.ShakeDetector;
import com.easyparking.helper.ShakeDetector.OnShakeListener;

public class ShakeActivity extends BaseActivity implements AMapLocationListener{
	private ShakeDetector mShakeDetector;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Button searchPos;
	private LocationManagerProxy mLocationProxy;

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shake);
		initShake();
		initViews();
		initLocation();
	}

	private void initLocation() {
		// TODO Auto-generated method stub
		mLocationProxy = LocationManagerProxy.getInstance(this);
		mLocationProxy.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 2000, 10, this);
		
	}

	private void initShake() {
		// TODO Auto-generated method stub
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector(new OnShakeListener() {
            @Override
            public void onShake() {
                // Do stuff!
            	searchPos();
            }
        });		
	}
	private void searchPos(){
		showSuccessMessage("You shaked me?");
	}
	@Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }   
	private void initViews() {
		// TODO Auto-generated method stub
		searchPos = (Button) findViewById(R.id.search_pos);
		
		searchPos.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchPos();
			}
		});
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
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
					+ "\n精    度    :" + location.getAccuracy() + "米"
					+ "\n定位方式:" + location.getProvider() + "\n定位时间:" + AMapUtil
					.convertToTime(location.getTime()));
			showSuccessMessage(str);
			if (mLocationProxy != null) {
				mLocationProxy.removeUpdates(this);
				mLocationProxy.destory();
			}
			mLocationProxy = null;
		}
	}

}
