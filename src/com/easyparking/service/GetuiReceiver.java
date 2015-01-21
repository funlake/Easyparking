package com.easyparking.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.easyparking.helper.Helper;
import com.easyparking.helper.HttpCallback;
import com.easyparking.helper.HttpRequest;
import com.easyparking.helper.Config;
import com.igexin.sdk.PushConsts;

public class GetuiReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle bundle = intent.getExtras();
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
			//get message
			case PushConsts.GET_MSG_DATA:
				byte[] payload = bundle.getByteArray("payload");
				//MainActivity.view2.performClick();
				if (payload != null && Config.core!=null) {
					String data = new String(payload);
					Config.myapply.hideProgress();
					Config.myspot.hideProgress();
					if(data.equals("apply")){
						Config.myapply.refreshData();
						Config.core.view1.performClick();
					}
					else if(data.equals("spot")){
						Config.myspot.refreshData();
						Config.core.view2.performClick();
					}
					//Toast.makeText(context,data, Toast.LENGTH_LONG).show();
				}
				
				
			break;
			
			//get client id
			case PushConsts.GET_CLIENTID:
				final String cid = bundle.getString("clientid");
				
				//Toast.makeText(context, cid, Toast.LENGTH_LONG).show();
				if(Helper.getSetting(context,"clientid").equals("")){
					new HttpRequest(new HttpCallback(){

						@Override
						public void run(String res) {
							// TODO Auto-generated method stub
							try {
								JSONObject r = new JSONObject(res);
								if("success".equals(r.getString("code"))){
									//activity.showSuccessMessage("推送配置成功!");
									Helper.saveSetting(context,"clientid",cid);
								}
								else{
									//activity.showErrorMessage("推送配置失败!");
								}
								//Toast.makeText(context, r.getString("msg"), Toast.LENGTH_LONG).show();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
								Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
								//activity.showErrorMessage("推送配置失败"+"("+e.getMessage()+")");
							}
						}
						
					}).execute("/user_update_clientid/"+Helper.getSetting(context,"userid"),"POST","clientid="+cid);
					
				}
			break;
			
			case PushConsts.THIRDPART_FEEDBACK:
				Toast.makeText(context,"message2", Toast.LENGTH_LONG).show();
			break;
		}
	}

}
