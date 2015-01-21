package com.easyparking;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.easyparking.helper.Config;
import com.easyparking.helper.Helper;
import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;
import com.gitonway.niftydialogeffects.widget.niftydialogeffects.Effectstype;
import com.gitonway.niftydialogeffects.widget.niftydialogeffects.NiftyDialogBuilder;

public class ProfilerActivity extends BaseActivity implements OnGeocodeSearchListener {
	private Button logout;
	private Button shop;
	private Button save;
	private Button update;
	private EditText username;
	private EditText phone;
	private EditText points;
	private EditText city;
	private GeocodeSearch geocoderSearch;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profiler);
		initViews();
		initFormData();
		Config.profile = this;
	}

	private void initViews() {
		username = (EditText) findViewById(R.id.username);
		phone = (EditText) findViewById(R.id.phone);
		points = (EditText) findViewById(R.id.point);
		city = (EditText) findViewById(R.id.city);
		
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		initBtns();
	}

	private void initBtns() {
		update = (Button) findViewById(R.id.update);
		update.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initFormData();
			}
		});
		save = (Button) findViewById(R.id.userinfo_save);
		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showProgress("获取城区坐标中...");
				GeocodeQuery query = new GeocodeQuery(city.getText().toString(), null);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
				geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
			}
		});
		shop = (Button) findViewById(R.id.point_exchange);
		shop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(ProfilerActivity.this,ShopActivity.class));
			}
		});
		logout = (Button) findViewById(R.id.logout);
		logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showCfmMsg("警告", "您确定要退出登录?",
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Helper.saveSetting(ProfilerActivity.this,
										"userid", "");
								Helper.saveSetting(ProfilerActivity.this,
										"clientid", "");
								Config.core.stopRefreshService();
								activityLogout();
								startActivity(new Intent(ProfilerActivity.this,
										LoginActivity.class));
								finish();
							}
						}, CFM_CANCELCB);
				
//				NiftyDialogBuilder confirmAlg =	NiftyDialogBuilder.getInstance(ProfilerActivity.this);
//				confirmAlg
//		        .withTitle("警告")                                  //.withTitle(null)  no title
//		        .withTitleColor("#FFFFFF")                                  //def
//		        .withDividerColor("#11000000")                              //def
//		        .withMessage("您确定要退出登录?")                     //.withMessage(null)  no Msg
//		        .withMessageColor("#FFFFFF")                                //def
//		        //.withIcon(getResources().getDrawable(R.drawable.icon))
//		        .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
//		        .withDuration(400)                                          //def
//		        .withEffect(Effectstype.Flipv)                                         //def Effectstype.Slidetop
//		        .withButton1Text("确认")                                      //def gone
//		        .withButton2Text("取消")                                  //def gone
//		       // .setCustomView(R.layout.custom_view,v.getContext())         //.setCustomView(View or ResId,context)
//		        .setButton1Click(null)
//		        .setButton2Click(null)
//		        .show();

			}
		});
	}

	private void initFormData() {
		String uid = Helper.getSetting(this, "userid");
		showProgress("数据加载中...");
		new HttpRequest(new HttpCallback() {
			public void run(String res) {
				// Toast.makeText(MainActivity.this,
				// res,Toast.LENGTH_LONG).show();
				// Log.i("HTTP", res);
				try {
					JSONObject r = new JSONObject(res);
					if ("success".equals(r.getString("code"))) {
						JSONObject data = r.getJSONObject("result");
						username.setText(data.getString("user"));
						phone.setText(data.getString("phone"));
						points.setText(data.getInt("points") + "");
						city.setText(data.getString("city"));
					} else {
						showErrorMessage(r.getString("msg"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					// Toast.makeText(MyspotActivity.this, e.getMessage(),
					// Toast.LENGTH_SHORT).show();
					showErrorMessage("错误:" + e.getMessage() + ",请检查您的网络连接");
				}
				hideProgress();
			}
		}).execute("/user_get/" + uid, "GET", null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		hideProgress();
		//StaticInfos.myapply
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// onBackPressed();
			// Toast.makeText(MyspotActivity.this, "hehe",
			// Toast.LENGTH_LONG).show();
			// finish();
			Config.myapply.stopRefreshService();
			Config.core.onBackPressed();
		}
		return true;
		// return super.onKeyDown(keyCode, event);
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
				saveUserProfiler(address.getLatLonPoint() + "");
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
	
	private void saveUserProfiler(String searchedPoint){
		String uid = Helper.getSetting(this, "userid");
		final String latlng[] = searchedPoint.split(",");
		// TODO Auto-generated method stub
		new HttpRequest(new HttpCallback() {
			public void run(String res) {
				try {
					JSONObject r = new JSONObject(res);
					if ("success".equals(r.getString("code"))) {
						showSuccessMessage(r.getString("msg"));
						Helper.saveSetting(ProfilerActivity.this,"city_lat", latlng[0]);
						Helper.saveSetting(ProfilerActivity.this,"city_lng", latlng[1]);
						initFormData();
					
//	
//						Helper.sendSMS(phone.getText().toString(),
//								"[易停车]感谢您保存了一条记录");
					} else {
						showErrorMessage(r.getString("msg"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					// Toast.makeText(MyspotActivity.this,
					// e.getMessage(),
					// Toast.LENGTH_SHORT).show();
					showErrorMessage("错误:" + e.getMessage()
							+ ",请检查您的网络连接");
				}
				hideProgress();
			}
		}).execute("/user_update/" + uid, "POST",
				"phone=" + phone.getText()+"&city="+city.getText()+"&latlng="+searchedPoint);
	}
}
