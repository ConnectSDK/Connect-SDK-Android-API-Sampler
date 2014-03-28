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
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.PowerControl;
import com.connectsdk.service.capability.TVControl;
import com.connectsdk.service.capability.TVControl.ChannelListListener;
import com.connectsdk.service.capability.TVControl.ChannelListener;
import com.connectsdk.service.capability.ToastControl;
import com.connectsdk.service.capability.VolumeControl;
import com.connectsdk.service.capability.VolumeControl.MuteListener;
import com.connectsdk.service.capability.VolumeControl.VolumeListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.ServiceSubscription;
import com.example.connect_sdk_sampler.R;
import com.example.connect_sdk_sampler.widget.ChannelAdapter;

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
    public ArrayAdapter<ChannelInfo> adapter;
    List<ChannelInfo> channelList;
    
    public TextView channelNumberTextView;
    public TextView channelNameTextView;
    
    boolean muteToggle;
    boolean mode3DToggle;
    boolean incomingCallToggle;
    
    private ServiceSubscription<ChannelListener> mCurrentChannelSubscription;

    private ServiceSubscription<VolumeListener> mVolumeSubscription;
    private ServiceSubscription<MuteListener> mMuteSubscription;

    public TVFragment(Context context)
    {
        super(context);
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
        adapter = new ChannelAdapter(getContext(), R.layout.channel_item);

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

    	if ( getTv().hasCapability(VolumeControl.Mute_Set) ) {
	        muteToggleButton.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	            	muteToggle = !muteToggle;
	            	getVolumeControl().setMute(muteToggle, null);
	            }
	        });
    	}
    	else {
    		disableButton(muteToggleButton);
    	}

    	if ( getTv().hasCapability(VolumeControl.Volume_Up_Down) ) {
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
    	}
    	else {
    		disableButton(volumeDownButton);
    		disableButton(volumeUpButton);
    	}

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
        
    	if ( getTv().hasCapability(VolumeControl.Mute_Set) ) {
            incomingCallButton.setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				incomingCallToggle = !incomingCallToggle;
    				
    				if ( incomingCallToggle == true ) {
    					getVolumeControl().setMute(true, null);

    					if ( getTv().hasCapability(MediaControl.Pause) ) {
    			    		getMediaControl().pause(null);
    			    	}
    					
    					if ( getTv().hasCapability(ToastControl.Show_Toast) ) {
    						getToastControl().showToast("Incoming Call, Paused", null);
    					}
    				}
    				else {
    					getVolumeControl().setMute(false, null);

    					if ( getTv().hasCapability(MediaControl.Play) ) {
    			    		getMediaControl().play(null);
    			    	}

    					if ( getTv().hasCapability(ToastControl.Show_Toast) ) {
    						getToastControl().showToast("Resume Play", null);
    					}
    				}
    			}
    		});
    	}
    	else {
    		disableButton(incomingCallButton);
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
        
        if ( getTv().hasCapability(VolumeControl.Volume_Set) ) {
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
        
        if ( getTv().hasCapability(TVControl.Channel_Subscribe) ) {
        	mCurrentChannelSubscription = getTVControl().subscribeCurrentChannel(new ChannelListener() {
    			
    			@Override
    			public void onSuccess(final ChannelInfo ch) {
    				String channelNumber = ch.getNumber();
    				String channelName = ch.getName();
    				
    				channelNumberTextView.setText(channelNumber);
    					
    				if ( channelName == null || channelName.length() == 0 ) 
    					channelNameTextView.setText("No Database");
    				else
    					channelNameTextView.setText(channelName);
    			}
    			
    			@Override
    			public void onError(ServiceCommandError error) {
    				
    			}
    		});
        }
        
        if ( getTv().hasCapability(VolumeControl.Volume_Subscribe) ) {
        	mVolumeSubscription = getVolumeControl().subscribeVolume(new VolumeListener() {
				
        		public void onSuccess(Float volume) {
			        volumeSlider.setProgress((int) (volume * 100));
				}
				
				@Override
				public void onError(ServiceCommandError error) {
	                Log.d("LG", "Error subscribing to volume: " + error);
				}
			});
        }
        
        if (getTv().hasCapability(VolumeControl.Mute_Subscribe)) {
        	mMuteSubscription = getVolumeControl().subscribeMute(new MuteListener() {
				
				@Override
				public void onSuccess(Boolean object) {
			        muteToggleButton.setChecked(object);
				}
				
				@Override
				public void onError(ServiceCommandError error) {
					Log.d("LG", "Error subscribing to mute: " + error);
				}
			});
        }
        
        if ( getTv().hasCapability(TVControl.Channel_List) ) {
            getTVControl().getChannelList(new ChannelListListener() {
    			
    			@Override
    			public void onSuccess(ArrayList<ChannelInfo> arg0) {
    				channelList = new ArrayList<ChannelInfo>(arg0);
    				for (int i = 0; i < channelList.size(); i++) {
    					ChannelInfo channel = channelList.get(i);

    					adapter.add(channel);
    				}
    			}
    			
    			@Override
    			public void onError(ServiceCommandError arg0) {
    				
    			}
    		});
        }
    }

    @Override
    public void disableButtons()
    {
    	adapter.clear();
        volumeSlider.setEnabled(false);
        volumeSlider.setOnSeekBarChangeListener(null);
        
        if (mCurrentChannelSubscription != null)
        {
        	mCurrentChannelSubscription.unsubscribe();
        	mCurrentChannelSubscription = null;
        }
        
        if (mVolumeSubscription != null)
        {
            mVolumeSubscription.unsubscribe();
            mVolumeSubscription = null;
        }
        
        if (mMuteSubscription != null) {
        	mMuteSubscription.unsubscribe();
        	mMuteSubscription = null;
        }

        super.disableButtons();
    }
}
