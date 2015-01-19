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

package com.connectsdk.sampler.fragments;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.connectsdk.core.ChannelInfo;
import com.connectsdk.sampler.R;
import com.connectsdk.sampler.widget.ChannelAdapter;
import com.connectsdk.service.capability.PowerControl;
import com.connectsdk.service.capability.TVControl;
import com.connectsdk.service.capability.TVControl.ChannelListListener;
import com.connectsdk.service.capability.TVControl.ChannelListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.ServiceSubscription;

public class TVFragment extends BaseFragment {
    public Button channelUpButton;
    public Button channelDownButton;
    public Button powerOffButton;
    public Button mode3DButton;

    public ListView channelListView;
    public ChannelAdapter adapter;
    
    boolean mode3DToggle;
    boolean incomingCallToggle;
    
    private ServiceSubscription<ChannelListener> mCurrentChannelSubscription;

    public TVFragment() {};
    
    public TVFragment(Context context)
    {
        super(context);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_tv, container, false);
		
        channelUpButton = (Button) rootView.findViewById(R.id.channelUpButton);
        channelDownButton = (Button) rootView.findViewById(R.id.channelDownButton);
        powerOffButton = (Button) rootView.findViewById(R.id.powerOffButton);
        mode3DButton = (Button) rootView.findViewById(R.id.mode3DButton);

        channelListView = (ListView) rootView.findViewById(R.id.channelListView);
        adapter = new ChannelAdapter(getContext(), R.layout.channel_item);

        channelListView.setAdapter(adapter);

        buttons = new Button[] {
                channelUpButton, 
                channelDownButton, 
                powerOffButton, 
                mode3DButton
        };
        
        mode3DToggle = false;
        incomingCallToggle = false;
        
        channelListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ChannelInfo channelInfo = (ChannelInfo) arg0.getItemAtPosition(arg2);
				getTVControl().setChannel(channelInfo, null);
			}
		});
        
		return rootView;
	}

    @Override
    public void enableButtons()
    {
        super.enableButtons();

    	if ( getTv().hasCapability(TVControl.Channel_Up) ) {
	        channelUpButton.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	            	getTVControl().channelUp(null);
	            }
	        });
    	}
    	else {
    		disableButton(channelUpButton);
    	}

    	if ( getTv().hasCapability(TVControl.Channel_Down) ) {
	        channelDownButton.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	            	getTVControl().channelDown(null);
	            }
	        });
    	}
    	else {
    		disableButton(channelDownButton);
    	}

    	if ( getTv().hasCapability(PowerControl.Off) ) {
	        powerOffButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					getPowerControl().powerOff(null);
				}
			});
    	}
    	else {
    		disableButton(powerOffButton);
    	}
        
		if ( getTv().hasCapability(TVControl.Set_3D) ) {
	        mode3DButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
			        if ( getTv().hasCapability(TVControl.Set_3D) ) {
			        	if ( getTVControl() != null ) {
			        		mode3DToggle = !mode3DToggle;
			        		getTVControl().set3DEnabled(mode3DToggle, null);
			        	}
			        }
				}
			});
		}
		else {
			disableButton(mode3DButton);
		}
		
		if ( getTv().hasCapability(TVControl.Channel_List) ) {
            getTVControl().getChannelList(new ChannelListListener() {
    			
    			@Override
    			public void onSuccess(List<ChannelInfo> channelList) {
    				adapter.clear();
    				for (ChannelInfo channel : channelList)
    					adapter.add(channel);
    				adapter.sort();
    			}
    			
    			@Override
    			public void onError(ServiceCommandError arg0) {
    				
    			}
    		});
        }
        
        if ( getTv().hasCapability(TVControl.Channel_Subscribe) ) {
        	mCurrentChannelSubscription = getTVControl().subscribeCurrentChannel(new ChannelListener() {
    			
    			@Override
    			public void onSuccess(final ChannelInfo ch) {
    				adapter.setCurrentChannel(ch);
    				adapter.notifyDataSetChanged();
    				
    				int position = adapter.getPosition(ch);
    				channelListView.setSelection(position);
    			}
    			
    			@Override
    			public void onError(ServiceCommandError error) {
    				
    			}
    		});
        }
    }

    @Override
    public void disableButtons()
    {
    	adapter.clear();
        
        if (mCurrentChannelSubscription != null)
        {
        	mCurrentChannelSubscription.unsubscribe();
        	mCurrentChannelSubscription = null;
        }

        super.disableButtons();
    }
}
