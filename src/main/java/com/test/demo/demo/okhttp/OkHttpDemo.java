package com.test.demo.demo.okhttp;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpDemo {
	
	public static final Log log = LogFactory.getLog(OkHttpDemo.class);
	
	public static void main(String[] args) {
		asyncGetRequest();
	}
	
	public static void asyncGetRequest() {
		String url = "http://wwww.baidu.com";
		OkHttpClient okHttpClient = new OkHttpClient();
		final Request request = new Request.Builder()
		        .url(url)
		        .get()//默认就是GET请求，可以不写
		        .build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
		    @Override
		    public void onFailure(Call call, IOException e) {
		    	log.error("onFailure: ");
		    }

		    @Override
		    public void onResponse(Call call, Response response) throws IOException {
		    	log.info("onResponse: " + response.body().string());
		    }
		});
	}
}
