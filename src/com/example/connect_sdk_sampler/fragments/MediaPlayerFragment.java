package com.example.connect_sdk_sampler.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.connectsdk.core.LaunchSession;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.SystemControl;
import com.connectsdk.service.capability.listeners.LaunchListener;
import com.connectsdk.service.capability.listeners.ServiceInfoListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.example.connect_sdk_sampler.R;

public class MediaPlayerFragment extends BaseFragment {
	public Button photoButton;
	public Button videoButton;
    public Button playButton;
    public Button pauseButton;
    public Button stopButton;
    public Button rewindButton;
    public Button fastForwardButton;
    
    LaunchSession photoLaunchSession;
    LaunchSession videoLaunchSession;

    public ListView capabilityListView;
    public ArrayAdapter<String> adapter;
    
    public MediaPlayerFragment(ConnectableDevice tv, Context context)
    {
        super(tv, context);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_media_player, container, false);

		photoButton = (Button) rootView.findViewById(R.id.photoButton);
		videoButton = (Button) rootView.findViewById(R.id.videoButton);
        playButton = (Button) rootView.findViewById(R.id.playButton);
        pauseButton = (Button) rootView.findViewById(R.id.pauseButton);
        stopButton = (Button) rootView.findViewById(R.id.stopButton);
        rewindButton = (Button) rootView.findViewById(R.id.rewindButton);
        fastForwardButton = (Button) rootView.findViewById(R.id.fastForwardButton);
        capabilityListView = (ListView) rootView.findViewById(R.id.capabilitiesListView);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        capabilityListView.setAdapter(adapter);
        
        buttons = new Button[7];
        buttons[0] = photoButton;
        buttons[1] = videoButton;
        buttons[2] = playButton;
        buttons[3] = pauseButton;
        buttons[4] = stopButton;
        buttons[5] = rewindButton;
        buttons[6] = fastForwardButton;

        return rootView;
	}

    @Override
    public void enableButtons()
    {
    	 photoButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
             	if ( photoButton.isSelected() ) {
             		photoButton.setSelected(false);
             		if (photoLaunchSession != null) {
             			photoLaunchSession.close(null);
             			photoLaunchSession = null;
             		}
             	}
             	else {
             		String imagePath = "http://www.freesoftwaremagazine.com/files/nodes/3466/fig_sintel_style_study.jpg";
             		String mimeType = "image/jpeg";
             		String title = "Sintel";
             		String description = "Character Design";
             		String icon = "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/s48x48/50354_121158371242322_7687_q.jpg";
             		
             		getMediaPlayer().displayImage(imagePath, mimeType, title, description, icon, new LaunchListener() {
						
						@Override
						public void onLaunchSuccess(LaunchSession session) {
							photoLaunchSession = session;
							runOnUiThread(new Runnable() {
								public void run() {
				             		photoButton.setSelected(true);
								}
							});
						}
						
						@Override
						public void onLaunchFailed(ServiceCommandError error) {
						}
					});
             	}
             }
    	 });
    	 
    	 videoButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
             	if ( videoButton.isSelected() ) {
             		videoButton.setSelected(false);
             		if (videoLaunchSession != null) {
             			videoLaunchSession.close(null);
             			videoLaunchSession = null;
             		}
             	}
             	else {
             		String videoPath = "http://mirrorblender.top-ix.org/movies/sintel-1280-surround.mp4";
             		String mimeType = "video/mp4";
             		String title = "Sintel";
             		String description = "videoURL";
             		String icon = null;
             		
             		getMediaPlayer().displayVideo(videoPath, mimeType, title, description, icon, false, new LaunchListener() {
						
						@Override
						public void onLaunchSuccess(LaunchSession session) {
							videoLaunchSession = session;
							runOnUiThread(new Runnable() {
								public void run() {
				             		videoButton.setSelected(true);
								}
							});
						}
						
						@Override
						public void onLaunchFailed(ServiceCommandError error) {
						}
					});
             	}
             }
         });
    	
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	getMediaControl().play(null);
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	getMediaControl().pause(null);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	getMediaControl().stop(null);
            }
        });

        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	getMediaControl().rewind(null);
            }
        });

        fastForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	getMediaControl().fastForward(null);
            }
        });

        if ( getTv().hasCapability(SystemControl.kSystemControlServiceInfo) ) {
        	if ( getSystemControl() != null ) {
        		getSystemControl().getServiceInfo(new ServiceInfoListener() {
					
					@Override
					public void onGetServiceInfoSuccess(JSONArray services) {
						for (int i = 0; i < services.length(); i++) {
							JSONObject object;
							try {
								object = (JSONObject) services.get(i);
								final String name = (String) object.get("name");
								runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										adapter.add(name);
									}
								});
		
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
					
					@Override
					public void onGetServiceInfoFailed(ServiceCommandError error) {
					}
				});
        	}
        }
        
        super.enableButtons();

        if ( getMediaPlayer() != null) {
    		photoButton.setSelected(false);
    		videoButton.setSelected(false);
        }
        
        if ( !getTv().hasCapability(MediaControl.kMediaControlPlay) ) {
        	playButton.setEnabled(false);
        }
        
        if ( !getTv().hasCapability(MediaControl.kMediaControlPause) ) {
        	pauseButton.setEnabled(false);
        }

        if ( !getTv().hasCapability(MediaControl.kMediaControlStop) ) {
        	stopButton.setEnabled(false);
        }

        if ( !getTv().hasCapability(MediaControl.kMediaControlRewind) ) {
        	rewindButton.setEnabled(false);
        }
        	
        if ( !getTv().hasCapability(MediaControl.kMediaControlFastForward) ) {
        	fastForwardButton.setEnabled(false);
        }
    }

	@Override
	public void disableButtons() {
		adapter.clear();
		super.disableButtons();
	}
}