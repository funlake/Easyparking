package com.easyparking;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.easyparking.helper.Helper;
import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;
import com.easyparking.helper.Config;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class MyapplyActivity extends BaseActivity {

	private PullToRefreshListView myapplylist;
	private Resources resources;
	private String sid = null;
	private Button addapply;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myapply);
		resources = getResources();
		initStatusLang();
		initList();
		initBtn();
		Config.myapply = this;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	
	private void initBtn() {
		addapply = (Button) findViewById(R.id.addapply);
		addapply.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showProgress("转向申请页面中...");
				Intent intent = new Intent(MyapplyActivity.this,
						ChooseposActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				getParent().startActivityForResult(intent, 200);
				getParent().overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});
	}

	private void initList() {
		myapplylist = (PullToRefreshListView) findViewById(R.id.myapplylist);
		myapplylist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		myapplylist.setTextFilterEnabled(false);
		myapplylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				myapplylist.setItemChecked(position, true);
				//sid = getColDataByKey(position, "state");
				String state = getColDataByKey(position, "state");
				//if(state.equals("waitforconfirm") || state.equals("approved") || state.equals("success")){
					showProgress("转向详情页面中...");
					Intent intent = new Intent(MyapplyActivity.this,ApplyinfoActivity.class);
					intent.putExtra("apply_id", getColDataByKey(position, "_id"));
					intent.putExtra("spot_id", getColDataByKey(position, "spot_id"));
					intent.putExtra("apply_state", state);
					getParent().startActivityForResult(intent, 110);
				//}
					getParent().overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
			}
		});
		myapplylist.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				refreshData();
			}
		});
		showProgress("数据加载中...");
		refreshData();
	}

	public void refreshData() {
		// showProgress(getString(R.string.loading_data));
		new HttpRequest(new HttpCallback() {
			public void run(String res) {
				try {
					JSONObject r = new JSONObject(res);
					if ("success".equals(r.getString("code"))) {
						listdata = r.getJSONArray("result");
						if (listdata.length() > 0) {
							myapplylist.setAdapter(new spotAdapter(
									MyapplyActivity.this));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					showErrorMessage("错误:" + e.getMessage() + ",请检查您的网络连接");
				}
				myapplylist.onRefreshComplete();
				hideProgress();
			}
		}).execute("/myapply/" + Helper.getUid(this), "GET", null);
	}

	public final class ViewHolder {

		public TextView address;
		//public TextView code;
		public TextView state;
		public TextView period;
		public TextView owner;
	}

	public class spotAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public spotAdapter(Context context) {
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
				convertView = mInflater.inflate(R.layout.myapply_row, parent,
						false);
				holder = new ViewHolder();
				// JSONObject loc;
				holder.address = (TextView) convertView
						.findViewById(R.id.address);
				//holder.code = (TextView) convertView.findViewById(R.id.code);
				holder.state = (TextView) convertView.findViewById(R.id.status);
				
				holder.period = (TextView) convertView.findViewById(R.id.period);
				holder.owner = (TextView) convertView.findViewById(R.id.owner);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final JSONObject row;
			try {
				row = listdata.getJSONObject(position);
				JSONObject spotinfo = row.getJSONObject("spotinfo");
				JSONObject userinfo = spotinfo.getJSONObject("userinfo");
				holder.address.setText(spotinfo.getString("address")+"("+spotinfo.getString("code")+")");
				holder.owner.setText(userinfo.getString("user"));
				//holder.code.setText(spotinfo.getString("code"));
				holder.state.setBackgroundColor(resources.getColor(resources
						.getIdentifier("Status_" + row.getString("state"),
								"color", getPackageName())));
				holder.state.setText(ApplyStatus.get(row.getString("state")));
				holder.period.setText(Helper.showDateTime(row.getString("start_time"))+" ~ "+Helper.showDateTime(row.getString("end_time")));
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
			// onBackPressed();
			// Toast.makeText(MyspotActivity.this, "hehe",
			// Toast.LENGTH_LONG).show();
			// finish();
			Config.core.onBackPressed();
		}
		return true;
		// return super.onKeyDown(keyCode, event);
	}

	public void onResultBack(int resultCode, int resultCode2, Intent data) {
		// TODO Auto-generated method stub
		hideProgress();
		switch (resultCode) {
			case 101:
			case 111:
				refreshData();
			break;
		}
	}
}
