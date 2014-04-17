package com.example.connect_sdk_sampler;

import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Application;

import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.DIALService;
import com.crittercism.app.Crittercism;

public class MainApplication extends Application {
	@Override
	public void onCreate() {
//		Crittercism.initialize(getApplicationContext(), "53305ec78633a426d7000005");

		DIALService.registerApp("Levak");
		DiscoveryManager.init(getApplicationContext());

		super.onCreate();
	}
}
