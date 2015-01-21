package com.easyparking.service;

import java.util.Timer;
import java.util.TimerTask;

import com.easyparking.helper.Config;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RefreshdataService extends Service {
	private Timer refreshTimer;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override 
	public void onCreate(){
		//Log.i("timer","fxxxxk");
		//Toast.makeText(getApplicationContext(),"fxxxk", Toast.LENGTH_LONG).show();
		super.onCreate();
	}
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
    	//Log.i("timer","fxxxxk2");
    	//Toast.makeText(getApplicationContext(),"fxxxk2", Toast.LENGTH_LONG).show();
    	return super.onStartCommand(intent, flags, startId);  
    }
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent,int startId){
		//Toast.makeText(getApplicationContext(),"fxxxk", Toast.LENGTH_LONG).show();
		//itn = intent;
		startTimer(0,1000*10);//即使执行,轮询间隔10秒
		super.onStart(intent, startId);
	}
	@Override
	public void onDestroy(){
		stopTimer();
		super.onDestroy();
	}
	
	private void startTimer(int timeout,int timeloop){
		refreshTimer = new Timer();
		refreshTimer.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.i("refreshdata","refreshing data");
				Config.myapply.refreshData();
				Config.myspot.refreshData();
				//Toast.makeText(getApplicationContext(),"fxxxk", Toast.LENGTH_SHORT).show();
				//System.out.println("fxxxxxk");
			}
			
		},timeout,timeloop);
	}
	
	private void stopTimer(){
		refreshTimer.cancel();
	}
}
