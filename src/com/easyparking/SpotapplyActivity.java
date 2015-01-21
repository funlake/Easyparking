package com.easyparking;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyparking.helper.Helper;
import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;

public class SpotapplyActivity extends BaseActivity {
	private String spot_id;
	private ListView spotapplylist;
	private String address;
	private String code;
	private Resources resources;
	// private Button applyCfmBtn;
	// private Button rejectCfmBtn;
	private RelativeLayout toolbar;

	// private String selectedIds;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spotapply);
		Intent i = getIntent();
		spot_id = i.getStringExtra("spot_id");
		address = i.getStringExtra("address");
		resources = getResources();
		toolbar = (RelativeLayout) findViewById(R.id.spotapplybanner);
		initList();
		initBtns();
		initBottom();
		initStatusLang();
	}

	private void initBottom() {
		TextView ad = (TextView) findViewById(R.id.addressinfo);
		ad.setText(address);
	}

	@SuppressLint("InlinedApi")
	private void initList() {
		spotapplylist = (ListView) findViewById(R.id.spotapplylist);
		// spotapplylist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		// spotapplylist.setMultiChoiceModeListener(new ModeCallback());

		spotapplylist.setTextFilterEnabled(false);
		spotapplylist
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						String state = getColDataByKey(position + 1, "state");
						if (!state.equals("success")) {
							// spotapplylist.setItemChecked(position+1, true);
							final String aid = getColDataByKey(position + 1,
									"_id");
							final String uid = getColDataByKey(position + 1,
									"uid");
							showCfmMsg("注意", "是否拒绝/同意该申请?", "拒绝", "同意",
							// if user confirm to delete
									new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											confirmAlg.cancel();
											// TODO Auto-generated method stub
											new HttpRequest(new HttpCallback() {

												@Override
												public void run(String res) {
													// TODO Auto-generated
													// method stub
													try {
														JSONObject r = new JSONObject(
																res);
														if ("error".equals(r
																.getString("code"))) {
															showErrorMessage(r
																	.getString("msg"));

														} else {
															refreshData();
															showSuccessMessage(r
																	.getString("msg"));
														}

													} catch (JSONException e) {
														// TODO Auto-generated
														// catch block
														showErrorMessage(e
																.getMessage());
													}
												}
											}).execute("/apply_reject", "POST",
													"aid=" + aid + "&uid="
															+ uid);
										}
									}, new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											confirmAlg.cancel();
											// TODO Auto-generated method stub
											new HttpRequest(new HttpCallback() {

												@Override
												public void run(String res) {
													// TODO Auto-generated
													// method stub
													try {
														JSONObject r = new JSONObject(
																res);
														if ("error".equals(r
																.getString("code"))) {
															showErrorMessage(r
																	.getString("msg"));

														} else {

															refreshData();
															showSuccessMessage(r
																	.getString("msg"));
														}

													} catch (JSONException e) {
														// TODO Auto-generated
														// catch block
														showErrorMessage(e
																.getMessage());
													}
												}
											}).execute("/apply_confirm",
													"POST", "aid=" + aid
															+ "&uid=" + uid);
										}
									});
						}
					}
				});

		showProgress("数据加载中...");
		refreshData();
	}

	private void initBtns() {
		ImageButton back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(201);
				finish();
			}
		});
	}

	public void refreshData() {
		// showProgress(getString(R.string.loading_data));
		new HttpRequest(new HttpCallback() {
			public void run(String res) {
				// Toast.makeText(MainActivity.this,
				// res,Toast.LENGTH_LONG).show();
				// Log.i("HTTP", res);
				try {
					JSONObject r = new JSONObject(res);
					if ("success".equals(r.getString("code"))) {
						// if(r.getInt("total") > 0){
						listdata = r.getJSONArray("result");
						if (listdata.length() > 0) {
							spotapplylist.setAdapter(new spotApplyAdapter(
									SpotapplyActivity.this));
						}
						// myspotsList.setAdapter(sdt);
						// myspotsList.onRefreshComplete();
						// }
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					// Toast.makeText(MyspotActivity.this, e.getMessage(),
					// Toast.LENGTH_SHORT).show();
					showErrorMessage("错误:" + e.getMessage() + ",请检查您的网络连接");
				}
				// spotapplylist.onRefreshComplete();
				hideProgress();
			}
		}).execute("/apply_by_spot/" + spot_id, "GET", null);
	}

	public final class ViewHolder {

		// public TextView datetime;
		public TextView username;
		public TextView state;
		public TextView parking_period;
	}

	public class spotApplyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public spotApplyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listdata.length();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.spotapply_row, parent,
						false);
				holder = new ViewHolder();
				// JSONObject loc;
				// holder.datetime = (TextView)
				// convertView.findViewById(R.id.datetime);
				holder.parking_period = (TextView) convertView
						.findViewById(R.id.parking_period);
				holder.username = (TextView) convertView
						.findViewById(R.id.username);
				holder.state = (TextView) convertView.findViewById(R.id.state);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final JSONObject row;
			try {
				row = listdata.getJSONObject(position);
				JSONObject userinfo = row.getJSONObject("userinfo");
				// holder.datetime.setText(row.getString("created_time"));
				holder.username.setText(userinfo.getString("user"));
				holder.parking_period.setText(Helper.showDateTime(row
						.getString("start_time"))
						+ " ~ "
						+ Helper.showDateTime(row.getString("end_time")));
				holder.state.setBackgroundColor(resources.getColor(resources
						.getIdentifier("Status_" + row.getString("state"),
								"color", getPackageName())));
				holder.state.setText(ApplyStatus.get(row.getString("state")));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				showErrorMessage(e.getMessage());
			}
			return convertView;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.setResult(201);
			this.finish();
		}
		return true;
		// return super.onKeyDown(keyCode, event);
	}
}
