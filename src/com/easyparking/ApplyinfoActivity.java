package com.easyparking;

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

public class ApplyinfoActivity extends BaseActivity {
	private String apply_id;
	private String spot_id;
	private String apply_state;

	private EditText address;
	private EditText period;
	private EditText owner;
	// private EditText success_count;
	private Button view_comment;
	private Button start_parking;
	private Button stop_parking;
	private Button add_comment;
	private Button remove;
	private ImageButton back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// this.getActionBar().hide();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applyinfo);
		initParams();
		initView();
		initFormData();
	}

	private void initParams() {
		Intent i = getIntent();
		apply_id = i.getStringExtra("apply_id");
		spot_id = i.getStringExtra("spot_id");
		apply_state = i.getStringExtra("apply_state");
	}

	private void initView() {
		address = (EditText) findViewById(R.id.address);
		period = (EditText) findViewById(R.id.period);
		owner = (EditText) findViewById(R.id.spot_owner);
		// success_count = (EditText) findViewById(R.id.success_count);
		initBtns();
	}

	private void initBtns() {
		final String uid = Helper.getSetting(this, "userid");
		back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(111);
				finish();
			}
		});

		view_comment = (Button) findViewById(R.id.view_comment);
		view_comment.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ApplyinfoActivity.this,
						CommentActivity.class);
				intent.putExtra("spot_id", spot_id);
				startActivity(intent);
			}
		});

		start_parking = (Button) findViewById(R.id.start_parking);
		start_parking.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showCfmMsg("警告", "您确定开始停车?", new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						showProgress("验证中...");
						// TODO Auto-generated method stub
						new HttpRequest(new HttpCallback() {

							@Override
							public void run(String res) {
								hideProgress();
								// TODO Auto-generated method stub
								try {
									JSONObject r = new JSONObject(res);
									if ("success".equals(r.getString("code"))) {
										showSuccessMessage(r.getString("msg"));
										back.performClick();
									} else {
										showErrorMessage(r.getString("msg"));
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									showErrorMessage(e.getMessage());
								}

							}

						}).execute("/apply_approved", "POST", "aid=" + apply_id
								+ "&uid=" + uid);
					}
				}, CFM_CANCELCB);

			}
		});
		stop_parking = (Button) findViewById(R.id.stop_parking);
		stop_parking.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showCfmMsg("警告", "您确定结束停车?", new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						new HttpRequest(new HttpCallback() {

							@Override
							public void run(String res) {
								// TODO Auto-generated method stub
								try {
									JSONObject r = new JSONObject(res);
									if ("success".equals(r.getString("code"))) {
										showSuccessMessage(r.getString("msg"));
										//back.performClick();
										Intent intent = new Intent(ApplyinfoActivity.this,
												CommentaddActivity.class);
										intent.putExtra("apply_id", apply_id);
										startActivity(intent);
										hideProgress();
										setResult(111);
										finish();
									} else {
										showErrorMessage(r.getString("msg"));
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									showErrorMessage(e.getMessage());
								}
							}

						}).execute("/apply_leave", "POST", "aid=" + apply_id
								+ "&uid=" + uid);
					}
				}, CFM_CANCELCB);

			}
		});

		add_comment = (Button) findViewById(R.id.add_comment);
		add_comment.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showProgress("转向评论页面中...");
				Intent intent = new Intent(ApplyinfoActivity.this,
						CommentaddActivity.class);
				intent.putExtra("apply_id", apply_id);
				startActivity(intent);
				hideProgress();
				setResult(111);
				finish();
			}
		});

		remove = (Button) findViewById(R.id.remove);
		if (!apply_state.equals("success")) {
			remove.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showCfmMsg("警告", "您确定要删除此申请?", new View.OnClickListener() {

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
											showSuccessMessage(r
													.getString("msg"));
											back.performClick();
										} else {
											showErrorMessage(r.getString("msg"));
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										showErrorMessage(e.getMessage());
									}
								}

							}).execute(
									"/apply_remove/"
											+ Helper.getUid(ApplyinfoActivity.this),
									"POST", "ids=" + apply_id);
						}
					}, CFM_CANCELCB);
				}
			});
		}
		else{
			remove.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.setResult(111);
			this.finish();
		}
		return true;
	}

	private void initFormData() {
		String uid = Helper.getSetting(this, "userid");
		showProgress("数据加载中...");
		new HttpRequest(new HttpCallback() {
			public void run(String res) {
				// Toast.makeText(MainActivity.this,
				// res,Toast.LENGTH_LONG).show();
				// showErrorMessage(res);

				try {
					JSONObject r = new JSONObject(res);
					if ("success".equals(r.getString("code"))) {
						JSONObject row = r.getJSONObject("result");

						JSONObject spotinfo = row.getJSONObject("spotinfo");
						JSONObject ownerinfo = spotinfo
								.getJSONObject("userinfo");
						address.setText(spotinfo.getString("address") + "("
								+ spotinfo.getString("code") + ")");
						period.setText(Helper.showDateTime(row
								.getString("start_time"))
								+ " ~ "
								+ Helper.showDateTime(row.getString("end_time")));
						owner.setText(ownerinfo.getString("user"));
						// success_count.setText(spotinfo.getInt("success_count")+"次");

						String state = row.getString("state");
						if (state.equals("waitforconfirm")) {
							start_parking.setVisibility(View.VISIBLE);
						} else if (state.equals("approved")) {
							stop_parking.setVisibility(View.VISIBLE);
						} else if (state.equals("success")
								&& !row.getBoolean("user_comment")) {
							add_comment.setVisibility(View.VISIBLE);
						}

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
		}).execute("/apply_get/" + apply_id + "/" + uid, "GET", null);
	}
}
