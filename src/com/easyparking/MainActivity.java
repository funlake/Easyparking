package com.easyparking;

import java.util.ArrayList;
import java.util.List;

import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyparking.helper.Helper;
import com.easyparking.helper.IndexViewPager;
import com.easyparking.helper.Config;
import com.igexin.sdk.PushManager;

public class MainActivity extends BaseActivity {
	@SuppressWarnings("deprecation")
	LocalActivityManager manager = null;
	Context context = null;
	IndexViewPager pager = null;
	public MyPagerAdapter viewAdapter = null;
	public View view1;
	public View view2;
	public View view3;
	// private AlertDialog alg;

	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!checkLogin()){
			showProgress("转向登陆页面中...");
			Intent intent = new Intent(MainActivity.this,LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			hideProgress();
			finish();
		}
		else{
			setContentView(R.layout.main);
			context = MainActivity.this;
			manager = new LocalActivityManager(this, true);
			manager.dispatchCreate(savedInstanceState);
			initPagerViewer();
			initBottomClick();
			//initFirstTime();
			Config.core = this;
			//if(Helper.isFirstRun(this)){
				PushManager.getInstance().initialize(MainActivity.this.getApplicationContext());
			//}
			//refresh myapply and myspot data
//			startRefreshService();
		}
	}

	public boolean checkLogin(){
		if(Helper.getSetting(this,"userid").equals("")){
			return false;
		}
		return true;
	}
	public void refreshAdpater(){
		viewAdapter.notifyDataSetChanged();
	}
	private void initPagerViewer() {
		// TODO Auto-generated method stub
		pager = (IndexViewPager) findViewById(R.id.viewpage);

		final ArrayList<View> list = new ArrayList<View>();
		Intent ma = new Intent(context, MyapplyActivity.class);
		ma.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		list.add(getView("ma", ma));
//		Intent ca = new Intent(context, ChooseposActivity.class);
//		ca.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		list.add(getView("ca", ca));

		Intent ms = new Intent(context, MyspotActivity.class);

		ms.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		list.add(getView("ms", ms));
		

		Intent st = new Intent(context, ProfilerActivity.class);

		ms.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		list.add(getView("st", st));
		viewAdapter = new MyPagerAdapter(list);
		pager.setAdapter(viewAdapter);

	}

	private void initBottomClick() {
		view1 = (View) findViewById(R.id.view1);
		view2 = (View) findViewById(R.id.view2);
		view3 = (View) findViewById(R.id.view3);
		//final View view4 = (View) findViewById(R.id.view4);
		final Resources resource = getResources();
		view1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pager.setCurrentItem(0);
				view1.setBackgroundDrawable(resource.getDrawable(R.drawable.bghl));
				view2.setBackgroundDrawable(resource.getDrawable(R.drawable.bg));
				view3.setBackgroundDrawable(resource.getDrawable(R.drawable.bg));
				//view4.setBackgroundColor(resource.getColor(R.color.Mainbg));
			}
		});
		//default to first view
		view1.performClick();
		view2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pager.setCurrentItem(1);
				Config.myspot.initialize();
				view1.setBackgroundDrawable(resource.getDrawable(R.drawable.bg));
				view2.setBackgroundDrawable(resource.getDrawable(R.drawable.bghl));
				view3.setBackgroundDrawable(resource.getDrawable(R.drawable.bg));
				//view4.setBackgroundColor(resource.getColor(R.color.Mainbg));
			}
		});

		view3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pager.setCurrentItem(2);
				view1.setBackgroundDrawable(resource.getDrawable(R.drawable.bg));
				view2.setBackgroundDrawable(resource.getDrawable(R.drawable.bg));
				view3.setBackgroundDrawable(resource.getDrawable(R.drawable.bghl));
				//view4.setBackgroundColor(resource.getColor(R.color.Mainbg));
			}
		});
//
//		view4.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				view1.setBackgroundColor(resource.getColor(R.color.Mainbg));
//				view2.setBackgroundColor(resource.getColor(R.color.Mainbg));
//				view3.setBackgroundColor(resource.getColor(R.color.Mainbg));
//				view4.setBackgroundColor(resource.getColor(R.color.Highlight));
//			}
//		});
	}

	@SuppressWarnings("deprecation")
	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}

	public class MyPagerAdapter extends PagerAdapter {
		List<View> list = new ArrayList<View>();

		public MyPagerAdapter(ArrayList<View> list) {
			this.list = list;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			ViewPager pViewPager = ((ViewPager) container);
			pViewPager.removeView(list.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			ViewPager pViewPager = ((ViewPager) arg0);
			pViewPager.addView(list.get(arg1));
			return list.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
		
		@Override
		public int getItemPosition(Object object) {  
		    return POSITION_NONE;  
		} 
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBackPressed() {		
		showCfmMsg("警告","确定要退出应用吗?",new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//dialog.dismiss();
				Config.scheduleStatus = false;
				Config.timer.cancel();
				//PushManager.getInstance().initialize(MainActivity.this.getApplicationContext());
//				stopRefreshService();
				finish();
			}
		},CFM_CANCELCB);
		// super.onBackPressed();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		hideProgress();
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackPressed();

		}

		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		//my spots got returns from add process.
		//showErrorMessage("is this happen in myspotActivity"+resultCode);
		if(resultCode > 200 && resultCode < 300){
			@SuppressWarnings("deprecation")
			MyspotActivity activity = (MyspotActivity) manager.getActivity("ms");
			activity.onResultBack(resultCode, resultCode, data);
			//showErrorMessage("is this happen in myspotActivity");
		}
		if(resultCode > 100 && resultCode < 200){
			@SuppressWarnings("deprecation")
			MyapplyActivity activity = (MyapplyActivity) manager.getActivity("ma");
			activity.onResultBack(resultCode, resultCode, data);
			//showErrorMessage("is this happen in myspotActivity");
		}
		super.onActivityResult(requestCode, resultCode, data);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// //Log.e("pressed what?", "Key down = " + keyCode);
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// return true;
	// } else {
	// return super.onKeyDown(keyCode, event);
	// }
	// }

    //Activity创建或者从后台重新回到前台时被调用  
    @Override  
    protected void onStart() {
    	//pager.setCurrentItem(pager.getCurrentItem());
        super.onStart();  
        //Log.i("lifecircle", "onStart called.");  
    }  
      
//    //Activity从后台重新回到前台时被调用  
//    @Override  
//    protected void onRestart() {  
//        super.onRestart();  
//        Log.i("lifecircle", "onRestart called.");  
//    }  
//      
//    //Activity创建或者从被覆盖、后台重新回到前台时被调用  
//    @Override  
//    protected void onResume() {  
//        super.onResume();  
//        Log.i("lifecircle", "onResume called.");  
//    }  
      
    //Activity窗口获得或失去焦点时被调用,在onResume之后或onPause之后  
    /*@Override 
    public void onWindowFocusChanged(boolean hasFocus) { 
        super.onWindowFocusChanged(hasFocus); 
        Log.i(TAG, "onWindowFocusChanged called."); 
    }*/  
      
    //Activity被覆盖到下面或者锁屏时被调用  
//    @Override  
//    protected void onPause() {  
//        super.onPause();  
//        Log.i("lifecircle", "onPause called.");  
//        //有可能在执行完onPause或onStop后,系统资源紧张将Activity杀死,所以有必要在此保存持久数据  
//    }  
//      
//    //退出当前Activity或者跳转到新Activity时被调用  
//    @Override  
//    protected void onStop() {  
//        super.onStop();  
//        Log.i("lifecircle", "onStop called.");     
//    }  
//      
//    //退出当前Activity时被调用,调用之后Activity就结束了  
//    @Override  
//    protected void onDestroy() {  
//        super.onDestroy();  
//        Log.i("lifecircle", "onDestory called.");  
//    }  
//      
//    /** 
//     * Activity被系统杀死时被调用. 
//     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死. 
//     * 另外,当跳转到其他Activity或者按Home键回到主屏时该方法也会被调用,系统是为了保存当前View组件的状态. 
//     * 在onPause之前被调用. 
//     */  
//    @Override  
//    protected void onSaveInstanceState(Bundle outState) {  
//        
//        Log.i("lifecircle", "onSaveInstanceState called. put param: " );  
//        super.onSaveInstanceState(outState);  
//    }  
//      
//    /** 
//     * Activity被系统杀死后再重建时被调用. 
//     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死,用户又启动该Activity. 
//     * 这两种情况下onRestoreInstanceState都会被调用,在onStart之后. 
//     */  
//    @Override  
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {  
//        Log.i("lifecircle", "onRestoreInstanceState called. get param: ");  
//        super.onRestoreInstanceState(savedInstanceState);  
//    }
}
