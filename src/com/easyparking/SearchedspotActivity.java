package com.easyparking;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ActionMode;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyparking.helper.Helper;
import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class SearchedspotActivity extends BaseActivity {
	private PullToRefreshListView searchspotslist;
	private String lng;
	private String lat;
	private String beginning;
	private String end;
	// private Resources resources;
	private String uid;
	private String sid = null;
	private Button applybtn;
	private RelativeLayout toolbar;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchedspots);
		uid = Helper.getUid(this);
		// resources = getResources();
		initParams();
		initList();
		initBtns();
		initStatusLang();
	}

	public void initParams() {
		Intent intent = getIntent();

		String lnglat = intent.getStringExtra("lnglat");
		String[] ll = lnglat.split(",");
		lng = ll[0];
		lat = ll[1];

		String times = intent.getStringExtra("parking_time");
		String se[] = times.split(",");
		beginning = se[0];
		end = se[1];
	}

	@SuppressLint("InlinedApi")
	public void initList() {
		toolbar = (RelativeLayout) findViewById(R.id.searchspotbanner);
		searchspotslist = (PullToRefreshListView) findViewById(R.id.searchspotslist);
		//searchspotslist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		//searchspotslist.setMultiChoiceModeListener(new ModeCallback());
		searchspotslist.setTextFilterEnabled(false);
		searchspotslist
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						searchspotslist.setItemChecked(position, true);
//						String spot_id = getColDataByKey(position, "_id");
//						Intent intent = new Intent(SearchedspotActivity.this,
//								SearchedspotinfoActivity.class);
//						intent.putExtra("spot_id", spot_id);
//						intent.putExtra("beginning", beginning);
//						intent.putExtra("end", end);
//						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//						startActivity(intent);

					}
				});
		searchspotslist.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				refreshData();
			}
		});
		showProgress("数据加载中...");
		refreshData();
	}

	public void initBtns() {
		ImageButton back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//setResult(101);
				finish();
			}
		});
	}

	// applybtn = (Button) findViewById(R.id.addapply);
	// applybtn.setOnClickListener(new View.OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// if(sid == null){
	// showErrorMessage("请选择车位");
	// return;
	// }
	// showCfmMsg("警告", "确定申请所选车位？",
	// // if user confirm to delete
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// // TODO Auto-generated method stub
	// new HttpRequest(new HttpCallback() {
	//
	// @Override
	// public void run(String res) {
	// // TODO Auto-generated method stub
	// try {
	// JSONObject r = new JSONObject(res);
	// if ("error".equals(r.getString("code"))) {
	// showErrorMessage(r.getString("msg"));
	// } else {
	// showSuccessMessage(r.getString("msg"));
	// refreshData();
	// }
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// showErrorMessage(e.getMessage());
	// }
	//
	// }
	//
	// }).execute(
	// "/apply_add/"
	// + Helper.getUid(SearchedspotActivity.this),
	// "POST", "spot_id=" + sid + "&beginning="+beginning+"&end="+end);
	// }
	// },
	// CFM_CANCELCB);
	//
	// }
	// });
	//
	// }

	public void refreshData() {
		// showProgress(getString(R.string.loading_data));
		new HttpRequest(new HttpCallback() {
			public void run(String res) {
				try {
					JSONObject r = new JSONObject(res);
					if ("success".equals(r.getString("code"))) {
						// if(r.getInt("total") > 0){
						listdata = r.getJSONArray("result");

						searchspotslist.setAdapter(new spotAdapter(
								SearchedspotActivity.this));
						if (listdata.length() > 0) {
							// searchspotslist.setItemChecked(0,true);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					showErrorMessage(e.getMessage());
				}
				searchspotslist.onRefreshComplete();
				hideProgress();
			}
		}).execute("/spot_point_find/" + lng + "/" + lat + "/" + uid, "GET",
				null);
	}

	public final class ViewHolder {

		public TextView address;
		public TextView owner;
		public TextView success_count;
		public TextView desc;
		public Button apply;
		public Button view_comment;
		// public TextView state;
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
				convertView = mInflater.inflate(R.layout.searchedspots_row,
						parent, false);
				holder = new ViewHolder();
				// JSONObject loc;
				holder.address = (TextView) convertView
						.findViewById(R.id.address);
				holder.owner = (TextView) convertView.findViewById(R.id.owner);
				holder.desc  = (TextView) convertView.findViewById(R.id.desc);
				holder.apply = (Button) convertView.findViewById(R.id.apply_btn);
				holder.view_comment = (Button) convertView.findViewById(R.id.view_comment_btn);
			
				//holder.success_count = (TextView) convertView.findViewById(R.id.success_count);
				// holder.state = (TextView)
				// convertView.findViewById(R.id.status);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final JSONObject row;
			try {
				row = listdata.getJSONObject(position);
				JSONObject userinfo = row.getJSONObject("userinfo");
				holder.address.setText(row.getString("address")+"("+row.getString("code")+")");
				holder.owner.setText(userinfo.getString("user"));
				holder.desc.setText(row.getString("desc"));
				final String spot_id = row.getString("_id");
				holder.view_comment.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(SearchedspotActivity.this,CommentActivity.class);
						intent.putExtra("spot_id", spot_id);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				});
				holder.apply.setOnClickListener(new View.OnClickListener() {

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
													}
												} catch (JSONException e) {
													// TODO Auto-generated catch block
													showErrorMessage(e.getMessage());
												}
												
											}

										}).execute(
												"/apply_add/"
														+ Helper.getUid(SearchedspotActivity.this),
												"POST", "spot_id=" + spot_id
														+ "&beginning=" + beginning
														+ "&end=" + end);
									}
								}, CFM_CANCELCB);
						
					}
				});
				//holder.success_count.setText(row.getInt("success_count")+"");
				//holder.code.setText(row.getString("code"));
				// holder.state.setBackgroundColor(resources.getColor(resources
				// .getIdentifier("Status_" + row.getString("state"),
				// "color", getPackageName())));
				// holder.state.setText(ApplyStatus.get(row.getString("state")));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				showErrorMessage(e.getMessage());
			}
			return convertView;
		}

	}

	private class ModeCallback implements ListView.MultiChoiceModeListener {
		private int lastposition = -1;

		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.searchspot_action, menu);
			mode.setTitle("申请车位");

			toolbar.setVisibility(View.GONE);

			return true;
		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return true;
		}

		public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
			final String sid = getColDataByKey(lastposition, "_id");
			switch (item.getItemId()) {
			case R.id.apply:
				showCfmMsg("警告", "确定申请所选车位？",
				// if user confirm to delete
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
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
												
												refreshData();
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											showErrorMessage(e.getMessage());
										}
										mode.finish();
									}

								}).execute(
										"/apply_add/"
												+ Helper.getUid(SearchedspotActivity.this),
										"POST", "spot_id=" + sid
												+ "&beginning=" + beginning
												+ "&end=" + end);
							}
						}, CFM_CANCELCB);
				break;
			default:
				break;
			}
			return true;
		}

		public void onDestroyActionMode(ActionMode mode) {
			toolbar.setVisibility(View.VISIBLE);
			// selectedIds = null;
			lastposition = -1;
		}

		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
			if (lastposition == -1) {
				if (checked) {
					lastposition = position;
				}
			} else {
				if (lastposition != position) {
					searchspotslist.setItemChecked(lastposition, false);
					lastposition = position;
				}
				// selectedIds = null;
			}
			final int checkedCount = searchspotslist.getCheckedItemCount();

			switch (checkedCount) {
			case 0:
				toolbar.setVisibility(View.VISIBLE);
				mode.setSubtitle(null);
				break;
			default:
				mode.setSubtitle("申请选择的车位");

				break;
			}
		}

	}
}
