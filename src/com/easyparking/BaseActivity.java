package com.easyparking;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.easyparking.helper.Config;
import com.easyparking.service.RefreshdataService;
import com.gitonway.niftydialogeffects.widget.niftydialogeffects.Effectstype;
import com.gitonway.niftydialogeffects.widget.niftydialogeffects.NiftyDialogBuilder;

public class BaseActivity extends Activity {
	private Dialog loadingDialog = null;
	private Window loadingDialogWindow;
	private TextView loadingContent;
	protected JSONArray listdata;
	protected HashMap<String, String> ApplyStatus = new HashMap<String, String>();
	protected HashMap<String, String> SpotStatus = new HashMap<String, String>();
	protected HashMap<String, String> CommentAttitude = new HashMap<String, String>();
	protected NiftyDialogBuilder confirmAlg;

	public void showSuccessMessage(String msg) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_success_toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText(msg);
		Toast toast = new Toast(this);
		// toast.setText(msg);
		toast.setGravity(Gravity.BOTTOM, 0, 150);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	public void showErrorMessage(String msg) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_error_toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText(msg);

		Toast toast = new Toast(this);
		// toast.setText(msg);
		toast.setGravity(Gravity.BOTTOM, 0, 150);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	View.OnClickListener CFM_CANCELCB = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// dialog.dismiss();
			// v.setVisibility(View.GONE);
			confirmAlg.cancel();
		}
	};

	public void showCfmMsg(String title, String messsage,
			View.OnClickListener yesfn, View.OnClickListener cancelfn) {
		// confirmAlg = new AlertDialog.Builder(this).create();
		// confirmAlg.show();
		// confirmAlg.setContentView(R.layout.custom_cfm);
		//
		// Window content = confirmAlg.getWindow();
		// content.setWindowAnimations(R.anim.alpha_in);
		// TextView tt = (TextView)content.findViewById(R.id.title);
		// TextView bd = (TextView)content.findViewById(R.id.body);
		// Button cancel = (Button)content.findViewById(R.id.cancel);
		// Button confirm = (Button)content.findViewById(R.id.confirm);
		// tt.setText(title);
		// bd.setText(messsage);
		// cancel.setOnClickListener(cancelfn);
		// confirm.setOnClickListener(yesfn);
		NiftyDialogBuilder.instance = null;
		confirmAlg = NiftyDialogBuilder.getInstance(this);
		confirmAlg.withTitle(title) // .withTitle(null) no title
				.withTitleColor("#FFFFFF") // def
				.withDividerColor("#11000000") // def
				.withMessage(messsage) // .withMessage(null) no Msg
				.withMessageColor("#FFFFFF") // def
				// .withIcon(getResources().getDrawable(R.drawable.icon))
				.isCancelableOnTouchOutside(true) // def | isCancelable(true)
				.withDuration(400) // def
				.withEffect(Effectstype.Sidefill) // def Effectstype.Slidetop
				.withButton1Text("取消") // def gone
				.withButton2Text("确认") // def gone
				// .setCustomView(R.layout.custom_view,v.getContext())
				// //.setCustomView(View or ResId,context)
				.setButton1Click(cancelfn).setButton2Click(yesfn).show();

	}

	public void showCfmMsg(String title, String messsage, String btn1text,
			String btn2text, View.OnClickListener cancelfn,
			View.OnClickListener yesfn) {
		NiftyDialogBuilder.instance = null;
		confirmAlg = NiftyDialogBuilder.getInstance(this);
		confirmAlg.withTitle(title) // .withTitle(null) no title
				.withTitleColor("#FFFFFF") // def
				.withDividerColor("#11000000") // def
				.withMessage(messsage) // .withMessage(null) no Msg
				.withMessageColor("#FFFFFF") // def
				// .withIcon(getResources().getDrawable(R.drawable.icon))
				.isCancelableOnTouchOutside(true) // def | isCancelable(true)
				.withDuration(400) // def
				.withEffect(Effectstype.Sidefill) // def Effectstype.Slidetop
				.withButton1Text(btn1text) // def gone
				.withButton2Text(btn2text) // def gone
				// .setCustomView(R.layout.custom_view,v.getContext())
				// //.setCustomView(View or ResId,context)
				.setButton1Click(cancelfn).setButton2Click(yesfn).show();
	}

	@SuppressLint("InlinedApi")
	public void showProgress(String msg) {
		if (loadingDialog == null) {
			loadingDialog = new Dialog(this, R.style.Dialog);
			loadingDialog.setContentView(R.layout.custom_progress);
			loadingDialogWindow = loadingDialog.getWindow();
			loadingContent = (TextView) loadingDialogWindow
					.findViewById(R.id.progress_content);
			WindowManager.LayoutParams lp = loadingDialogWindow.getAttributes();
			// lp.y = 200;
			lp.alpha = 0.95f;
			loadingDialogWindow.setGravity(Gravity.CENTER);
			loadingDialogWindow.setAttributes(lp);
			loadingDialog.setCancelable(false);
		}
		loadingContent.setText(msg);
		loadingDialog.show();
	}

	public void hideProgress() {
		if (loadingDialog != null) {
			loadingDialog.dismiss();
		}
	}

	protected boolean isOpenNetwork() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}

		return false;
	}

	public void initStatusLang() {
		SpotStatus.put("normal", getString(R.string.spot_state_normal));
		SpotStatus.put("applying", getString(R.string.spot_state_applying));
		SpotStatus.put("waitforconfirm",
				getString(R.string.spot_state_waitforconfirm));
		SpotStatus.put("approved", getString(R.string.spot_state_approved));
		SpotStatus.put("success", getString(R.string.spot_state_success));
		SpotStatus.put("fail", getString(R.string.spot_state_fail));
		SpotStatus.put("expired", getString(R.string.spot_state_expired));

		ApplyStatus.put("normal", getString(R.string.apply_state_normal));
		ApplyStatus.put("applying", getString(R.string.apply_state_applying));
		ApplyStatus.put("waitforconfirm",
				getString(R.string.apply_state_waitforconfirm));
		ApplyStatus.put("approved", getString(R.string.apply_state_approved));
		ApplyStatus.put("success", getString(R.string.apply_state_success));
		ApplyStatus.put("fail", getString(R.string.apply_state_fail));
		ApplyStatus.put("expired", getString(R.string.apply_state_expired));

		CommentAttitude.put("good", getString(R.string.good));
		CommentAttitude.put("soso", getString(R.string.soso));
		CommentAttitude.put("bad", getString(R.string.bad));
	}

	/** list view method **/
	public JSONObject getRowByPosition(int position) {
		JSONObject row = null;
		try {
			row = (JSONObject) listdata.get(position);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return row;
	}

	public String getColDataByKey(int position, String key) {
		String content = "";
		try {
			// since we have scroll up load more widget in listview,that thing
			// has already took 1 row in table
			// so here row of list should decrease 1 to match the real position
			// in data array.
			content = getRowByPosition(position - 1).getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		return content;
	}

	public JSONObject getColObjectByKey(int position, String key) {
		JSONObject content = null;
		try {
			// since we have scroll up load more widget in listview,that thing
			// has already took 1 row in table
			// so here row of list should decrease 1 to match the real position
			// in data array.
			content = getRowByPosition(position - 1).getJSONObject(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		return content;
	}

	public JSONArray getColArrayByKey(int position, String key) {
		JSONArray content = null;
		try {
			// since we have scroll up load more widget in listview,that thing
			// has already took 1 row in table
			// so here row of list should decrease 1 to match the real position
			// in data array.
			content = getRowByPosition(position - 1).getJSONArray(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		return content;
	}

	public Integer getColIntByKey(int position, String key) {
		Integer content = 0;
		try {
			// since we have scroll up load more widget in listview,that thing
			// has already took 1 row in table
			// so here row of list should decrease 1 to match the real position
			// in data array.
			content = getRowByPosition(position - 1).getInt(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		return content;
	}

	public Boolean getColBooleanByKey(int position, String key) {
		Boolean content = false;
		try {
			// since we have scroll up load more widget in listview,that thing
			// has already took 1 row in table
			// so here row of list should decrease 1 to match the real position
			// in data array.
			content = getRowByPosition(position - 1).getBoolean(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		return content;
	}

	public void startRefreshService() {
		// showSuccessMessage("开始刷新服务");
		Intent intent = new Intent(this, RefreshdataService.class);
		startService(intent);
	}

	public void stopRefreshService() {
		Intent intent = new Intent(this, RefreshdataService.class);
		stopService(intent);
	}

	public void activityLogout() {
		Config.core = null;
		Config.myapply = null;
		Config.myspot = null;
		Config.profile = null;
	}
}
