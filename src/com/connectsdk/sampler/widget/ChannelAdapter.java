//
//  Connect SDK Sample App by LG Electronics
//
//  To the extent possible under law, the person who associated CC0 with
//  this sample app has waived all copyright and related or neighboring rights
//  to the sample app.
//
//  You should have received a copy of the CC0 legalcode along with this
//  work. If not, see http://creativecommons.org/publicdomain/zero/1.0/.
//

package com.connectsdk.sampler.widget;

import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.connectsdk.core.ChannelInfo;
import com.connectsdk.sampler.R;

public class ChannelAdapter extends ArrayAdapter<ChannelInfo> {
    private int resourceId;
    private ChannelInfo currentChannel;

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

        boolean isChannel = app.equals(currentChannel);
        int textColor = isChannel ? 0xFFFFFFFF : 0xFF000000;
        view.setBackgroundColor(isChannel ? 0xFF550000 : 0);
        textView.setTextColor(textColor);
        subTextView.setTextColor(textColor);
        channelNumber.setTextColor(textColor);

        return view;
    }

    public void setCurrentChannel(ChannelInfo currentChannel) {
        this.currentChannel = currentChannel;
    }

    public int getCurrentPosition() {
        if (currentChannel == null)
            return -1;


        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).equals(currentChannel))
                return i;
        }

        return -1;
    }

    public void sort() {
        this.sort(new Comparator<ChannelInfo>() {
            @Override
            public int compare(ChannelInfo lhs, ChannelInfo rhs) {
                int sortMajor = lhs.getMajorNumber() - rhs.getMajorNumber();
                if (sortMajor == 0)
                    return lhs.getMinorNumber() - rhs.getMinorNumber();
                else 
                    return sortMajor;
            }
        });
    }
}
