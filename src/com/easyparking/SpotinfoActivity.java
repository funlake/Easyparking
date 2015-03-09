package com.easyparking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.easyparking.helper.Helper;
import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;

public class SpotinfoActivity extends DetailActivity {
	private String spot_id;
	private String latlng;
	private EditText address;
	private EditText desc;
	private EditText success_count;
	private EditText available_time;
	private Button view_comment;
	private Button view_apply;
	private Button view_map;
	private Button update;
	private Button remove;
	private ImageButton back;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spotinfo);
		initParams();
		initViews();
		initFormData();
	}

	private void initParams() {
		Intent i = getIntent();
		spot_id = i.getStringExtra("spot_id");
	}

	private void initViews() {
		address = (EditText) findViewById(R.id.address);
		desc = (EditText) findViewById(R.id.desc);
		success_count = (EditText) findViewById(R.id.success_count);
		available_time = (EditText) findViewById(R.id.available_time);
		back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(211);
				finish();
			}
		});
	}

	private void initBtns() {

		view_comment = (Button) findViewById(R.id.view_comment);
		view_comment.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SpotinfoActivity.this,
						CommentActivity.class);
				intent.putExtra("spot_id", spot_id);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent,1211);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});
		view_map = (Button) findViewById(R.id.view_map);
		view_map.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showProgress("转向地图页面...");
				Intent intent = new Intent(SpotinfoActivity.this,
						SpotmapActivity.class);
				intent.putExtra("latlng", latlng);
				intent.putExtra("address", address.getText().toString());
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				startActivityForResult(intent,1211);
				hideProgress();
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});
		view_apply = (Button) findViewById(R.id.view_apply);
		view_apply.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SpotinfoActivity.this,
						SpotapplyActivity.class);
				intent.putExtra("spot_id", spot_id);
				intent.putExtra("address", address.getText().toString());
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent,1211);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});
/*		update = (Button) findViewById(R.id.update);
		update.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new HttpRequest(new HttpCallback() {

					@Override
					public void run(String res) {
						// TODO Auto-generated method stub
						try {
							JSONObject r = new JSONObject(res);
							if ("success".equals(r
									.getString("code"))) {
								setResult(213);
								finish();
							}
							showSuccessMessage(r
									.getString("msg"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							showErrorMessage(e.getMessage());
						}
					}

				}).execute("/spot_update/"+spot_id, "POST",
						"desc=" + desc.getText().toString());
			}
		});*/
		remove = (Button) findViewById(R.id.remove);
		remove.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showCfmMsg("警告", "确定删除所选车位？",
				// if user confirm to delete
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								confirmAlg.cancel();
								// TODO Auto-generated method stub
								new HttpRequest(new HttpCallback() {

									@Override
									public void run(String res) {
										// TODO Auto-generated method stub
										try {
											JSONObject r = new JSONObject(res);
											if ("success".equals(r
													.getString("code"))) {
												setResult(213);
												finish();
											}
											showSuccessMessage(r
													.getString("msg"));
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											showErrorMessage(e.getMessage());
										}
									}

								}).execute("/spot_remove/" + Helper.getUid(SpotinfoActivity.this), "POST",
										"ids=" + spot_id);
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
						JSONArray available = spotinfo
								.getJSONArray("available_times");
						// JSONObject ownerinfo = spotinfo
						// .getJSONObject("userinfo");
						address.setText(spotinfo.getString("address") + "("
								+ spotinfo.getString("code") + ")");
						desc.setText(spotinfo.getString("desc"));
						success_count.setText(spotinfo.getInt("success_count")
								+ "次");
						available_time.setText(available.join(";").replace(
								"\"", ""));
						JSONObject loc = spotinfo.getJSONObject("loc");
						latlng = loc.getDouble("latitude") + ","
								+ loc.getDouble("longitude");
						initBtns();

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		hideProgress();
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.setResult(211);
			this.finish();
		}
		return true;
		// return super.onKeyDown(keyCode, event);
	}
}
