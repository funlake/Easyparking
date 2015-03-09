package com.easyparking;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.easyparking.helper.Helper;
import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;

public class RegisterActivity extends DetailActivity implements OnGeocodeSearchListener {
	private Button register;
	private EditText username;
	private EditText password;
	private EditText password2;
	private EditText phone;
	private EditText city;
	private LinearLayout firststep;
	private GeocodeSearch geocoderSearch;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register2);
		initView();
	}
	
	public void initView(){
//		username 	= (EditText) findViewById(R.id.username);
		password 	= (EditText) findViewById(R.id.password);
//		password2	= (EditText) findViewById(R.id.password2);
		phone 		= (EditText) findViewById(R.id.phone);
		city 		= (EditText) findViewById(R.id.city);
		
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		initBtns();
	}
	public void initBtns(){
		ImageButton back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(11);
				finish();
			}
		});
		register = (Button) findViewById(R.id.register);
		register.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				firststep = (LinearLayout) findViewById(R.id.firststep);
				firststep.animate().setDuration(400).translationXBy(-1000).start();
				
//				if(phone.getText().toString().equals("")){
//					showErrorMessage("请填写手机号码!");
//				}
//				else if(password.getText().toString().equals("")){
//					showErrorMessage("请输入密码!");
//				}
//				else if(city.getText().toString().equals("")){
//					showErrorMessage("请填写您所属的城区");
//				}
//				else{
//					showProgress("获取城区坐标中...");
//					GeocodeQuery query = new GeocodeQuery(city.getText().toString(), null);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
//					geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
//					
//
//				}
				
				// TODO Auto-generated method stub

			}
		});
	}
	private void registerUser(String latlng){
		showProgress("正在保存用户...");
		new HttpRequest(new HttpCallback(){

			@Override
			public void run(String res) {
				// TODO Auto-generated method stub
				try {
					JSONObject r = new JSONObject(res);
					
					if(!"error".equals(r.getString("code"))){
						showSuccessMessage(r.getString("msg"));
						Intent data=new Intent();
						data.putExtra("registered_name",username.getText().toString());
						setResult(11,data);
						finish();
					}
					else{
						showErrorMessage(r.getString("msg"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					showErrorMessage(e.getMessage());
				}
				hideProgress();
			}
			
		}).execute("/user_register","POST",
				"user="+username.getText().toString()+
				"&pass="+password.getText().toString()+
				"&phone="+phone.getText().toString()+
				"&did="+Helper.getDeviceId(RegisterActivity.this)+
				"&city="+city.getText().toString()+
				"&latlng="+latlng
		);		
	}
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		hideProgress();
		// TODO Auto-generated method stub
		if (rCode == 0) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);
				//showSuccessMessage(address.getLatLonPoint() + "");
				// savebtn.setClickable(true);
				registerUser(address.getLatLonPoint() + "");
			} 
			else{
				showErrorMessage("无效的城区地址!");
			}
		} 
		else{
			showErrorMessage("无法获取城市对应的坐标!");
		}
		hideProgress();
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
