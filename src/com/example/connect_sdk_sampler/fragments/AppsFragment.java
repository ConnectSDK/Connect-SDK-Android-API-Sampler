package com.example.connect_sdk_sampler.fragments;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.connectsdk.core.AppInfo;
import com.connectsdk.core.LaunchSession;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.service.capability.Launcher;
import com.connectsdk.service.capability.ToastControl;
import com.connectsdk.service.capability.listeners.AppIdListener;
import com.connectsdk.service.capability.listeners.AppListListener;
import com.connectsdk.service.capability.listeners.LaunchListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.ServiceSubscription;
import com.example.connect_sdk_sampler.R;
import com.example.connect_sdk_sampler.widget.AppAdapter;

public class AppsFragment extends BaseFragment {
//	public Button smartWorldButton;
    public Button browserButton;
    public Button deepLinkButton;
    public Button youtubeButton;
    public Button toastButton;

    public ListView appListView;
    public AppAdapter adapter;
    List<AppInfo> applicationList;
    LaunchSession runningAppSession;
    
    ServiceSubscription runningAppSubs;

    public AppsFragment(ConnectableDevice tv, Context context)
    {
        super(tv, context);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_apps, container, false);

        browserButton = (Button) rootView.findViewById(R.id.browserButton);
        deepLinkButton = (Button) rootView.findViewById(R.id.deepLinkButton);
        youtubeButton = (Button) rootView.findViewById(R.id.youtubeButton);
        toastButton = (Button) rootView.findViewById(R.id.toastButton);

        appListView = (ListView) rootView.findViewById(R.id.appListView);
        adapter = new AppAdapter(getContext(), android.R.layout.simple_list_item_1);
        appListView.setAdapter(adapter);

        buttons = new Button[4];
        buttons[0] = browserButton;
        buttons[1] = deepLinkButton;
        buttons[2] = youtubeButton;
        buttons[3] = toastButton;
        
		return rootView;
	}

    @Override
    public void enableButtons()
    {
        browserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	if ( browserButton.isSelected() ) {
            		browserButton.setSelected(false);
            		if (runningAppSession != null) {
            			runningAppSession.close(null);
            		}
            	}
            	else {
                	browserButton.setSelected(true);
            		if ( getTv().hasCapability(Launcher.kLauncherBrowser) ) {
            			if ( getTv().hasCapability(Launcher.kLauncherBrowserParams) ) {
            				getLauncher().launchBrowser("http://www.google.com/", new LaunchListener() {

								@Override
								public void onLaunchSuccess(LaunchSession session) {
            						setRunningAppInfo(session);
								}

								@Override
								public void onLaunchFailed(ServiceCommandError error) {
								}
            				});
            			}
            			else {
            				getLauncher().launchBrowser(new LaunchListener() {

								@Override
								public void onLaunchSuccess(LaunchSession session) {
            						setRunningAppInfo(session);
								}

								@Override
								public void onLaunchFailed(ServiceCommandError error) {
								}
            				});
            			}
            		}
            	}
            }
        });

        toastButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getToastControl().showToast("Hello", null);
			}
		});

        deepLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	if ( deepLinkButton.isSelected() ) {
            		deepLinkButton.setSelected(false);
            		if (runningAppSession != null) {
            			runningAppSession.close(null);
            		}
            	}
            	else {
            		deepLinkButton.setSelected(true);
            		getLauncher().launchNetflix("70242311", new LaunchListener() {

						@Override
						public void onLaunchSuccess(LaunchSession session) {
    						setRunningAppInfo(session);
						}

						@Override
						public void onLaunchFailed(ServiceCommandError error) {
						}
    				});
            	}
            }
        });

        youtubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	if ( youtubeButton.isSelected() ) {
            		youtubeButton.setSelected(false);
            		if (runningAppSession != null) {
            			runningAppSession.close(null);
            		}
            	}
            	else {
            		youtubeButton.setSelected(true);
            		getLauncher().launchYouTube("IHQr0HCIN2w", new LaunchListener() {

						@Override
						public void onLaunchSuccess(LaunchSession session) {
    						setRunningAppInfo(session);
						}

						@Override
						public void onLaunchFailed(ServiceCommandError error) {
						}
    				});
            	}
            }
        });
        
		browserButton.setSelected(false);
		deepLinkButton.setSelected(false);
		youtubeButton.setSelected(false);

		if ( getTv().hasCapability(Launcher.kLauncherRunningAppSubscribe) ) {
			runningAppSubs = getLauncher().subscribeRunningAppId(new AppIdListener() {
				
				@Override
				public void onGetAppIdSuccess(String appId) {
					adapter.setRunningAppId(appId);
					runOnUiThread(new Runnable() {
						public void run() {
							adapter.notifyDataSetChanged();
						}
					});					
				}
				
				@Override
				public void onGetAppIdFailed(ServiceCommandError error) {
					
				}
			});
		}

		getLauncher().getApplicationList(new AppListListener() {
			
			@Override
			public void onGetAppListSuccess(List<AppInfo> appList) {
				setApplicationList(appList);
				
				for (int i = 0; i < appList.size(); i++) {
					final AppInfo app = appList.get(i);

					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							adapter.add(app);
						}
					});
				}
			}
			
			@Override
			public void onGetAppListFailed(ServiceCommandError error) {
			}
		});
 
		appListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				AppInfo appInfo = (AppInfo) applicationList.get(arg2);
				
				getLauncher().launchApplicationWithInfo(appInfo, null, new LaunchListener() {

					@Override
					public void onLaunchSuccess(LaunchSession session) {
						setRunningAppInfo(session);
					}

					@Override
					public void onLaunchFailed(ServiceCommandError error) {
					}
				});
			}
		});
        
        super.enableButtons();
        
		if ( !getTv().hasCapability(ToastControl.kToastControlShowToast) ) {
			toastButton.setEnabled(false);
		}
		
		if ( getTv().hasCapability(Launcher.kLauncherBrowser) ) {
			if ( getTv().hasCapability(Launcher.kLauncherBrowserParams) ) {
				browserButton.setText("Open Google");
			}
			else {
				browserButton.setText("Open Browser");
			}
		}
    }
    
    @Override
	public void disableButtons() {
    	if ( runningAppSubs != null ) 
    		runningAppSubs.unsubscribe();
    	adapter.clear();
    	
		super.disableButtons();
	}

	public void setRunningAppInfo(LaunchSession session) {
    	runningAppSession = session;
    }
    
    public void setApplicationList(List<AppInfo> appList) {
    	applicationList = appList;
    }
}
