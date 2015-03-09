package com.easyparking;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.MapView;
import com.easyparking.helper.Config;
import com.easyparking.helper.Helper;
import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class MyspotActivity extends BaseActivity {
	public MapView mMapView;
	private PullToRefreshListView myspotsList;
	// private JSONArray data;
	private spotAdapter sdt;
	private RelativeLayout toolbar;
	private String selectedIds;
	private String uid;
	private Resources resources;
	private boolean isInit = false;

	protected void onCreate(Bundle savedInstanceState) {
		Config.myspot = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myspot);
	}

	public void initialize() {
		if (!isInit) {
			toolbar = (RelativeLayout) findViewById(R.id.spotbanner);
			uid = Helper.getUid(this);
			resources = getResources();
			initBtns();
			initList();
			initStatusLang();
			isInit = true;
		}
	}

	public void initBtns() {
		Button abtn = (Button) findViewById(R.id.addspot);
		abtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Toast.makeText(MyspotActivity.this,
				// Helper.getSetting(MyspotActivity.this,"uid"),Toast.LENGTH_LONG).show();
				// dlg = new Dialog(MyspotActivity.this,R.style.Dialog);
				// dlg.setContentView(R.layout.addspotform);
				// dlg.show();
				showProgress("转向添加页面中...");
				Intent intent = new Intent(MyspotActivity.this,
						Addpos1Activity.class);
				getParent().startActivityForResult(intent, 200);
				getParent().overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}

		});
	}

	public void initList() {
		myspotsList = (PullToRefreshListView) findViewById(R.id.myspotslist);
		sdt = new spotAdapter(MyspotActivity.this);
		// myspotsList.setAdapter(sdt);
		showProgress("数据加载中...");
		refreshData();
		initListAction();

	}

	@SuppressLint("InlinedApi")
	public void initListAction() {
		myspotsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		myspotsList.setMultiChoiceModeListener(new ModeCallback());
		myspotsList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				refreshData();

			}

		});
		myspotsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// myspotsList.setItemChecked(position, true);
				if (getColBooleanByKey(position, "new_apply")) {
					// go to spot's apply page directly
					Intent intent = new Intent(MyspotActivity.this,
							SpotapplyActivity.class);
					intent.putExtra("spot_id", getColDataByKey(position, "_id"));
					intent.putExtra("address",
							getColDataByKey(position, "address"));
					getParent().startActivityForResult(intent, 210);

				} else {
					Intent intent = new Intent(MyspotActivity.this,
							SpotinfoActivity.class);
					intent.putExtra("spot_id", getColDataByKey(position, "_id"));
					getParent().startActivityForResult(intent, 210);
				}
				getParent().overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});
	}

	private class ModeCallback implements ListView.MultiChoiceModeListener {

		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.myspot_action, menu);
			mode.setTitle("管理车位");
			toolbar.setVisibility(View.GONE);

			return true;
		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return true;
		}

		public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.remove:
				SparseBooleanArray checkedItems = myspotsList
						.getCheckedItemPositions();

				for (int i = 0; i < myspotsList.getAdapter().getCount(); i++) {
					if (checkedItems.get(i)) {
						// Do something
						if (selectedIds == null) {
							selectedIds = getColDataByKey(i, "_id");
						} else {
							selectedIds += "," + getColDataByKey(i, "_id");
						}
					}
				}
				// Toast.makeText(
				// MyspotActivity.this,
				// selectedIds, Toast.LENGTH_SHORT).show();
				// mode.finish();
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
												mode.finish();
												refreshData();
											}
											showSuccessMessage(r
													.getString("msg"));
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											showErrorMessage(e.getMessage());
										}
									}

								}).execute("/spot_remove/" + uid, "POST",
										"ids=" + selectedIds);
							}
						}, CFM_CANCELCB);
				break;
			default:
				Toast.makeText(MyspotActivity.this,
						"Clicked " + item.getTitle(), Toast.LENGTH_SHORT)
						.show();
				break;
			}
			return true;
		}

		public void onDestroyActionMode(ActionMode mode) {
			toolbar.setVisibility(View.VISIBLE);
			selectedIds = null;
		}

		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
			final int checkedCount = myspotsList.getCheckedItemCount();

			switch (checkedCount) {
			case 0:
				toolbar.setVisibility(View.VISIBLE);
				mode.setSubtitle(null);
				break;
			default:
				mode.setSubtitle("您已选择" + checkedCount + "项");

				break;
			}
		}

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
						myspotsList.setAdapter(sdt);
						myspotsList.onRefreshComplete();
						// }
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
		}).execute("/myspots/" + uid, "GET", null);
	}

	public final class ViewHolder {

		public TextView address;
		public TextView state;
		public RatingBar rating;
		public TextView apply_count;
		public TextView desc;
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
				convertView = mInflater.inflate(R.layout.myspot_row, parent,
						false);
				holder = new ViewHolder();

				// JSONObject loc;
				holder.address = (TextView) convertView
						.findViewById(R.id.myspot_address);
				holder.rating = (RatingBar) convertView
						.findViewById(R.id.myspot_rating);
				holder.state = (TextView) convertView
						.findViewById(R.id.myspot_state);
				holder.apply_count = (TextView) convertView
						.findViewById(R.id.myspot_apply_amount);
				holder.desc = (TextView) convertView
						.findViewById(R.id.myspot_desc);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			final JSONObject row;
			try {
				row = listdata.getJSONObject(position);
				holder.address.setText(row.getString("address") + "("
						+ row.getString("code") + ")");
				// showErrorMessage(row.getDouble("rating")+"");
				holder.rating.setRating((float) row.getDouble("rating"));
				holder.state.setBackgroundColor(resources.getColor(resources
						.getIdentifier("Status_" + row.getString("state"),
								"color", getPackageName())));
				holder.state.setText(SpotStatus.get(row.getString("state")));
				holder.desc.setText(row.getString("desc"));
				boolean n = row.getBoolean("new_apply");
				if (n) {
					holder.apply_count.setText("新");
					holder.apply_count.setPadding(4, 4, 4, 4);
				} else {
					holder.apply_count.setPadding(0, 0, 0, 0);
				}
				// holder.apply_count.setText(row.getString("apply_count"));
				// holder.remove.setOnClickListener(new View.OnClickListener() {
				// @Override
				// public void onClick(View v) {
				// // TODO Auto-generated method stub
				// try {
				// new HttpRequest(new HttpCallback() {
				// public void run(String res) {
				// // Toast.makeText(MainActivity.this,
				// // res,Toast.LENGTH_LONG).show();
				// // Log.i("http_result",res);
				// initList();
				// }
				// }).execute("/spot_remove/" + row.getString("_id"),
				// "GET", null);
				// } catch (JSONException e) {
				// // TODO Auto-generated catch block
				// Toast.makeText(MyspotActivity.this, e.getMessage(),
				// Toast.LENGTH_SHORT).show();
				// }
				// }
				// });
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

	public void onResultBack(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		hideProgress();
		switch (resultCode) {
		// add spot/set apply status
		case 201:
			// remove spot
		case 213:

			// case 211:
			refreshData();
			break;
		}

		// showSuccessMessage("返回啦！");
	}
}
