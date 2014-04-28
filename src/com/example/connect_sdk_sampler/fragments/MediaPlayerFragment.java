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

package com.example.connect_sdk_sampler.fragments;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.MediaControl.DurationListener;
import com.connectsdk.service.capability.MediaControl.PlayStateListener;
import com.connectsdk.service.capability.MediaControl.PlayStateStatus;
import com.connectsdk.service.capability.MediaControl.PositionListener;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.capability.MediaPlayer.MediaLaunchObject;
import com.connectsdk.service.capability.VolumeControl;
import com.connectsdk.service.capability.VolumeControl.VolumeListener;
import com.connectsdk.service.capability.listeners.ResponseListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.sessions.LaunchSession;
import com.example.connect_sdk_sampler.R;

public class MediaPlayerFragment extends BaseFragment {
	public Button photoButton;
	public Button videoButton;
	public Button audioButton;
    public Button playButton;
    public Button pauseButton;
    public Button stopButton;
    public Button rewindButton;
    public Button fastForwardButton;
    public Button closeButton;
    
    public LaunchSession photoLaunchSession;
    public LaunchSession videoLaunchSession;
    public LaunchSession audioLaunchSession;
    
    public TextView positionTextView;
    public TextView durationTextView;
    public SeekBar mSeekBar;
    public boolean mIsUserSeeking;
    public SeekBar mVolumeBar;

    public boolean mSeeking;
    public Runnable mRefreshRunnable;
    public static final int REFRESH_INTERVAL_MS = (int) TimeUnit.SECONDS.toMillis(1);
    public Handler mHandler;
    public long totalTimeDuration;
    public boolean mIsGettingPlayPosition;
    
    private MediaControl mMediaControl = null;
    
    private Timer refreshTimer;
    
    public MediaPlayerFragment(Context context)
    {
        super(context);
        
        mIsUserSeeking = false;
        mSeeking = false;
        mIsGettingPlayPosition = false;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_media_player, container, false);

		photoButton = (Button) rootView.findViewById(R.id.photoButton);
		videoButton = (Button) rootView.findViewById(R.id.videoButton);
		audioButton = (Button) rootView.findViewById(R.id.audioButton);
        playButton = (Button) rootView.findViewById(R.id.playButton);
        pauseButton = (Button) rootView.findViewById(R.id.pauseButton);
        stopButton = (Button) rootView.findViewById(R.id.stopButton);
        rewindButton = (Button) rootView.findViewById(R.id.rewindButton);
        fastForwardButton = (Button) rootView.findViewById(R.id.fastForwardButton);
        closeButton = (Button) rootView.findViewById(R.id.closeButton);
        
        positionTextView = (TextView) rootView.findViewById(R.id.stream_position);
        durationTextView = (TextView) rootView.findViewById(R.id.stream_duration);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.stream_seek_bar);
        mVolumeBar = (SeekBar) rootView.findViewById(R.id.volume_seek_bar);
        
        buttons = new Button[] {
        	photoButton, 
        	videoButton, 
        	audioButton, 
        	playButton, 
        	pauseButton, 
        	stopButton, 
        	rewindButton, 
        	fastForwardButton, 
        	closeButton
        };

        mHandler = new Handler();

        return rootView;
	}
    
    @Override
	public void setTv(ConnectableDevice tv) {
		super.setTv(tv);
		
		if (tv == null) {
			stopUpdating();
			mMediaControl = null;
		}
	}
    
    @Override
    public void onPause() {
    	stopUpdating();
    	super.onPause();
    }

	@Override
    public void enableButtons()
    {
    	if ( getTv().hasCapability(MediaPlayer.Display_Image) ) {
    		photoButton.setEnabled(true);
    		photoButton.setOnClickListener(new View.OnClickListener() {

    			@Override
    			public void onClick(View view) {
					if (photoLaunchSession != null) {
						photoLaunchSession.close(null);
             			photoLaunchSession = null;
             			closeButton.setEnabled(false);
             			closeButton.setOnClickListener(null);
             			stopUpdating();
             			return;
             		}
					
					disableMedia();
					
	         		String imagePath = "http://ec2-54-201-108-205.us-west-2.compute.amazonaws.com/samples/media/photo.jpg";
	         		String mimeType = "image/jpeg";
	         		String title = "Sintel Character Design";
	         		String description = "Blender Open Movie Project";
	         		String icon = "http://ec2-54-201-108-205.us-west-2.compute.amazonaws.com/samples/media/photoIcon.jpg";
	         		
	         		getMediaPlayer().displayImage(imagePath, mimeType, title, description, icon, new MediaPlayer.LaunchListener() {
						
	         			@Override
	         			public void onSuccess(MediaLaunchObject object) {
	         				photoLaunchSession = object.launchSession;
	         				closeButton.setEnabled(true);
	         				closeButton.setOnClickListener(closeListener);
	         				stopUpdating();
						}
						
						@Override
						public void onError(ServiceCommandError error) {
						}
					});
	             }
	    	 });
    	}
    	else {
    		disableButton(photoButton);
    	}
    	 
    	totalTimeDuration = -1;
    	
    	if ( getTv().hasCapability(MediaPlayer.Display_Video) ) {
    		videoButton.setEnabled(true);
    		videoButton.setOnClickListener(new View.OnClickListener() {

    			@Override
    			public void onClick(View view) {
             		if (videoLaunchSession != null) {
             			videoLaunchSession.close(null);
             			videoLaunchSession = null;
             			stopUpdating();
             			disableMedia();
             			return;
             		}

             		String videoPath = "http://ec2-54-201-108-205.us-west-2.compute.amazonaws.com/samples/media/video.mp4";
             		String mimeType = "video/mp4";
             		String title = "Sintel Trailer";
             		String description = "Blender Open Movie Project";
             		String icon = "http://ec2-54-201-108-205.us-west-2.compute.amazonaws.com/samples/media/videoIcon.jpg";
             		
             		getMediaPlayer().playMedia(videoPath, mimeType, title, description, icon, false, new MediaPlayer.LaunchListener() {
						
             			public void onSuccess(MediaLaunchObject object) {
							videoLaunchSession = object.launchSession;
							mMediaControl = object.mediaControl;
							stopUpdating();
							enableMedia();
						}
						
						@Override
						public void onError(ServiceCommandError error) {
						}
             		});
    			}
    		});
    	}
    	else {
    		disableButton(videoButton);
    	}
    	
    	if (getTv().hasCapability(MediaPlayer.Display_Audio)) {
    		audioButton.setEnabled(true);
    		audioButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					if (audioLaunchSession != null) {
						audioLaunchSession.close(null);
						audioLaunchSession = null;
						stopUpdating();
						disableMedia();
						return;
					}
					
					String mediaURL = "http://ec2-54-201-108-205.us-west-2.compute.amazonaws.com/samples/media/audio.mp3";
					String iconURL = "http://ec2-54-201-108-205.us-west-2.compute.amazonaws.com/samples/media/audioIcon.jpg";
					String title = "The Song that Doesn't End";
					String description = "Lamb Chop's Play Along";
					String mimeType = "audio/mp3";
					boolean shouldLoop = false;
					
					getMediaPlayer().playMedia(mediaURL, mimeType, title, description, iconURL, shouldLoop, new MediaPlayer.LaunchListener() {
						
						@Override
						public void onError(ServiceCommandError error) {
							Log.d("LG", "Error playing audio");
						}
						
						@Override
						public void onSuccess(MediaLaunchObject object) {
							Log.d("LG", "Started playing audio");
							
							audioLaunchSession = object.launchSession;
							mMediaControl = object.mediaControl;
							
							enableMedia();
						}
					});
				}
			});
    	} else {
    		disableButton(audioButton);
    	}
    	
    	mVolumeBar.setEnabled(getTv().hasCapability(VolumeControl.Volume_Set));
    	mVolumeBar.setOnSeekBarChangeListener(volumeListener);
    	
    	if (getTv().hasCapability(VolumeControl.Volume_Get)) {
    		getVolumeControl().getVolume(getVolumeListener);
    	}
    	
    	if (getTv().hasCapability(VolumeControl.Volume_Subscribe)) {
    		getVolumeControl().subscribeVolume(getVolumeListener);
    	}

    	disableMedia();
    }

	@Override
	public void disableButtons() {
		mSeekBar.setEnabled(false);
		mVolumeBar.setEnabled(false);
		mVolumeBar.setOnSeekBarChangeListener(null);
		positionTextView.setEnabled(false);
		durationTextView.setEnabled(false);
		
		super.disableButtons();
	}
	
	protected void onSeekBarMoved(long position) {
		if (mMediaControl != null && getTv().hasCapability(MediaControl.Seek)) {
			mSeeking = true;
			
    		mMediaControl.seek(position, new ResponseListener<Object>() {
				
				@Override
				public void onSuccess(Object response) {
					Log.d("LG", "Success on Seeking");
					mSeeking = false;
					startUpdating();
				}
				
				@Override
				public void onError(ServiceCommandError error) {
					Log.w("Connect SDK", "Unable to seek: " + error.getCode());
					mSeeking = false;
					startUpdating();
				}
			});
		}
	}
	
	public void enableMedia() {

    	playButton.setEnabled(getTv().hasCapability(MediaControl.Play));
    	pauseButton.setEnabled(getTv().hasCapability(MediaControl.Pause));
    	stopButton.setEnabled(getTv().hasCapability(MediaControl.Stop));
    	rewindButton.setEnabled(getTv().hasCapability(MediaControl.Rewind));
    	fastForwardButton.setEnabled(getTv().hasCapability(MediaControl.FastForward));
       	mSeekBar.setEnabled(getTv().hasCapability(MediaControl.Seek));
       	closeButton.setEnabled(getTv().hasCapability(MediaPlayer.Close));

        fastForwardButton.setOnClickListener(fastForwardListener);
    	mSeekBar.setOnSeekBarChangeListener(seekListener);
        rewindButton.setOnClickListener(rewindListener);
        stopButton.setOnClickListener(stopListener);
        playButton.setOnClickListener(playListener);
        pauseButton.setOnClickListener(pauseListener);
        closeButton.setOnClickListener(closeListener);
        
        if (getTv().hasCapability(MediaControl.PlayState_Subscribe)) {
        	mMediaControl.subscribePlayState(playStateListener);
        } else {
        	startUpdating();
        }
	}
	
	public void disableMedia() {
    	playButton.setEnabled(false);
    	playButton.setOnClickListener(null);
    	pauseButton.setEnabled(false);
    	pauseButton.setOnClickListener(null);
    	stopButton.setEnabled(false);
    	stopButton.setOnClickListener(null);
    	rewindButton.setEnabled(false);
    	rewindButton.setOnClickListener(null);
    	fastForwardButton.setEnabled(false);
    	fastForwardButton.setOnClickListener(null);
       	mSeekBar.setEnabled(false);
       	mSeekBar.setOnSeekBarChangeListener(null);
       	closeButton.setEnabled(false);
       	closeButton.setOnClickListener(null);
       	
       	totalTimeDuration = -1;
	}
	
	public View.OnClickListener playListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        	if (mMediaControl != null)
        		mMediaControl.play(null);
        }
    };
    
    public View.OnClickListener pauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        	if (mMediaControl != null)
        		mMediaControl.pause(null);
        }
    };
    
    public View.OnClickListener closeListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
			if (getMediaPlayer() != null) {
				if (photoLaunchSession != null)
					getMediaPlayer().closeMedia(photoLaunchSession, null);
				if (videoLaunchSession != null)
					getMediaPlayer().closeMedia(videoLaunchSession, null);
				if (audioLaunchSession != null)
					getMediaPlayer().closeMedia(audioLaunchSession, null);
				
				photoLaunchSession = null;
				videoLaunchSession = null;
				audioLaunchSession = null;

				disableMedia();
			}
		}
	};
    
    public View.OnClickListener stopListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        	if (mMediaControl != null)
        		mMediaControl.stop(new ResponseListener<Object>() {
				
				@Override
				public void onSuccess(Object response) {
					stopUpdating();
					
					positionTextView.setText("--:--");
					durationTextView.setText("--:--");
					mSeekBar.setProgress(0);
				}
				
				@Override
				public void onError(ServiceCommandError error) {
				}
			});
        }
    };
    
    public View.OnClickListener rewindListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        	if (mMediaControl != null)
        		mMediaControl.rewind(null);
        }
    };
    
    public View.OnClickListener fastForwardListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        	if (mMediaControl != null)
        		mMediaControl.fastForward(null);
        }
    };
    
    public OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
            mIsUserSeeking = false;
            mSeekBar.setSecondaryProgress(0);
            onSeekBarMoved(seekBar.getProgress());					
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
            mIsUserSeeking = true;
            mSeekBar.setSecondaryProgress(seekBar.getProgress());					
            stopUpdating();
		}
		
		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			
		}
	};
	
	public OnSeekBarChangeListener volumeListener = new OnSeekBarChangeListener() {
		
		@Override public void onStopTrackingTouch(SeekBar arg0) { }
		@Override public void onStartTrackingTouch(SeekBar arg0) { }

		@Override
		public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {
			if (fromUser)
				getVolumeControl().setVolume((float) mVolumeBar.getProgress() / 100.0f, null);
		}
	};
	
	public VolumeListener getVolumeListener = new VolumeListener() {
		
		@Override
		public void onError(ServiceCommandError error) {
			Log.d("LG", "Error getting Volume: " + error);
		}
		
		@Override
		public void onSuccess(Float object) {
			mVolumeBar.setProgress((int) (object * 100.0f));
		}
	};
	
	public PlayStateListener playStateListener = new PlayStateListener() {
		
		@Override
		public void onError(ServiceCommandError error) {
			Log.d("LG", "Playstate Listener error = " + error);
		}
		
		@Override
		public void onSuccess(PlayStateStatus playState) {
			Log.d("LG", "Playstate changed | playState = " + playState);
			
			switch (playState) {
			case Playing:
//				if (!mSeeking)
					startUpdating();

				if (mMediaControl != null && getTv().hasCapability(MediaControl.Duration)) {
					mMediaControl.getDuration(durationListener);
				}
				break;
			case Finished:
				positionTextView.setText("--:--");
				durationTextView.setText("--:--");
				mSeekBar.setProgress(0);
				
			case Paused:
				stopUpdating();
				break;
			default:
				break;
			}
		}
	};
	
	private void startUpdating() {
		if (refreshTimer != null) {
			refreshTimer.cancel();
			refreshTimer = null;
		}
		refreshTimer = new Timer();
		refreshTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				Log.d("LG", "Updating information");
				if (mMediaControl != null && getTv() != null && getTv().hasCapability(MediaControl.Position)) {
					mMediaControl.getPosition(positionListener);
				}
				
				if (mMediaControl != null
						&& getTv() != null
						&& getTv().hasCapability(MediaControl.Duration)
						&& !getTv().hasCapability(MediaControl.PlayState_Subscribe)
						&& totalTimeDuration <= 0) {
					mMediaControl.getDuration(durationListener);
				}
			}
		}, 0, REFRESH_INTERVAL_MS);
	}
	
	private void stopUpdating() {
		if (refreshTimer == null)
			return;
		
		refreshTimer.cancel();
		refreshTimer = null;
	}
	
	private PositionListener positionListener = new PositionListener() {
		
		@Override public void onError(ServiceCommandError error) { }
		
		@Override
		public void onSuccess(Long position) {
			positionTextView.setText(formatTime(position.intValue()));
			mSeekBar.setProgress(position.intValue());
		}
	};
	
	private DurationListener durationListener = new DurationListener() {
		
		@Override public void onError(ServiceCommandError error) { }
		
		@Override
		public void onSuccess(Long duration) {
			totalTimeDuration = duration;
			mSeekBar.setMax(duration.intValue());
			durationTextView.setText(formatTime(duration.intValue()));
		}
	};
	
	private String formatTime(long millisec) {
		int seconds = (int) (millisec / 1000);
		int hours = seconds / (60 * 60);
		seconds %= (60 * 60);
		int minutes = seconds / 60;
		seconds %= 60;

		String time;
		if (hours > 0) {
			time = String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds);
		}
		else {
			time = String.format(Locale.US, "%d:%02d", minutes, seconds);
		}

		return time;
	}
}
