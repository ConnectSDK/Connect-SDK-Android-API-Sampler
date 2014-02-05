package com.example.connect_sdk_sampler.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.widget.Button;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.service.capability.ExternalInputControl;
import com.connectsdk.service.capability.FivewayControl;
import com.connectsdk.service.capability.KeyboardControl;
import com.connectsdk.service.capability.Launcher;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.capability.MouseControl;
import com.connectsdk.service.capability.PowerControl;
import com.connectsdk.service.capability.SystemControl;
import com.connectsdk.service.capability.TVControl;
import com.connectsdk.service.capability.ToastControl;
import com.connectsdk.service.capability.VolumeControl;

public class BaseFragment extends Fragment {
	
	private ConnectableDevice mTv;
	private Launcher launcher;
	private MediaPlayer mediaPlayer;
	private MediaControl mediaControl;
	private TVControl tvControl;
	private VolumeControl volumeControl;
	private ToastControl toastControl;
	private MouseControl mouseControl;
	private KeyboardControl keyboardControl;
	private PowerControl powerControl;
	private ExternalInputControl externalInputControl;
	private SystemControl systemControl;
	private FivewayControl fivewayControl;
    public Button[] buttons;
    Context mContext;

    public BaseFragment(ConnectableDevice tv, Context context)
    {
        mTv = tv;
        mContext = context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTv(mTv);
    }

    public void setTv(ConnectableDevice tv)
	{
		mTv = tv;

        if (tv == null) {
        	launcher = null;
        	mediaPlayer = null;
        	mediaControl = null;
        	tvControl = null;
        	volumeControl = null;
        	toastControl = null;
        	keyboardControl = null;
        	mouseControl = null;
        	externalInputControl = null;
        	powerControl = null;
        	systemControl = null;
        	fivewayControl = null;
        	
            disableButtons();
        }
        else {
        	launcher = mTv.getLauncher();
        	mediaPlayer = mTv.getMediaPlayer();
        	mediaControl = mTv.getMediaControl();
        	tvControl = mTv.getTVControl();
        	volumeControl = mTv.getVolumeControl();
        	toastControl = mTv.getToastControl();
        	keyboardControl = mTv.getKeyboardControl();
        	mouseControl = mTv.getMouseControl();
        	externalInputControl = mTv.getExternalInputControl();
        	powerControl = mTv.getPowerControl();
        	systemControl = mTv.getSystemControl();
        	fivewayControl = mTv.getFivewayControl();
        	
            enableButtons();
        }
	}

    public void disableButtons() {
        if (buttons != null)
        {
            for (Button button : buttons)
            {
                button.setOnClickListener(null);
                button.setEnabled(false);
            }
        }
    }

    public void enableButtons() {
        if (buttons != null)
        {
            for (Button button : buttons)
                button.setEnabled(true);
        }
    }

    public ConnectableDevice getTv()
    {
        return mTv;
    }
    
    public Launcher getLauncher() 
    {
    	return launcher;
    }
    
    public MediaPlayer getMediaPlayer()
    {
    	return mediaPlayer;
    }
    
    public MediaControl getMediaControl()
    {
    	return mediaControl;
    }
    
    public VolumeControl getVolumeControl() 
    {
    	return volumeControl;
    }
    
    public TVControl getTVControl() 
    {
    	return tvControl;
    }
    
    public ToastControl getToastControl() 
    {
    	return toastControl;
    }
    
    public KeyboardControl getKeyboardControl() 
    {
    	return keyboardControl;
    }
    
    public MouseControl getMouseControl() 
    {
    	return mouseControl;
    }
    
    public ExternalInputControl getExternalInputControl()
    {
    	return externalInputControl;
    }
    
    public PowerControl getPowerControl() 
    {
    	return powerControl;
    }
    
    public SystemControl getSystemControl()
    {
    	return systemControl;
    }
    
    public FivewayControl getFivewayControl() 
    {
    	return fivewayControl;
    }
    
    public Context getContext()
    {
    	return mContext;
    }

	void runOnUiThread(Runnable run) {
		new Handler(getContext().getMainLooper()).post(run);
	}

}