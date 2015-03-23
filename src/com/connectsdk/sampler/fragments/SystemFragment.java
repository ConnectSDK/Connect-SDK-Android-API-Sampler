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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;

import com.connectsdk.core.ExternalInputInfo;
import com.connectsdk.sampler.R;
import com.connectsdk.sampler.util.TestResponseObject;
import com.connectsdk.service.capability.ExternalInputControl;
import com.connectsdk.service.capability.ExternalInputControl.ExternalInputListListener;
import com.connectsdk.service.capability.Launcher;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.VolumeControl;
import com.connectsdk.service.capability.VolumeControl.MuteListener;
import com.connectsdk.service.capability.VolumeControl.VolumeListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.ServiceSubscription;
import com.connectsdk.service.sessions.LaunchSession;

public class SystemFragment extends BaseFragment {
    public CheckBox muteToggleButton;
    public Button volumeUpButton;
    public Button volumeDownButton;
    public SeekBar volumeSlider;

    public Button playButton;
    public Button pauseButton;
    public Button stopButton;
    public Button rewindButton;
    public Button fastForwardButton;

    public Button inputPickerButton;

    public ListView inputListView;
    public ArrayAdapter<String> adapter;

    public LaunchSession inputPickerSession;
    
    public TestResponseObject testResponse;

    private ServiceSubscription<VolumeListener> mVolumeSubscription;
    private ServiceSubscription<MuteListener> mMuteSubscription;

    public SystemFragment() {};

    public SystemFragment(Context context)
    {
        super(context);
        testResponse = new TestResponseObject();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_system, container, false);

        muteToggleButton = (CheckBox) rootView.findViewById(R.id.muteToggle);
        volumeUpButton = (Button) rootView.findViewById(R.id.volumeUpButton);
        volumeDownButton = (Button) rootView.findViewById(R.id.volumeDownButton);
        inputListView = (ListView) rootView.findViewById(R.id.inputListView);
        playButton = (Button) rootView.findViewById(R.id.playButton);
        pauseButton = (Button) rootView.findViewById(R.id.pauseButton);
        stopButton = (Button) rootView.findViewById(R.id.stopButton);
        rewindButton = (Button) rootView.findViewById(R.id.rewindButton);
        fastForwardButton = (Button) rootView.findViewById(R.id.fastForwardButton);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        inputListView.setAdapter(adapter);

        volumeSlider = (SeekBar) rootView.findViewById(R.id.volumeSlider);
        volumeSlider.setMax(100);

        inputListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String input = (String) arg0.getItemAtPosition(arg2);
                if (getTv().hasCapability(ExternalInputControl.Set)) {
                    ExternalInputInfo inputInfo = new ExternalInputInfo();
                    inputInfo.setId(input);
                    getExternalInputControl().setExternalInput(inputInfo, null);
                }
            }
        });

        inputPickerButton = (Button) rootView.findViewById(R.id.inputPickerButton);

        buttons = new Button[] {
                inputPickerButton, 
                volumeUpButton, 
                volumeDownButton, 
                muteToggleButton, 
                pauseButton, 
                playButton, 
                stopButton, 
                rewindButton, 
                fastForwardButton
        };
        buttons[0] = inputPickerButton;

        return rootView;
    }

    @Override
    public void enableButtons() {
        super.enableButtons();

        if (getTv().hasCapability(ExternalInputControl.List))
            getExternalInputControl().getExternalInputList(externalInputListener);

        volumeSlider.setEnabled(getTv().hasCapability(VolumeControl.Volume_Set));
        inputPickerButton.setEnabled(getTv().hasCapability(ExternalInputControl.Picker_Launch));
        muteToggleButton.setEnabled(getTv().hasCapability(VolumeControl.Mute_Set));
        volumeUpButton.setEnabled(getTv().hasCapability(VolumeControl.Volume_Up_Down));
        volumeDownButton.setEnabled(getTv().hasCapability(VolumeControl.Volume_Up_Down));

        playButton.setEnabled(getTv().hasCapability(MediaControl.Play));
        pauseButton.setEnabled(getTv().hasCapability(MediaControl.Pause));
        stopButton.setEnabled(getTv().hasCapability(MediaControl.Stop));
        rewindButton.setEnabled(getTv().hasCapability(MediaControl.Rewind));
        fastForwardButton.setEnabled(getTv().hasCapability(MediaControl.FastForward));

        if (getTv().hasCapability(VolumeControl.Volume_Subscribe))
            mVolumeSubscription = getVolumeControl().subscribeVolume(volumeListener);

        if (getTv().hasCapability(VolumeControl.Mute_Subscribe))
            mMuteSubscription = getVolumeControl().subscribeMute(muteListener);

        inputPickerButton.setOnClickListener(inputPickerClickListener);
        volumeUpButton.setOnClickListener(volumeChangedClickListener);
        volumeDownButton.setOnClickListener(volumeChangedClickListener);
        muteToggleButton.setOnClickListener(muteToggleClickListener);
        volumeSlider.setOnSeekBarChangeListener(volumeSeekListener);

        playButton.setOnClickListener(playClickListener);
        pauseButton.setOnClickListener(pauseClickListener);
        stopButton.setOnClickListener(stopClickListener);
        rewindButton.setOnClickListener(rewindClickListener);
        fastForwardButton.setOnClickListener(fastForwardClickListener);
    }

    private View.OnClickListener muteToggleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getVolumeControl().setMute(muteToggleButton.isChecked(), null);
            if(muteToggleButton.isChecked()) {
            testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.Muted_Media);
            } else if(!muteToggleButton.isChecked()){
            	testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.UnMuted_Media);
            }
        }
    };

    private View.OnClickListener volumeChangedClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.volumeDownButton:
                    getVolumeControl().volumeDown(null);
                    testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.VolumeDown);
                    break;
                case R.id.volumeUpButton:
                    getVolumeControl().volumeUp(null);
                    testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.VolumeUp);
                    break;
            }
        }
    };

    private View.OnClickListener inputPickerClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (inputPickerButton.isSelected()) {
                if (inputPickerSession != null) {
                    inputPickerButton.setSelected(false);
                    getExternalInputControl().closeInputPicker(inputPickerSession, null);
                }
            }
            else {
                inputPickerButton.setSelected(true);
                if (getExternalInputControl() != null) {
                    getExternalInputControl().launchInputPicker(new Launcher.AppLaunchListener() {

                        public void onError(ServiceCommandError error) { }

                        public void onSuccess(LaunchSession object) {
                            inputPickerSession = object;
                            testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.InputPickerVisible);
                        }
                    });
                }
            }
        }
    };

    private ExternalInputListListener externalInputListener = new ExternalInputListListener() {

        @Override
        public void onSuccess(List<ExternalInputInfo> externalInputList) {
            adapter.clear();
            for (int i = 0; i < externalInputList.size(); i++) {
                ExternalInputInfo input = externalInputList.get(i);
                final String deviceId = input.getId();

                adapter.add(deviceId);
            }
        }

        @Override
        public void onError(ServiceCommandError arg0) {
        }
    };

    private VolumeListener volumeListener = new VolumeListener() {

        public void onSuccess(Float volume) {
            volumeSlider.setProgress((int) (volume * 100));
        }

        @Override
        public void onError(ServiceCommandError error) {
            Log.d("LG", "Error subscribing to volume: " + error);
        }
    };

    private MuteListener muteListener = new MuteListener() {

        @Override
        public void onSuccess(Boolean object) {
            muteToggleButton.setChecked(object);
        }

        @Override
        public void onError(ServiceCommandError error) {
            Log.d("LG", "Error subscribing to mute: " + error);
        }
    };

    private SeekBar.OnSeekBarChangeListener volumeSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                float fVol = (float)(progress / 100.0);
                getVolumeControl().setVolume(fVol, null);
            }
        }

        @Override public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override public void onStopTrackingTouch(SeekBar seekBar) { }
    };

    private OnClickListener playClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            getMediaControl().play(null);
        }
    };

    private OnClickListener pauseClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            getMediaControl().pause(null);
        }
    };

    private OnClickListener stopClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            getMediaControl().stop(null);
        }
    };

    private OnClickListener rewindClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            getMediaControl().rewind(null);
        }
    };

    private OnClickListener fastForwardClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            getMediaControl().fastForward(null);
        }
    };

    @Override
    public void disableButtons() {
        adapter.clear();
        volumeSlider.setEnabled(false);
        volumeSlider.setOnSeekBarChangeListener(null);

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
