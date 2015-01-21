package com.easyparking;

import java.util.ArrayList;
import java.util.List;
import com.easyparking.helper.IndexViewPager;
import com.easyparking.helper.Config;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class AddposActivity extends Activity {
	LocalActivityManager manager = null;
	IndexViewPager pager = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// this.getActionBar().hide();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addpos);
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);
		initApViewPager();
	}
	
	private void initApViewPager(){
		// TODO Auto-generated method stub
		pager = (IndexViewPager) findViewById(R.id.apviewpager);

		final ArrayList<View> list = new ArrayList<View>();

		Intent ap = new Intent(AddposActivity.this, Addpos1Activity.class);
		ap.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		list.add(getView("ap", ap));
		
		Intent ap2 = new Intent(AddposActivity.this, Addpos2Activity.class);
		ap2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		list.add(getView("ap2", ap2));		
		
		pager.setAdapter(new MyPagerAdapter(list));
		
		Config.v2 = pager;
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
	}
}
