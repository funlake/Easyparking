package com.easyparking;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;

public class CommentaddActivity extends BaseActivity {
	private String apply_id;
	private Button save;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_add);
		initParams();
		initViews();
	}
	private void initParams(){
		Intent i = getIntent();
		apply_id = i.getStringExtra("apply_id");
	}
	private void initViews(){
		ImageButton back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//setResult(201);
				finish();
			}
		});
		save = (Button) findViewById(R.id.comment_save);
		save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				EditText desc = (EditText) findViewById(R.id.desc);
				RadioGroup attitude = (RadioGroup) findViewById(R.id.attitude);
				if(desc.getText().toString().equals("")){
					showErrorMessage("请填写描述!");
				}
				else{
					int id = attitude.getCheckedRadioButtonId();
					String att = "";
					switch(id){
						case R.id.good:
							att = "good";
						break;
						
						case R.id.soso:
							att = "soso";
						break;
						
						case R.id.bad:
							att = "bad";
						break;
					}
					new HttpRequest(new HttpCallback(){

						@Override
						public void run(String res) {
							// TODO Auto-generated method stub
							try {
								JSONObject r = new JSONObject(res);
								if("success".equals(r.getString("code"))){
									showSuccessMessage(r.getString("msg"));
									finish();
									
								}
								else{
									showErrorMessage(r.getString("msg"));
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								showErrorMessage(e.getMessage());
							}
						}
						
					}).execute("/comment_add","POST","aid="+apply_id+"&attitude="+att+"&comment="+desc.getText().toString());
				}
				
			}
		});
		initBtns();
	}
	
	private void initBtns(){
		
	}
}
