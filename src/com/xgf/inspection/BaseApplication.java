package com.xgf.inspection;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.provider.SyncStateContract.Constants;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class BaseApplication extends Application {

	private static Context context;

	private static RequestQueue sQueue;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		sQueue = Volley.newRequestQueue(getApplicationContext());
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static RequestQueue getInstanceRequestQueue() {
		if (null == sQueue) {
			sQueue = Volley.newRequestQueue(getContext());
		}
		return sQueue;
	}

	public static Context getContext() {
		return context;
	}

}
