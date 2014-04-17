package com.example.connect_sdk_sampler;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.device.PairingDialog;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.discovery.DiscoveryManager.PairingLevel;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.DeviceService.PairingType;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.command.ServiceCommandError;
import com.example.connect_sdk_sampler.fragments.BaseFragment;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    private ConnectableDevice mTV;
    private AlertDialog dialog;
    private AlertDialog pairingAlertDialog;
    
    private MenuItem connectItem;

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;

    private ConnectableDeviceListener deviceListener = new ConnectableDeviceListener() {
		
		@Override
		public void onPairingRequired(ConnectableDevice device, DeviceService service, PairingType pairingType) {
	        Log.d("2ndScreenAPP", "Connected to " + mTV.getIpAddress());

			switch (pairingType) { 
				case FIRST_SCREEN:
					Log.d("2ndScreenAPP", "First Screen");
		    		pairingAlertDialog.show();
					break;
					
				case PIN_CODE:
					Log.d("2ndScreenAPP", "Pin Code");
					PairingDialog dialog = new PairingDialog(MainActivity.this, mTV);
					
					AlertDialog d = dialog.getPairingDialog("Enter Pairing Code on TV");
					d.show();
					break;
				
				case NONE:
				default:
					break;
			}
		}
		
		@Override
		public void onConnectionFailed(ConnectableDevice device, ServiceCommandError error) {
			Log.d("2ndScreenAPP", "onConnectFailed");
			connectFailed(mTV);
		}

		@Override
		public void onDeviceReady(ConnectableDevice device) {
            if ( pairingAlertDialog.isShowing() ) {
            	Log.d("2ndScreenAPP", "onPairingSuccess");
    		    pairingAlertDialog.dismiss();
            }
        	registerSuccess(mTV);			
		}

		@Override
		public void onDeviceDisconnected(ConnectableDevice device) {
			Log.d("2ndScreenAPP", "Device Disconnected");
			connectEnded(mTV);
            connectItem.setTitle("Connect");
			
			BaseFragment frag = mSectionsPagerAdapter.getFragment(mViewPager.getCurrentItem());
			if ( frag != null ) {
				Toast.makeText(getApplicationContext(), "Device Disconnected", Toast.LENGTH_SHORT).show();
				frag.disableButtons();
			}
		}

		@Override
		public void onCapabilityUpdated(ConnectableDevice device, List<String> added, List<String> removed) {
			
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setIcon(mSectionsPagerAdapter.getIcon(i))
					.setTabListener(this));
		}

        setupPicker();

    	DiscoveryManager.getInstance().setPairingLevel(PairingLevel.ON);
		DiscoveryManager.getInstance().start();
    }

    public List<ConnectableDevice> getImageDevices() {
    	List<ConnectableDevice> imageDevices = new ArrayList<ConnectableDevice>();
    	
    	for (ConnectableDevice device : DiscoveryManager.getInstance().getCompatibleDevices().values()) {
    		if (device.hasCapability(MediaPlayer.Display_Image))
    			imageDevices.add(device);
    	}
    	
    	return imageDevices;
    }

    @Override
	protected void onDestroy() {
		super.onDestroy();

		if ( dialog != null ) {
			dialog.dismiss();
		}
		
		if ( mTV != null ) {
			mTV.disconnect();
		}
	}

    public void hConnectToggle()
    {
    	if ( !this.isFinishing() ) {
            if (mTV != null)
            {
                if (mTV.isConnected())
                    mTV.disconnect();
                
                connectItem.setTitle("Connect");
                mTV = null;
    			mSectionsPagerAdapter.getFragment(mViewPager.getCurrentItem()).setTv(null);
            } else
            {
                dialog.show();
            }
    	}
    }

    private void setupPicker()
    {
    	DiscoveryManager.getInstance().registerDefaultDeviceTypes();

        DevicePicker dp = new DevicePicker(this);
        dialog = dp.getPickerDialog("Device List", new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//            	DiscoveryManager.getInstance().stop();

                mTV = (ConnectableDevice)arg0.getItemAtPosition(arg2);
                mTV.addListener(deviceListener);
                mTV.connect();
                connectItem.setTitle("Connected");
            }
        });
        
        pairingAlertDialog = new AlertDialog.Builder(this)
        .setTitle("Pairing with TV")
        .setMessage("Please confirm the connect on your TV")
        .setPositiveButton("Okay", null)
        .setNegativeButton("Cancel", null)
        .create();
    }
    
    @Override
    protected void onResume() {

    	super.onResume();
    }

    void registerSuccess(ConnectableDevice device) {
        Log.d("2ndScreenAPP", "successful register");

        BaseFragment frag = mSectionsPagerAdapter.getFragment(mViewPager.getCurrentItem());
        if (frag != null)
        	frag.setTv(mTV);
    }

    void connectFailed(ConnectableDevice device) {
    	if (device != null)
    		Log.d("2ndScreenAPP", "Failed to connect to " + device.getIpAddress());

        if (mTV != null) {
        	mTV.disconnect();
        	mTV = null;
        }
    }

    void connectEnded(ConnectableDevice device) {
        if ( pairingAlertDialog.isShowing() ) {
        	pairingAlertDialog.dismiss();
        }
        mTV = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        connectItem = menu.getItem(0);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.action_connect:
    		hConnectToggle();
    		return true;
    	}
        return super.onOptionsItemSelected(item);
    }
    
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
		getSupportActionBar().setTitle(mSectionsPagerAdapter.getTitle(tab.getPosition()));
		BaseFragment frag = mSectionsPagerAdapter.getFragment(tab.getPosition());
		if (frag != null)
			frag.setTv(mTV);
	}

	@Override public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }
	@Override public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }
}
