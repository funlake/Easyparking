package com.easyparking;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;
import com.easyparking.helper.RemoteImageHelper;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class ShopActivity extends BaseActivity {
	private PullToRefreshListView shoplist;
	private RemoteImageHelper lazyImageHelper = new RemoteImageHelper();
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop);
		initViews();
		
	}
	public void initViews(){
		initBtns();
		initList();
	}
	public void initBtns(){
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
	
	public void initList(){
		shoplist = (PullToRefreshListView) findViewById(R.id.pointshoplist);
		shoplist.setTextFilterEnabled(false);
		shoplist.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				refreshData();
			}

		});
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
							shoplist.setAdapter(new shopAdapter(
									ShopActivity.this));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					showErrorMessage("错误:" + e.getMessage() + ",请检查您的网络连接");
				}
				shoplist.onRefreshComplete();
				hideProgress();
			}
		}).execute("/shop_goods", "GET", null);
	}
	public final class ViewHolder {

		public TextView title;
		public TextView desc;
		public TextView point_cost;
		public ImageView image;
	}

	public class shopAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public shopAdapter(Context context) {
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
				convertView = mInflater.inflate(R.layout.shop_row, parent,
						false);
				holder = new ViewHolder();
				// JSONObject loc;
				holder.title = (TextView) convertView
						.findViewById(R.id.product_title);
				holder.desc = (TextView) convertView.findViewById(R.id.product_desc);
				holder.point_cost = (TextView) convertView.findViewById(R.id.point_cost);
				holder.image = (ImageView) convertView.findViewById(R.id.image);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			final JSONObject row;
			try {
				row = listdata.getJSONObject(position);
				holder.title.setText(row.getString("title"));
				holder.desc.setText(row.getString("desc"));
				holder.point_cost.setText(row.getString("points"));
				lazyImageHelper.loadImage(holder.image, "http://192.168.0.103:9528/uploads/"+row.getString("img_thumb"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return convertView;
		}

	}

}
