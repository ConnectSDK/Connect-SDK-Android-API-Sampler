package com.example.connect_sdk_sampler.widget;

import com.connectsdk.core.AppInfo;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AppAdapter extends ArrayAdapter<AppInfo> {
    Context context; 
    int resourceId; 
    String runningAppId;

	public AppAdapter(Context context, int resource) {
		super(context, resource);
		this.context = context;
		this.resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        
		if (convertView == null) {
			view = View.inflate(getContext(), resourceId, null);
		}
		
		AppInfo app = this.getItem(position);

		TextView textView = (TextView) view.findViewById(android.R.id.text1);
		textView.setText(app.getName());
		if ( runningAppId != null && runningAppId.equals(app.getId()) ) {
			System.out.println("runningAppId: " + runningAppId + ", id: " + app.getId());
			textView.setTextColor(Color.RED);
		}
		else 
			textView.setTextColor(Color.BLACK);
		
		return view;
	}
	
	public void setRunningAppId(String appId) {
		runningAppId = appId;
	}
}
