package com.easyparking;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.easyparking.helper.Helper;
import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class CommentActivity extends BaseActivity {
	private ListView commentlist;
	private String spot_id;
	private Resources resources;
	private JSONArray originalData;
	private RadioButton good;
	private RadioButton soso;
	private RadioButton bad;
	private int good_amount = 0;
	private int soso_amount = 0;
	private int bad_amount = 0;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment);
		resources = getResources();
		initStatusLang();
		initParams();
		initViews();
		
	}
	private void initParams() {
		Intent i = getIntent();
		spot_id = i.getStringExtra("spot_id");
	}
	private void initViews(){
		initList();
		initBtns();
	}
	private void initBtns(){
		good = (RadioButton) findViewById(R.id.good);
		soso = (RadioButton) findViewById(R.id.soso);
		bad = (RadioButton) findViewById(R.id.bad);
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
	private void initFilter(){
		good_amount = 0;
		soso_amount = 0;
		bad_amount  = 0;
		RadioGroup attitude = (RadioGroup) findViewById(R.id.attitude);
		RadioButton all = (RadioButton) findViewById(R.id.all);
		all.setText(getString(R.string.all)+"("+originalData.length()+")");
		final HashMap<String, JSONArray> att_amount  = new HashMap<String,JSONArray>();
		att_amount.put("good", new JSONArray());
		att_amount.put("soso", new JSONArray());
		att_amount.put("bad", new JSONArray());
		for(int i=0,j=originalData.length();i<j;i++){
			try {
				JSONObject r = originalData.getJSONObject(i);
				String att   = r.getString("attitude");
				if(att.equals("good")){
					good_amount += 1;
					att_amount.get("good").put(r);
					
				}
				else if(att.equals("soso")){
					soso_amount += 1;
					att_amount.get("soso").put(r);
				}
				else if(att.equals("bad")){
					bad_amount += 1;
					att_amount.get("bad").put(r);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				
			}
		}
		good.setText(getString(R.string.good)+"("+good_amount+")");
		soso.setText(getString(R.string.soso)+"("+soso_amount+")");
		bad.setText(getString(R.string.bad)+"("+bad_amount+")");
		attitude.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				String atd = null;
				switch(checkedId){
					case R.id.all:

					break;
					
					case R.id.good:
						atd = "good";
					break;
					
					case R.id.soso:
						atd = "soso";
					break;
					
					case R.id.bad:
						atd = "bad";
					break;
				}
				//all
				listdata = originalData;
				//special attitude
				if(atd != null){
					listdata = att_amount.get(atd);
				}
				commentlist.setAdapter(new commentAdapter(CommentActivity.this));
			}
			
		});
	}
	private void initList(){
		commentlist = (ListView) findViewById(R.id.commentlist);
//		commentlist.setOnRefreshListener(new OnRefreshListener() {
//
//			@Override
//			public void onRefresh() {
//				// TODO Auto-generated method stub
//				refreshData();
//			}
//		});
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
						originalData = listdata;
						if (listdata.length() > 0) {
							commentlist.setAdapter(new commentAdapter(CommentActivity.this));
							initFilter();
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					showErrorMessage("错误:" + e.getMessage() + ",请检查您的网络连接");
				}
				//commentlist.onRefreshComplete();
				hideProgress();
			}
		}).execute("/comment_find/" + spot_id, "GET", null);
	}

	public final class ViewHolder {

		public TextView username;
		public TextView comment_date;
		public TextView comment_attitude;
		public TextView comment;
	}

	public class commentAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public commentAdapter(Context context) {
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
				convertView = mInflater.inflate(R.layout.comment_row, parent,
						false);
				holder = new ViewHolder();
				// JSONObject loc;
				holder.username = (TextView) convertView
						.findViewById(R.id.username);
				//holder.code = (TextView) convertView.findViewById(R.id.code);
				holder.comment_date = (TextView) convertView.findViewById(R.id.comment_date);
				holder.comment_attitude = (TextView) convertView.findViewById(R.id.comment_attitude);
				holder.comment = (TextView) convertView.findViewById(R.id.comment);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final JSONObject row;
			try {
				row = listdata.getJSONObject(position);
				JSONObject userinfo = row.getJSONObject("userinfo");
				holder.username.setText(userinfo.getString("user"));
				holder.comment_date.setText(Helper.showDateTime(row.getString("created_time")));
				holder.comment_attitude.setBackgroundColor(resources.getColor(resources
						.getIdentifier("Comment_" + row.getString("attitude"),
								"color", getPackageName())));
				holder.comment_attitude.setText(CommentAttitude.get(row.getString("attitude")));
				//holder.code.setText(spotinfo.getString("code"));
				holder.comment.setText(row.getString("comment"));
				String attitude = row.getString("attitude");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				showErrorMessage(e.getMessage());
			}
			return convertView;
		}

	}
}
