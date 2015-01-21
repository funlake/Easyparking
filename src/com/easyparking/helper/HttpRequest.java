package com.easyparking.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class HttpRequest extends AsyncTask<String, Integer, String> {

	private HttpCallback callback;

	public HttpRequest(HttpCallback cb) {
		callback = cb;
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String url = Config.Server_URL + params[0];
		String method = params[1];
		String posts  = params[2];
		String res = "";
		//get
		
		if (method.equals("GET") || method.equals("")) {
			HttpGet g = new HttpGet(url);
			try {
				HttpResponse r = new DefaultHttpClient().execute(g);
				if (r.getStatusLine().getStatusCode() == 200) {
					res = EntityUtils.toString(r.getEntity());
				} else {
					res = "bad1";
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				res = "bad2";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				res = "bad3";
			}
			return res;
		}//get 
		else if (method.equals("POST")) {
			
			HttpPost p = new HttpPost(url);
			try {
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				if(!posts.equals("")){
					//Log.i("http","POST:"+posts);
					String ps[] = posts.split("&");
					String kv[];
					String key;
					String val;
					for(String pss:ps){
						kv = pss.split("=");
						key = kv[0];
						if(kv.length == 1){
							val = "";
						}
						else{
							val = kv[1];
						}
						list.add(new BasicNameValuePair(key, val));
					}
				}
//				NameValuePair pair1 = new BasicNameValuePair("user", "lake");
//				NameValuePair pair2 = new BasicNameValuePair("pass", "123456");
//				
//				list.add(pair1);
//				list.add(pair2);
				p.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
				HttpResponse r = new DefaultHttpClient().execute(p);
				if (r.getStatusLine().getStatusCode() == 200) {
					res = EntityUtils.toString(r.getEntity());
				} else {
					res = "请求错误";
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				res = "http协议有误";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				res = "网络错误";
			}
			return res;
		}//post
		return "";
	}

	protected void onPostExecute(String R) {
		callback.run(R);
	}
}
