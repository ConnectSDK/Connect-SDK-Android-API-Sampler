package com.example.connect_sdk_sampler.fragments;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.connectsdk.core.ExternalInputInfo;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.service.capability.ExternalInputControl;
import com.connectsdk.service.capability.listeners.ExternalInputListListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.example.connect_sdk_sampler.R;

public class InputsFragment extends BaseFragment {
	public Button inputPickerButton;
	
    public ListView inputListView;
    public ArrayAdapter<String> adapter;
    
    public InputsFragment(ConnectableDevice tv, Context context)
    {
        super(tv, context);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_inputs, container, false);

		inputListView = (ListView) rootView.findViewById(R.id.inputListView);
		adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
		inputListView.setAdapter(adapter);

		inputListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String input = (String) arg0.getItemAtPosition(arg2);
				if ( getTv().hasCapability(ExternalInputControl.kExternalInputControlSet) ) {
					ExternalInputInfo inputInfo = new ExternalInputInfo();
					inputInfo.setId(input);
					getExternalInputControl().setExternalInput(inputInfo, null);
				}
			}
        });
		
		inputPickerButton = (Button) rootView.findViewById(R.id.inputPickerButton);
		
		buttons = new Button[1];
		buttons[0] = inputPickerButton;
        
        return rootView;
	}

	@Override
	public void enableButtons() {
		if ( getExternalInputControl() != null ) {
			getExternalInputControl().getExternalInputList(new ExternalInputListListener() {
				
				@Override
				public void onGetExternalInputListSuccess(List<ExternalInputInfo> externalInputList) {
					for (int i = 0; i < externalInputList.size(); i++) {
						ExternalInputInfo input = externalInputList.get(i);
						final String deviceId = input.getId();
						
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								adapter.add(deviceId);
							}
						});
					}
				}
				
				@Override
				public void onGetExternalInputListFailed(ServiceCommandError arg0) {
				}
			});
		}

		inputPickerButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ( inputPickerButton.isSelected() ) { 
					inputPickerButton.setSelected(false);
					getExternalInputControl().closeInputPicker(null);
				}
				else {
					inputPickerButton.setSelected(true);
					if ( getExternalInputControl() != null ) {
						getExternalInputControl().launchInputPicker(null);
					}
				}
			}
		});

		super.enableButtons();
		
		if ( !getTv().hasCapability(ExternalInputControl.kExternalInputControlPickerLaunch) ) {
			inputPickerButton.setEnabled(false);
		}
	}

	@Override
	public void disableButtons() {
		adapter.clear();
		
		super.disableButtons();
	}
}
