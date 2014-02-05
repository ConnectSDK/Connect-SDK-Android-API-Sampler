package com.example.connect_sdk_sampler.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.connectsdk.core.ChannelInfo;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.service.capability.TVControl;
import com.connectsdk.service.capability.ToastControl;
import com.connectsdk.service.capability.VolumeControl;
import com.connectsdk.service.capability.listeners.ChannelListListener;
import com.connectsdk.service.capability.listeners.ChannelListener;
import com.connectsdk.service.capability.listeners.VolumeStatusListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.ServiceSubscription;
import com.example.connect_sdk_sampler.R;

public class TVFragment extends BaseFragment {
    public ToggleButton muteToggleButton;
    public Button volumeUpButton;
    public Button volumeDownButton;
    public Button channelUpButton;
    public Button channelDownButton;
    public Button powerOffButton;
    public Button incomingCallButton;
    public Button mode3DButton;
    public SeekBar volumeSlider;

    public ListView channelListView;
    public ArrayAdapter<String> adapter;
    List<ChannelInfo> channelList;
    
    public TextView channelNumberTextView;
    public TextView channelNameTextView;
    
    boolean muteToggle;
    boolean mode3DToggle;
    boolean incomingCallToggle;
    
    ServiceSubscription currentChannelSubscription;

    private ServiceSubscription mVolumeStatusSubscription;

    public TVFragment(ConnectableDevice tv, Context context)
    {
        super(tv, context);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_tv, container, false);

        muteToggleButton = (ToggleButton) rootView.findViewById(R.id.muteToggle);
        volumeUpButton = (Button) rootView.findViewById(R.id.volumeUpButton);
        volumeDownButton = (Button) rootView.findViewById(R.id.volumeDownButton);
        channelUpButton = (Button) rootView.findViewById(R.id.channelUpButton);
        channelDownButton = (Button) rootView.findViewById(R.id.channelDownButton);
        powerOffButton = (Button) rootView.findViewById(R.id.powerOffButton);
        incomingCallButton = (Button) rootView.findViewById(R.id.incomingCallButton);
        mode3DButton = (Button) rootView.findViewById(R.id.mode3DButton);

        channelListView = (ListView) rootView.findViewById(R.id.channelListView);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        channelListView.setAdapter(adapter);
        
        volumeSlider = (SeekBar) rootView.findViewById(R.id.volumeSlider);
        volumeSlider.setMax(100);
        
        channelNumberTextView = (TextView) rootView.findViewById(R.id.textChannelNumber);
        channelNameTextView = (TextView) rootView.findViewById(R.id.textChannelName);

        buttons = new Button[8];
        buttons[0] = muteToggleButton;
        buttons[1] = volumeUpButton;
        buttons[2] = volumeDownButton;
        buttons[3] = channelUpButton;
        buttons[4] = channelDownButton;
        buttons[5] = powerOffButton;
        buttons[6] = incomingCallButton;
        buttons[7] = mode3DButton;
        
        muteToggle = false;
        mode3DToggle = false;
        incomingCallToggle = false;
        
        channelListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String channelNumber = (String) arg0.getItemAtPosition(arg2);
				getTVControl().setChannelByNumber(channelNumber, null);
			}
		});
        
		return rootView;
	}

    @Override
    public void enableButtons()
    {
        muteToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	muteToggle = !muteToggle;
            	getVolumeControl().setMute(muteToggle, null);
            }
        });

        volumeUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	getVolumeControl().volumeUp(null);
            }
        });

        volumeDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	getVolumeControl().volumeDown(null);
            }
        });

        channelUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	getTVControl().channelUp(null);
            }
        });

        channelDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	getTVControl().channelDown(null);
            }
        });

        powerOffButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getPowerControl().powerOff(null);
			}
		});
        
        incomingCallButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				incomingCallToggle = !incomingCallToggle;
				
				if ( incomingCallToggle == true ) {
					getMediaControl().pause(null);
					getVolumeControl().setMute(true, null);
					
					if ( getTv().hasCapability(ToastControl.kToastControlShowToast) ) {
						getToastControl().showToast("Incoming Call, Paused", null);
					}
				}
				else {
					getMediaControl().play(null);
					getVolumeControl().setMute(false, null);

					if ( getTv().hasCapability(ToastControl.kToastControlShowToast) ) {
						getToastControl().showToast("Resume Play", null);
					}
				}
			}
		});
        
        mode3DButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        if ( getTv().hasCapability(TVControl.kTVControl3DSet) ) {
		        	if ( getTVControl() != null ) {
		        		mode3DToggle = !mode3DToggle;
		        		getTVControl().set3DEnabled(mode3DToggle, null);
		        	}
		        }
			}
		});
        
        if ( getTv().hasCapability(VolumeControl.kVolumeControlVolumeSet) ) {
            volumeSlider.setEnabled(true);

            volumeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                	if (fromUser) {
                		float fVol = (float)(progress / 100.0);
                    	getVolumeControl().setVolume(fVol, null);
                	}
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
        else {
        	volumeSlider.setEnabled(false);
        }
        
        if ( getTv().hasCapability(TVControl.kTVControlChannelSubscribe) ) {
        	currentChannelSubscription = getTVControl().subscribeCurrentChannel(new ChannelListener() {
    			
    			@Override
    			public void onGetChannelSuccess(final ChannelInfo ch) {
    				runOnUiThread(new Runnable() {
    					
    					@Override
    					public void run() {
    						String channelNumber = ch.getNumber();
    						String channelName = ch.getName();
    						
    						channelNumberTextView.setText(channelNumber);
    							
    						if ( channelName == null || channelName.length() == 0 ) 
    							channelNameTextView.setText("No Database");
    						else
    							channelNameTextView.setText(channelName);
    					}
    				});
    			}
    			
    			@Override
    			public void onGetChannelFailed(ServiceCommandError error) {
    				
    			}
    		});
        }
        
        if ( getTv().hasCapability(VolumeControl.kVolumeControlVolumeSubscribe) ) {
        	mVolumeStatusSubscription = getVolumeControl().subscribeVolumeStatus(new VolumeStatusListener() {
				
				@Override
				public void onGetVolumeStatusSuccess(final boolean isMute, final float volume) {
	            	runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							int intVol = (int) (volume * 100);
			                muteToggleButton.setChecked(isMute);
			                volumeSlider.setProgress(intVol);
						}
					});		
				}
				
				@Override
				public void onGetVolumeStatusFailed(ServiceCommandError error) {
	                Log.d("ConnectSDK", "Error subscribing to volume status: " + error.getDesc());
				}
			});
        }
        
        getTVControl().getChannelList(new ChannelListListener() {
			
			@Override
			public void onGetChannelListSuccess(ArrayList<ChannelInfo> arg0) {
				channelList = new ArrayList<ChannelInfo>(arg0);
				for (int i = 0; i < channelList.size(); i++) {
					ChannelInfo channel = channelList.get(i);
					final String channelNumber = channel.getNumber();

					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							adapter.add(channelNumber);
						}
					});
				}				
			}
			
			@Override
			public void onGetChannelListFailed(ServiceCommandError arg0) {
				
			}
		});
        
        super.enableButtons();
    }

    @Override
    public void disableButtons()
    {
    	adapter.clear();
        volumeSlider.setEnabled(false);
        volumeSlider.setOnSeekBarChangeListener(null);

        if (mVolumeStatusSubscription != null)
        {
            mVolumeStatusSubscription.unsubscribe();
            mVolumeStatusSubscription = null;
        }

        super.disableButtons();
    }
}
