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

import java.io.InputStream;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.connectsdk.core.MediaInfo;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.sampler.R;
import com.connectsdk.sampler.util.TestResponseObject;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.MediaControl.DurationListener;
import com.connectsdk.service.capability.MediaControl.PlayStateListener;
import com.connectsdk.service.capability.MediaControl.PlayStateStatus;
import com.connectsdk.service.capability.MediaControl.PositionListener;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.capability.MediaPlayer.MediaInfoListener;
import com.connectsdk.service.capability.MediaPlayer.MediaLaunchObject;
import com.connectsdk.service.capability.PlaylistControl;
import com.connectsdk.service.capability.VolumeControl;
import com.connectsdk.service.capability.VolumeControl.VolumeListener;
import com.connectsdk.service.capability.listeners.ResponseListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.sessions.LaunchSession;

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
    public Button mediaInfoButton;
    public Button playlistButton;
    public Button previousButton;
    public Button nextButton;
    public Button jumpButton;

    public static LaunchSession launchSession;

    public TextView positionTextView;
    public TextView durationTextView;
    public TextView mediaInfoTextView;
    public SeekBar mSeekBar;
    public boolean mIsUserSeeking;
    public SeekBar mVolumeBar;

    public EditText positionTrackView;

    public ImageView mediaInfoImageView;

    public boolean mSeeking;
    public Runnable mRefreshRunnable;
    public static final int REFRESH_INTERVAL_MS = (int) TimeUnit.SECONDS.toMillis(1);
    public Handler mHandler;
    public long totalTimeDuration;
    public boolean mIsGettingPlayPosition;

    
    static boolean isPlayingImage = false;
    static boolean isPlaying = false;

    private MediaControl mMediaControl = null;
    private PlaylistControl mPlaylistControl = null;

    private Timer refreshTimer;
    
    public TestResponseObject testResponse;

    public MediaPlayerFragment() {};

    public MediaPlayerFragment(Context context)
    {
        super(context);

        mIsUserSeeking = false;
        mSeeking = false;
        mIsGettingPlayPosition = false;
        testResponse = new TestResponseObject();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        setRetainInstance(true);
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
        mediaInfoButton = (Button) rootView.findViewById(R.id.mediaInfo_button);
        playlistButton = (Button) rootView.findViewById(R.id.playlistButton);
        previousButton = (Button) rootView.findViewById(R.id.previousButton);
        nextButton = (Button) rootView.findViewById(R.id.nextButton);
        jumpButton = (Button) rootView.findViewById(R.id.jumpButton);

        positionTextView = (TextView) rootView.findViewById(R.id.stream_position);
        durationTextView = (TextView) rootView.findViewById(R.id.stream_duration);
        mediaInfoTextView = (TextView) rootView.findViewById(R.id.mediaInfo_textView);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.stream_seek_bar);
        mVolumeBar = (SeekBar) rootView.findViewById(R.id.volume_seek_bar);
        positionTrackView = (EditText) rootView.findViewById(R.id.positionText);
        mediaInfoImageView = (ImageView) rootView.findViewById(R.id.mediaInfo_imageView);

        buttons = new Button[] {
                photoButton, 
                videoButton, 
                audioButton, 
                playButton, 
                pauseButton, 
                stopButton, 
                rewindButton, 
                fastForwardButton, 
                closeButton,
                mediaInfoButton,
                playlistButton,
                previousButton,
                nextButton,
                jumpButton
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
            mPlaylistControl = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPlaying) {
            startUpdating();
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
        if (getTv().hasCapability(MediaPlayer.Display_Image)) {
            photoButton.setEnabled(true);
            photoButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    showImage();
                }
            });
        }
        else {
            disableButton(photoButton);
        }

        totalTimeDuration = -1;

        if (getTv().hasCapability(MediaPlayer.Play_Video)) {
            videoButton.setEnabled(true);
            videoButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    playVideo();
                }
            });
        }
        else {
            disableButton(videoButton);
        }

        if (getTv().hasCapability(MediaPlayer.Play_Audio)) {
            audioButton.setEnabled(true);
            audioButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    playAudio();
                }
            });
        } else {
            disableButton(audioButton);
        }

        if (getTv().hasCapability(MediaPlayer.Play_Playlist)) {
            playlistButton.setEnabled(true);
            playlistButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    playM3U();
                }
            });
        } else {
            disableButton(playlistButton);
        }

        mVolumeBar.setEnabled(getTv().hasCapability(VolumeControl.Volume_Set));
        mVolumeBar.setOnSeekBarChangeListener(volumeListener);

        if (getTv().hasCapability(VolumeControl.Volume_Get)) {
            getVolumeControl().getVolume(getVolumeListener);
        }

        if (getTv().hasCapability(VolumeControl.Volume_Subscribe)) {
            getVolumeControl().subscribeVolume(getVolumeListener);
        }

        if (getTv().hasCapability(MediaPlayer.MediaInfo_Get)) {
            mediaInfoButton.setEnabled(true);
            mediaInfoButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    getMediaPlayer().getMediaInfo(mediaInfoListener);
                }
            });
        }
        else 
            mediaInfoButton.setEnabled(false);

        if (getTv().hasCapability(MediaPlayer.MediaInfo_Subscribe)) {
            getMediaPlayer().subscribeMediaInfo(mediaInfoListener);
        }
        if (!isPlaying || !isPlayingImage)
            disableMedia();
        if (isPlaying) enableMedia();
        else if (isPlayingImage) {
            closeButton.setEnabled(true);
            closeButton.setOnClickListener(closeListener);
            stopUpdating();
        }
    }

    private void playAudio() {
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
                if (launchSession != null) {
                    launchSession.close(null);
                    launchSession = null;
                    stopUpdating();
                    disableMedia();
                    isPlaying = isPlayingImage = false;
                }
            }

            @Override
            public void onSuccess(MediaLaunchObject object) {
                Log.d("LG", "Started playing audio");

                launchSession = object.launchSession;
                mMediaControl = object.mediaControl;
                mPlaylistControl = object.playlistControl;

                stopUpdating();
                enableMedia();
                isPlaying = true;
                disconnectWebAppSession();

            }
        });
    }

    private void playM3U() {
        String mediaURL = "http://ec2-54-201-108-205.us-west-2.compute.amazonaws.com/samples/media/example-m3u-playlist.m3u";
        String iconURL = "http://ec2-54-201-108-205.us-west-2.compute.amazonaws.com/samples/media/audioIcon.jpg";
        String title = "Playlist";
        String description = "Playlist description";
        String mimeType = "application/x-mpegurl";

        boolean shouldLoop = false;

        getMediaPlayer().playMedia(mediaURL, mimeType, title, description, iconURL, shouldLoop, new MediaPlayer.LaunchListener() {

            @Override
            public void onError(ServiceCommandError error) {
                Log.d("LG", "Error playing audio");
                if (launchSession != null) {
                    launchSession.close(null);
                    launchSession = null;
                    stopUpdating();
                    disableMedia();
                    isPlaying = isPlayingImage = false;
                }
            }

            @Override
            public void onSuccess(MediaLaunchObject object) {
                Log.d("LG", "Started playing playlist");
                launchSession = object.launchSession;
                mMediaControl = object.mediaControl;
                mPlaylistControl = object.playlistControl;
                stopUpdating();
                enableMedia();
                isPlaying = true;
                disconnectWebAppSession();
            }
        });
    }

    private void showImage() {
        disableMedia();

        String imagePath = "http://ec2-54-201-108-205.us-west-2.compute.amazonaws.com/samples/media/photo.jpg";
        String mimeType = "image/jpeg";
        String title = "Sintel Character Design";
        String description = "Blender Open Movie Project";
        String icon = "http://ec2-54-201-108-205.us-west-2.compute.amazonaws.com/samples/media/photoIcon.jpg";

        getMediaPlayer().displayImage(imagePath, mimeType, title, description, icon, new MediaPlayer.LaunchListener() {

            @Override
            public void onSuccess(MediaLaunchObject object) {
                launchSession = object.launchSession;
                closeButton.setEnabled(true);
                testResponse =  new TestResponseObject(true, 200, TestResponseObject.display_image);
                closeButton.setOnClickListener(closeListener);
                stopUpdating();
                isPlayingImage = true;
                disconnectWebAppSession();
               
            }

            @Override
            public void onError(ServiceCommandError error) {
                Log.e("Error", error.getMessage());
                if (launchSession != null) {
                    launchSession.close(null);
                    launchSession = null;
                    testResponse =  new TestResponseObject(false, error.getCode(), error.getMessage());
                    stopUpdating();
                    disableMedia();
                    isPlaying = isPlayingImage = false;
                    
                }
            }
        });
    }

    private void playVideo() {
        String videoPath = "http://ec2-54-201-108-205.us-west-2.compute.amazonaws.com/samples/media/video.mp4";
        String mimeType = "video/mp4";
        String title = "Sintel Trailer";
        String description = "Blender Open Movie Project";
        String icon = "http://ec2-54-201-108-205.us-west-2.compute.amazonaws.com/samples/media/videoIcon.jpg";

        getMediaPlayer().playMedia(videoPath, mimeType, title, description, icon, false, new MediaPlayer.LaunchListener() {

            public void onSuccess(MediaLaunchObject object) {
                launchSession = object.launchSession;
                mMediaControl = object.mediaControl;
                mPlaylistControl = object.playlistControl;
                stopUpdating();
                enableMedia();
                isPlaying = true;
                disconnectWebAppSession();
            }

            @Override
            public void onError(ServiceCommandError error) {
                if (launchSession != null) {
                    launchSession.close(null);
                    launchSession = null;
                    stopUpdating();
                    disableMedia();
                    isPlaying = isPlayingImage = false;
                }
            }
        });
    }

    @Override
    public void disableButtons() {
        mSeekBar.setEnabled(false);
        mVolumeBar.setEnabled(false);
        mVolumeBar.setOnSeekBarChangeListener(null);
        positionTextView.setEnabled(false);
        durationTextView.setEnabled(false);

        mediaInfoTextView.setText("");
        mediaInfoImageView.setImageBitmap(null);

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
        previousButton.setEnabled(getTv().hasCapability(PlaylistControl.Previous));
        nextButton.setEnabled(getTv().hasCapability(PlaylistControl.Next));
        jumpButton.setEnabled(getTv().hasCapability(PlaylistControl.JumpToTrack));


        fastForwardButton.setOnClickListener(fastForwardListener);
        mSeekBar.setOnSeekBarChangeListener(seekListener);
        rewindButton.setOnClickListener(rewindListener);
        stopButton.setOnClickListener(stopListener);
        playButton.setOnClickListener(playListener);
        pauseButton.setOnClickListener(pauseListener);
        previousButton.setOnClickListener(previousListener);
        nextButton.setOnClickListener(nextListener);
        jumpButton.setOnClickListener(jumpListener);
        closeButton.setOnClickListener(closeListener);

        if (getTv().hasCapability(MediaControl.PlayState_Subscribe) && !isPlaying) {
            mMediaControl.subscribePlayState(playStateListener);
        } else {
            if (mMediaControl != null) {
                mMediaControl.getDuration(durationListener);
            }
            startUpdating();
        }
    }

    public void disableMedia() {
        closeButton.setEnabled(false);
        closeButton.setOnClickListener(null);

        stopMedia();
    }

    public void stopMedia() {
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
        previousButton.setEnabled(false);
        previousButton.setOnClickListener(null);
        nextButton.setEnabled(false);
        nextButton.setOnClickListener(null);
        jumpButton.setEnabled(false);
        jumpButton.setOnClickListener(null);

        mSeekBar.setEnabled(false);
        mSeekBar.setOnSeekBarChangeListener(null);
        mSeekBar.setProgress(0);

        positionTextView.setText("--:--:--");
        durationTextView.setText("--:--:--");

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

    public View.OnClickListener previousListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mPlaylistControl != null)
                mPlaylistControl.previous(null);
        }
    };

    public View.OnClickListener nextListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mPlaylistControl != null)
                mPlaylistControl.next(null);
        }
    };

    public View.OnClickListener jumpListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mPlaylistControl != null) {
                mPlaylistControl.jumpToTrack(Integer.parseInt(positionTrackView.getText().toString()), null);
            }
        }
    };


    public View.OnClickListener closeListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (getMediaPlayer() != null) {
                if (launchSession != null)
                    launchSession.close(null);

                launchSession = null;

                disableMedia();
                stopUpdating();
                isPlaying = isPlayingImage = false;
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
                        stopMedia();
                        stopUpdating();
                        isPlaying = false;
                        isPlayingImage = true;
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
                    startUpdating();

                    if (mMediaControl != null && getTv().hasCapability(MediaControl.Duration)) {
                        mMediaControl.getDuration(durationListener);
                    }
                    break;
                case Finished:
                    positionTextView.setText("--:--");
                    durationTextView.setText("--:--");
                    mSeekBar.setProgress(0);

                default:
                    stopUpdating();
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

    private MediaInfoListener mediaInfoListener = new MediaInfoListener() {

        @Override
        public void onSuccess(MediaInfo mediaInfo) {
            String text = mediaInfo.getTitle();
            text += "\n";
            text += mediaInfo.getDescription();
            mediaInfoTextView.setText(text);
            final String stringUrl = mediaInfo.getImages().get(0).getUrl();

            if (stringUrl!=null) new DownloadImageTask(mediaInfoImageView).execute(stringUrl);
        }

        @Override
        public void onError(ServiceCommandError error) {

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

    private void disconnectWebAppSession() {
        if (WebAppFragment.mWebAppSession != null) {
            WebAppFragment.mWebAppSession.setWebAppSessionListener(null);
            WebAppFragment.mWebAppSession.close(null);
            WebAppFragment.isLaunched = false;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                Log.d("", urldisplay);
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
