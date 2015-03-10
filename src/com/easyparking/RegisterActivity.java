package com.easyparking;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.easyparking.helper.Helper;
import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;
import com.easyparking.views.WizardView;

public class RegisterActivity extends DetailActivity implements OnGeocodeSearchListener {
	private Button nextstep;
	private Button register;
	private EditText username;
	private EditText password;
	private EditText phone;
	private EditText city;
	private EditText verifycode;
	private GeocodeSearch geocoderSearch;
	private WizardView wizardview;
	private String register_latlng;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		initWizardView();
		//initView();
	}
	
	private void initWizardView() {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout wizard = (LinearLayout) findViewById(R.id.wizard);
	
		LayoutParams params = new LayoutParams(getWinWidth(), getWinHeight());
		
		LinearLayout firststep = (LinearLayout) inflater.inflate(R.layout.register_step1,null);
		firststep.setLayoutParams(params);
		
		wizard.addView(firststep);
		
		LinearLayout secondstep = (LinearLayout) inflater.inflate(R.layout.register_step2,null);
		secondstep.setLayoutParams(params);
		
		wizard.addView(secondstep);	
		
		initViews();
	}

	public void initViews(){
		wizardview  = (WizardView) findViewById(R.id.wizardview);
//		username 	= (EditText) findViewById(R.id.username);
		password 	= (EditText) findViewById(R.id.password);
//		password2	= (EditText) findViewById(R.id.password2);
		phone 		= (EditText) findViewById(R.id.phone);
		city 		= (EditText) findViewById(R.id.city);
		verifycode  = (EditText) findViewById(R.id.verifycode);
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
				if(wizardview.getCurrentPage() > 0){
					wizardview.prePage();
				}
				else{
					setResult(11);
					finish();
				}

			}
		});
		nextstep = (Button) findViewById(R.id.nextstep);
		nextstep.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String telRegex = "[1][358]\\d{9}";
				if(phone.getText().toString().equals("") || !phone.getText().toString().matches(telRegex)){
					showErrorMessage("请填写正确的手机号码!");
				}
				else if(password.getText().toString().equals("")){
					showErrorMessage("请输入密码!");
				}
				else if(city.getText().toString().equals("")){
					showErrorMessage("请填写您所属的城区");
				}
				else{
					showProgress("获取城区坐标中...");
					GeocodeQuery query = new GeocodeQuery(city.getText().toString(), null);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
					geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
				}
			}
		});
		
		register = (Button) findViewById(R.id.register);
		register.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkCodeThenRegister();
			}
		});
	}
	
	private void checkCodeThenRegister(){
		new HttpRequest(new HttpCallback(){

			@Override
			public void run(String res) {
				// TODO Auto-generated method stub
				try {
					JSONObject r = new JSONObject(res);
					
					if(!"error".equals(r.getString("code"))){
						//showSuccessMessage(r.getString("msg"));
						registerUser();
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
			
		}).execute("/user_register_check_verify_code","POST",
				"&phone="+phone.getText().toString()+
				"&code=" + verifycode.getText().toString()
			
		);			
	}
	private void registerUser(){
		showProgress("正在保存用户...");
		new HttpRequest(new HttpCallback(){

			@Override
			public void run(String res) {
				// TODO Auto-generated method stub
				try {
					JSONObject r = new JSONObject(res);
					
					if("success".equals(r.getString("code"))){
//						showSuccessMessage(r.getString("msg"));
//						Intent data=new Intent();
//						data.putExtra("registered_name",phone.getText().toString());
//						setResult(11,data);
//						finish();
						autoLogin();
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
				"user="+phone.getText().toString()+
				"&pass="+password.getText().toString()+
				"&phone="+phone.getText().toString()+
				"&did="+Helper.getDeviceId(RegisterActivity.this)+
				"&city="+city.getText().toString()+
				"&latlng="+register_latlng
		);		
	}
	private void autoLogin(){
		new HttpRequest(new HttpCallback(){
			@Override
			public void run(String res) {
				// TODO Auto-generated method stub
				try {
					JSONObject r = new JSONObject(res);
					
					if(!"error".equals(r.getString("code"))){
						JSONObject d = r.getJSONObject("result");
						JSONObject loc = d.getJSONObject("loc");
						//save session
						Helper.saveSetting(RegisterActivity.this,"userid",d.getString("_id"));
						Helper.saveSetting(RegisterActivity.this,"city_lat", loc.getDouble("latitude")+"");
						Helper.saveSetting(RegisterActivity.this,"city_lng", loc.getDouble("longitude")+"");
						showSuccessMessage(r.getString("msg"));
						showProgress("登录中...");
						Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
						startActivity(intent);
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
			
		}).execute("/user_login","POST",
				"phone="+phone.getText().toString()+
				"&pass="+password.getText().toString()
		);
	}
	private void checkIfPhoneExists(){
		showProgress("正在检测用户...");
		new HttpRequest(new HttpCallback(){

			@Override
			public void run(String res) {
				// TODO Auto-generated method stub
				try {
					JSONObject r = new JSONObject(res);
					
					if(!"error".equals(r.getString("code"))){
						//showSuccessMessage(r.getString("msg"));
						sendVerifyCode();
						wizardview.nextPage();
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
			
		}).execute("/user_if_exists","POST",
				"&phone="+phone.getText().toString()
		);			
	}
	
	private void sendVerifyCode(){
		new HttpRequest(new HttpCallback(){

			@Override
			public void run(String res) {
				// TODO Auto-generated method stub
				try {
					JSONObject r = new JSONObject(res);
					
					if(!"error".equals(r.getString("code"))){
						//wizardview.nextPage();
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
			
		}).execute("/user_register_send_verify_code","POST",
				"&phone="+phone.getText().toString()
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
				//registerUser(address.getLatLonPoint() + "");
				register_latlng = address.getLatLonPoint() + "";
				checkIfPhoneExists();
			} 
			else{
				showErrorMessage("无效的城区地址!");
			}
		} 
		else{
			showErrorMessage("无法获取城市对应的坐标!");
		}
		//hideProgress();
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	public class MyPagerAdapter extends PagerAdapter {
		List<View> list = new ArrayList<View>();

		public MyPagerAdapter(ArrayList<View> list) {
			this.list = list;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			ViewPager pViewPager = ((ViewPager) container);
			pViewPager.removeView(list.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			ViewPager pViewPager = ((ViewPager) arg0);
			pViewPager.addView(list.get(arg1));
			return list.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
		
		@Override
		public int getItemPosition(Object object) {  
		    return POSITION_NONE;  
		} 
	}
	private int getWinWidth(){
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	private int getWinHeight(){
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		hideProgress();
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(wizardview.getCurrentPage() > 0){
				wizardview.prePage();
				return false;
			}
			else{
				finish();
				return true;
			}
		}
		return false;
	}
}
