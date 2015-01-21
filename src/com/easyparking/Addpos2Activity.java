package com.easyparking;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.easyparking.helper.Helper;
import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;
import com.easyparking.helper.Config;

public class Addpos2Activity extends BaseActivity implements
		OnGeocodeSearchListener {
	private GeocodeSearch geocoderSearch;
	private LatLonPoint latLonPoint;
	private EditText address;
	private EditText latlng;
	private EditText code;
	private Button savebtn;
	private String addr;
	// private Spinner meterspinner;
	private EditText desc;
	private EditText available_time;
	private boolean gotError = false;
	private boolean updating_lnglat = false;

	// private OnItemSelectedListener meterItemClick = new
	// OnItemSelectedListener() {
	// @Override
	// public void onItemSelected(AdapterView<?> parent, View view,
	// int position, long id) {
	// // TODO Auto-generated method stub
	// Integer key = Integer.parseInt(meterspinner.getSelectedItem()
	// .toString().replaceAll("[^\\d]", ""));
	// radius = StaticInfos.MetersOption.get(key);
	//
	// }
	//
	// @Override
	// public void onNothingSelected(AdapterView<?> parent) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// };

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addposstep2);
		latlng = (EditText) findViewById(R.id.curpoint);
		address = (EditText) findViewById(R.id.curpos);
		code 	= (EditText) findViewById(R.id.parking_code);
		savebtn = (Button) findViewById(R.id.addspot_save);
		available_time = (EditText) findViewById(R.id.available_time);
		// savebtn.setClickable(false);
		// meterspinner = (Spinner) findViewById(R.id.meter_spinner_form);
		desc = (EditText) findViewById(R.id.desc);
		LatLng g = Config.getSpotPos();
		// Toast.makeText(this, g.latitude+"", Toast.LENGTH_LONG).show();
		if (g.latitude + "" != "") {
			// add
			latlng.setText(g.latitude + "," + g.longitude);
			geocoderSearch = new GeocodeSearch(this);
			geocoderSearch.setOnGeocodeSearchListener(this);
			address.setText("正在获取地址中...");
			latLonPoint = new LatLonPoint(g.latitude, g.longitude);
			RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
					GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
			geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
		}
		initBtns();
		initForm();
	}

	public void initForm() {
		final ArrayAdapter<String> am = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				Helper.getMetersOption());
		am.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// meterspinner.setAdapter(am);
		// meterspinner.setOnItemSelectedListener(meterItemClick);
	}

	public void initBtns() {
		ImageButton back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(201);
				finish();
			}
		});
		savebtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(gotError){
					showErrorMessage(getString(R.string.not_found_address_unsaveable));
					return;
				}
				String v1 = address.getText().toString();
				if (!v1.equals(addr)) {
					// address has been changed,we need to update latlng points
					updating_lnglat = true;
					savebtn.setClickable(false);
					showProgress("您已改动地址信息,正在更新坐标值...");
					GeocodeQuery query = new GeocodeQuery(v1, null);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
					geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
					addr = v1;
					// Toast.makeText(Addpos2Activity.this, "地址不一样",
					// Toast.LENGTH_SHORT).show();
				}
				else{
					String params = "";
					savebtn.setClickable(false);
					params = "uid=" + Helper.getUid(Addpos2Activity.this);
					params += "&latlng=" + latlng.getText();
					params += "&address=" + address.getText();
					params += "&code="+code.getText();
					params += "&desc=" + desc.getText();
					params += "&times=" + available_time.getText();
					new HttpRequest(new HttpCallback() {
						public void run(String res) {
							// Toast.makeText(MainActivity.this,
							// res,Toast.LENGTH_LONG).show();
							// Log.i("http_result",res);
							try {
								JSONObject r = new JSONObject(res);
								if ("error".equals(r.getString("code"))) {
									showErrorMessage(r.getString("msg"));
									savebtn.setClickable(true);
	
								} else {
									showSuccessMessage(r.getString("msg"));
									//setResult(201);
									finish();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								showErrorMessage(e.getMessage());
							}
						}
					}).execute("/spot_add", "POST", params);
				}

			}
		});
	}

	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		// savebtn.setClickable(false);
		gotError = false;
		if (rCode == 0) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);
				latlng.setText(address.getLatLonPoint() + "");
				// savebtn.setClickable(true);
				
				showSuccessMessage("坐标更新完毕,请再次点击保存按钮!");
				savebtn.setClickable(true);
			} 
			else{
				gotError = true;
			}

		} 
		else{
			gotError = true;
		}
		hideProgress();
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		// savebtn.setClickable(false);
		gotError = false;
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				addr = result.getRegeocodeAddress().getFormatAddress();
				// savebtn.setClickable(true);
				
			} else {
				gotError = true;
				// ToastUtil.show(GeocoderActivity.this, R.string.no_result);
				addr = "没有找到相应地点";
			}
		} else if (rCode == 27) {
			gotError = true;
			// ToastUtil.show(GeocoderActivity.this, R.string.error_network);
			addr = "网络异常";
		} else if (rCode == 32) {
			gotError = true;
			// ToastUtil.show(GeocoderActivity.this, R.string.error_key);
			addr = "高德地图所需的秘钥不正确";
		} else {
			gotError = true;
			addr = "";
		}
		address.setText(addr);
		hideProgress();
	}
}
