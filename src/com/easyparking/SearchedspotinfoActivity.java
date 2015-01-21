package com.easyparking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.easyparking.helper.Helper;
import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;

public class SearchedspotinfoActivity extends BaseActivity {
	private String spot_id;
	private String beginning;
	private String end;
	private EditText address;
	private EditText owner;
	private EditText success_count;
	private EditText available_time;
	private Button view_comment;
	private Button apply;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchedspotinfo);
		initParams();
		initViews();
		initFormData();
		
	}
	private void initParams() {
		Intent i = getIntent();
		spot_id = i.getStringExtra("spot_id");
		beginning = i.getStringExtra("beginning");
		end = i.getStringExtra("end");
	}
	private void initViews() {
		address = (EditText) findViewById(R.id.address);
		owner = (EditText) findViewById(R.id.spot_owner);
		success_count = (EditText) findViewById(R.id.success_count);
		available_time  = (EditText) findViewById(R.id.available_time);
		initBtns();
	}

	private void initBtns() {
		final ImageButton back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		view_comment = (Button) findViewById(R.id.view_comment);
		view_comment.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SearchedspotinfoActivity.this,CommentActivity.class);
				intent.putExtra("spot_id", spot_id);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		apply = (Button) findViewById(R.id.apply);
		apply.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showCfmMsg("警告", "确定申请此车位？",
				// if user confirm to delete
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								confirmAlg.cancel();
								new HttpRequest(new HttpCallback() {

									@Override
									public void run(String res) {
										// TODO Auto-generated method stub
										try {
											JSONObject r = new JSONObject(res);
											if ("error".equals(r
													.getString("code"))) {
												showErrorMessage(r
														.getString("msg"));
											} else {
												showSuccessMessage(r
														.getString("msg"));
												back.performClick();
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											showErrorMessage(e.getMessage());
										}
										
									}

								}).execute(
										"/apply_add/"
												+ Helper.getUid(SearchedspotinfoActivity.this),
										"POST", "spot_id=" + spot_id
												+ "&beginning=" + beginning
												+ "&end=" + end);
							}
						}, CFM_CANCELCB);
				
			}
		});
	}

	private void initFormData() {
		showProgress("数据加载中...");
		new HttpRequest(new HttpCallback() {
			public void run(String res) {
				// Toast.makeText(MainActivity.this,
				// res,Toast.LENGTH_LONG).show();
				// showErrorMessage(res);

				try {
					JSONObject r = new JSONObject(res);
					if ("success".equals(r.getString("code"))) {
						JSONObject spotinfo = r.getJSONObject("result");
						JSONArray available = spotinfo.getJSONArray("available_times");
						JSONObject ownerinfo = spotinfo
								.getJSONObject("userinfo");
						address.setText(spotinfo.getString("address") + "("
								+ spotinfo.getString("code") + ")");
						owner.setText(ownerinfo.getString("user"));
						success_count.setText(spotinfo.getInt("success_count")
								+ "次");
						available_time.setText(available.join(";").replace("\"", ""));
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
		}).execute("/spot_get/" + spot_id, "GET", null);
	}
}
