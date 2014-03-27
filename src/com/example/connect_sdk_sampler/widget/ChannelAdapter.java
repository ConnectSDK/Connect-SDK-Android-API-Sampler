package com.example.connect_sdk_sampler.widget;

import java.util.List;

import com.connectsdk.core.AppInfo;
import com.connectsdk.core.ChannelInfo;
import com.example.connect_sdk_sampler.R;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChannelAdapter extends ArrayAdapter<ChannelInfo> {
	private int resourceId;

	public ChannelAdapter(Context context, int resource) {
		super(context, resource);
		resourceId = resource;
	}

	public ChannelAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
		resourceId = resource;
	}

	public ChannelAdapter(Context context, int resource, ChannelInfo[] objects) {
		super(context, resource, objects);
		resourceId = resource;
	}

	public ChannelAdapter(Context context, int resource, List<ChannelInfo> objects) {
		super(context, resource, objects);
		resourceId = resource;
	}

	public ChannelAdapter(Context context, int resource,
			int textViewResourceId, ChannelInfo[] objects) {
		super(context, resource, textViewResourceId, objects);
		resourceId = resource;
	}

	public ChannelAdapter(Context context, int resource,
			int textViewResourceId, List<ChannelInfo> objects) {
		super(context, resource, textViewResourceId, objects);
		resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        
		if (convertView == null) {
			view = View.inflate(getContext(), resourceId, null);
		}
		
		ChannelInfo app = this.getItem(position);

		TextView textView = (TextView) view.findViewById(R.id.itemTitle);
		TextView subTextView = (TextView) view.findViewById(R.id.itemSubTitle);
		TextView channelNumber = (TextView) view.findViewById(R.id.itemNumber);
		textView.setText(app.getName());
		subTextView.setText(app.getId());
		channelNumber.setText(app.getNumber());
		
		return view;
	}
}
