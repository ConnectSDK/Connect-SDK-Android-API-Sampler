package com.example.connect_sdk_sampler;

import java.util.List;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
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
import com.connectsdk.service.DeviceService.PairingType;
import com.connectsdk.service.command.ServiceCommandError;
import com.example.connect_sdk_sampler.fragments.AppsFragment;
import com.example.connect_sdk_sampler.fragments.BaseFragment;
import com.example.connect_sdk_sampler.fragments.FivewayFragment;
import com.example.connect_sdk_sampler.fragments.InputsFragment;
import com.example.connect_sdk_sampler.fragments.MediaPlayerFragment;
import com.example.connect_sdk_sampler.fragments.TVFragment;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private BaseFragment mActiveFragment;

    private ConnectableDevice mTV;
    private AlertDialog dialog;
    private AlertDialog pairingAlertDialog;

    private ConnectableDeviceListener deviceListener = new ConnectableDeviceListener() {
		
		@Override
		public void onPairingRequired(PairingType pairingType) {
	        Log.d("2ndScreenAPP", "Connected to " + mTV.getIpAddress());

			switch (pairingType) { 
				case FIRST_SCREEN:
					Log.d("2ndScreenAPP", "First Screen");
		            runOnUiThread(new Runnable() {
		    			
		    			@Override
		    			public void run() {
		    				pairingAlertDialog.show();
		    			}
		    		});
					break;
					
				case PIN_CODE:
					Log.d("2ndScreenAPP", "Pin Code");
					runOnUiThread(new Runnable() {
						public void run() {
							PairingDialog dialog = new PairingDialog(MainActivity.this, mTV);
							
							AlertDialog d = dialog.getPairingDialog("Enter Pairing Code on TV");
							d.show();
						}
					});
					break;
				
				case NONE:
				default:
					break;
			}
		}
		
		@Override
		public void onConnectionFailed(ServiceCommandError error) {
			Log.d("2ndScreenAPP", "onConnectFailed");
			connectFailed(mTV);
		}

		@Override
		public void onDeviceReady() {
            if ( pairingAlertDialog.isShowing() ) {
            	Log.d("2ndScreenAPP", "onPairingSuccess");
            	runOnUiThread(new Runnable()  {
    				
    				@Override
    				public void run() {
    		        	pairingAlertDialog.dismiss();
    				}
    			});
            }
        	registerSuccess(mTV);			
		}

		@Override
		public void onDeviceDisconnected() {
			Log.d("2ndScreenAPP", "Device Disconnected");
			connectEnded(mTV);
			
			if ( mActiveFragment != null ) {
	            runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "Device Disconnected", Toast.LENGTH_SHORT).show();
						mActiveFragment.disableButtons();
					}
				});
			}			
		}

		@Override
		public void onCapabilityUpdated(List<String> added, List<String> removed) {
			
		}
	};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setupPicker();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
	protected void onDestroy() {
		super.onDestroy();

		if ( dialog != null ) {
			dialog.dismiss();;
		}
		
		if ( mTV != null ) {
			mTV.disconnect();
		}
	}

	@Override
    public void onNavigationDrawerItemSelected(int position) {
        BaseFragment newFragment;

        switch (position)
        {
            case 1:
                newFragment = new TVFragment(mTV, this);
                break;

            case 2:
                newFragment = new AppsFragment(mTV, this);
                break;

            case 3:
                newFragment = new FivewayFragment(mTV, this);
                break;

            case 4:
                newFragment = new InputsFragment(mTV, this);
                break;

            case 0:
            default:
                newFragment = new MediaPlayerFragment(mTV, this);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (mActiveFragment != null)
        {
            runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
		            mActiveFragment.setTv(null);
				}
			});
            fragmentManager.beginTransaction().remove(mActiveFragment).commit();
        }

        fragmentManager.beginTransaction()
                .add(R.id.container, newFragment)
                .commit();

        mActiveFragment = newFragment;
    }

    public void hConnectToggle()
    {
    	if ( !this.isFinishing() ) {
            if (mTV != null)
            {
                if (mTV.isConnected())
                    mTV.disconnect();

                mTV = null;
                runOnUiThread(new Runnable() {
    				
    				@Override
    				public void run() {
    			           mActiveFragment.setTv(null);
    				}
    			});
            } else
            {
            	DiscoveryManager.getInstance(getApplicationContext()).setPairingLevel(PairingLevel.ON);
            	DiscoveryManager.getInstance(getApplicationContext()).start();
                dialog.show();
            }
    	}
    }

    private void setupPicker()
    {
    	DiscoveryManager.getInstance(getApplicationContext()).registerDefaultDeviceTypes();

        DevicePicker dp = new DevicePicker(this);
        dialog = dp.getPickerDialog("Device List", new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
            	DiscoveryManager.getInstance(getApplicationContext()).stop();

                mTV = (ConnectableDevice)arg0.getItemAtPosition(arg2);
                mTV.addListener(deviceListener);
                mTV.connect();
            }
        });
        
        pairingAlertDialog = new AlertDialog.Builder(this)
        .setTitle("Pairing with TV")
        .setMessage("Please confirm the connect on your TV")
        .setPositiveButton("Okay", null)
        .setNegativeButton("Cancel", null)
        .create();
    }

    void registerSuccess(ConnectableDevice device) {
        Log.d("2ndScreenAPP", "successful register");

        runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
		        mActiveFragment.setTv(mTV);
			}
		});
    }

    void connectFailed(ConnectableDevice device) {
        Log.d("2ndScreenAPP", "Failed to connect to " + device.getIpAddress());
        mTV.disconnect();
        mTV = null;
    }

    void connectEnded(ConnectableDevice device) {
        if ( pairingAlertDialog.isShowing() ) {
        	runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
		        	pairingAlertDialog.dismiss();
				}
			});
        }
        mTV = null;
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);

            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
