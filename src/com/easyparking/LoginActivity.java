package com.easyparking;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.easyparking.helper.Helper;
import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;
public class LoginActivity extends BaseActivity {
	private EditText username;
	private EditText password;
	private Button login;
	private Button register;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		initView();
		
	}
	public void initView(){
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		initBtns();
	}
	public void initBtns(){
		login = (Button) findViewById(R.id.login);
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showProgress("用户验证中...");
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
								Helper.saveSetting(LoginActivity.this,"userid",d.getString("_id"));
								Helper.saveSetting(LoginActivity.this,"city_lat", loc.getDouble("latitude")+"");
								Helper.saveSetting(LoginActivity.this,"city_lng", loc.getDouble("longitude")+"");
								showSuccessMessage(r.getString("msg"));
								showProgress("转向主页面中...");
								Intent intent = new Intent(LoginActivity.this,MainActivity.class);
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
						"user="+username.getText().toString()+
						"&pass="+password.getText().toString()
				);
				hideProgress();
			}
		});
		register = (Button) findViewById(R.id.register);
		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showProgress("转向注册页面中...");
				Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
				startActivityForResult(intent, 10);
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		
		if(resultCode == 11){
			String u = data.getExtras().getString("registered_name");
			username.setText(u.toString());
			password.requestFocus();
		}
		hideProgress();
	}
}
