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
import com.connectsdk.sampler.util.TestResponseObject;
import com.connectsdk.sampler.widget.ChannelAdapter;
import com.connectsdk.service.capability.KeyControl;
import com.connectsdk.service.capability.PowerControl;
import com.connectsdk.service.capability.TVControl;
import com.connectsdk.service.capability.KeyControl.KeyCode;
import com.connectsdk.service.capability.TVControl.ChannelListListener;
import com.connectsdk.service.capability.TVControl.ChannelListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.ServiceSubscription;

public class TVFragment extends BaseFragment {
    public Button channelUpButton;
    public Button channelDownButton;
    public Button powerOffButton;
    public Button mode3DButton;

    public Button[] numberButtons = new Button[10];
    public Button dashButton;
    public Button enterButton;

    public ListView channelListView;
    public ChannelAdapter adapter;

    boolean mode3DToggle;
    boolean incomingCallToggle;
    public TestResponseObject testResponse;

    private ServiceSubscription<ChannelListener> mCurrentChannelSubscription;

    public TVFragment() {};

    public TVFragment(Context context)
    {
        super(context);
        testResponse = new TestResponseObject();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_tv, container, false);

        numberButtons[0] = (Button) rootView.findViewById(R.id.numberButton0);
        numberButtons[1] = (Button) rootView.findViewById(R.id.numberButton1);
        numberButtons[2] = (Button) rootView.findViewById(R.id.numberButton2);
        numberButtons[3] = (Button) rootView.findViewById(R.id.numberButton3);
        numberButtons[4] = (Button) rootView.findViewById(R.id.numberButton4);
        numberButtons[5] = (Button) rootView.findViewById(R.id.numberButton5);
        numberButtons[6] = (Button) rootView.findViewById(R.id.numberButton6);
        numberButtons[7] = (Button) rootView.findViewById(R.id.numberButton7);
        numberButtons[8] = (Button) rootView.findViewById(R.id.numberButton8);
        numberButtons[9] = (Button) rootView.findViewById(R.id.numberButton9);

        dashButton = (Button) rootView.findViewById(R.id.dashButton);
        enterButton = (Button) rootView.findViewById(R.id.enterButton);

        channelUpButton = (Button) rootView.findViewById(R.id.channelUpButton);
        channelDownButton = (Button) rootView.findViewById(R.id.channelDownButton);
        powerOffButton = (Button) rootView.findViewById(R.id.powerOffButton);
        mode3DButton = (Button) rootView.findViewById(R.id.mode3DButton);

        channelListView = (ListView) rootView.findViewById(R.id.channelListView);
        adapter = new ChannelAdapter(getContext(), R.layout.channel_item);

        channelListView.setAdapter(adapter);

        buttons = new Button[] {
                numberButtons[0],
                numberButtons[1],
                numberButtons[2],
                numberButtons[3],
                numberButtons[4],
                numberButtons[5],
                numberButtons[6],
                numberButtons[7],
                numberButtons[8],
                numberButtons[9],
                dashButton,
                enterButton,
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

        if (getTv().hasAnyCapability(KeyControl.KeyCode)) {
            numberButtons[0].setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_0, null);;
                }
            });
            
            numberButtons[1].setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_1, null);;
                }
            });

            numberButtons[2].setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_2, null);;
                }
            });

            numberButtons[3].setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_3, null);;
                }
            });

            numberButtons[4].setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_4, null);;
                }
            });

            numberButtons[5].setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_5, null);;
                }
            });

            numberButtons[6].setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_6, null);;
                }
            });

            numberButtons[7].setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_7, null);;
                }
            });

            numberButtons[8].setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_8, null);;
                }
            });

            numberButtons[9].setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    getKeyControl().sendKeyCode(KeyCode.NUM_9, null);;
                }
            });

            dashButton.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    getKeyControl().sendKeyCode(KeyCode.DASH, null);;
                }
            });

            enterButton.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    getKeyControl().sendKeyCode(KeyCode.ENTER, null);;
                }
            });
        }
        else {
            disableButton(numberButtons[0]);
            disableButton(numberButtons[1]);
            disableButton(numberButtons[2]);
            disableButton(numberButtons[3]);
            disableButton(numberButtons[4]);
            disableButton(numberButtons[5]);
            disableButton(numberButtons[6]);
            disableButton(numberButtons[7]);
            disableButton(numberButtons[8]);
            disableButton(numberButtons[9]);
            disableButton(dashButton);
            disableButton(enterButton);
        }

        if (getTv().hasCapability(TVControl.Channel_Up)) {
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

        if (getTv().hasCapability(TVControl.Channel_Down)) {
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

        if (getTv().hasCapability(PowerControl.Off)) {
            powerOffButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                	testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.Power_OFF);
                    getPowerControl().powerOff(null);
                }
            });
        }
        else {
            disableButton(powerOffButton);
        }

        if (getTv().hasCapability(TVControl.Set_3D)) {
            mode3DButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (getTv().hasCapability(TVControl.Set_3D)) {
                        if (getTVControl() != null) {
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

        if (getTv().hasCapability(TVControl.Channel_List)) {
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

        if (getTv().hasCapability(TVControl.Channel_Subscribe)) {
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
