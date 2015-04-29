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
import android.widget.Button;
import android.widget.ListView;

import com.connectsdk.core.AppInfo;
import com.connectsdk.sampler.R;
import com.connectsdk.sampler.util.TestResponseObject;
import com.connectsdk.sampler.widget.AppAdapter;
import com.connectsdk.service.capability.Launcher;
import com.connectsdk.service.capability.Launcher.AppInfoListener;
import com.connectsdk.service.capability.Launcher.AppLaunchListener;
import com.connectsdk.service.capability.Launcher.AppListListener;
import com.connectsdk.service.capability.ToastControl;
import com.connectsdk.service.capability.listeners.ResponseListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.ServiceSubscription;
import com.connectsdk.service.sessions.LaunchSession;

public class AppsFragment extends BaseFragment {
    //  public Button smartWorldButton;
    public Button browserButton;
    public Button myAppButton;
    public Button netflixButton;
    public Button appStoreButton;
    public Button youtubeButton;
    public Button toastButton;

    public ListView appListView;
    public AppAdapter adapter;
    LaunchSession runningAppSession;
    LaunchSession appStoreSession;
    LaunchSession myAppSession;
    public TestResponseObject testResponse;

    ServiceSubscription<AppInfoListener> runningAppSubs;

    public AppsFragment() {
    	testResponse = new TestResponseObject();
    	
    };

    public AppsFragment(Context context)
    {
        super(context);
        testResponse = new TestResponseObject();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_apps, container, false);

        browserButton = (Button) rootView.findViewById(R.id.browserButton);
        myAppButton = (Button) rootView.findViewById(R.id.myApp); 
        netflixButton = (Button) rootView.findViewById(R.id.deepLinkButton);
        appStoreButton = (Button) rootView.findViewById(R.id.appStoreButton);
        youtubeButton = (Button) rootView.findViewById(R.id.youtubeButton);
        toastButton = (Button) rootView.findViewById(R.id.toastButton);

        appListView = (ListView) rootView.findViewById(R.id.appListView);
        adapter = new AppAdapter(getContext(), R.layout.app_item);
        appListView.setAdapter(adapter);

        buttons = new Button[] {
                browserButton, 
                netflixButton, 
                youtubeButton, 
                toastButton, 
                myAppButton, 
                appStoreButton
        };

        return rootView;
    }

    @Override
    public void enableButtons()
    {
        super.enableButtons();

        if (getTv().hasCapability(Launcher.Browser) 
                || getTv().hasCapability(Launcher.Browser_Params))
        {
            browserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (browserButton.isSelected()) {
                        browserButton.setSelected(false);
                        if (runningAppSession != null) {
                            runningAppSession.close(null);
                        }
                    }
                    else {
                        browserButton.setSelected(true);

                        getLauncher().launchBrowser("http://connectsdk.com/", new Launcher.AppLaunchListener() {

                            public void onSuccess(LaunchSession session) {
                                setRunningAppInfo(session);
                                testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.Launched_Browser);
                            }

                            public void onError(ServiceCommandError error) {
                            }
                        });
                    }
                }
            });
        }
        else {
            disableButton(browserButton);
        }

        if (getTv().hasCapability(ToastControl.Show_Toast)) {
            toastButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    getToastControl().showToast("Yeah, toast!", getToastIconData(), "png", null);
                    testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.Show_Toast);
                }
            });
        }
        else {
            disableButton(toastButton);
        }

        if (getTv().hasCapability(Launcher.Netflix) 
                || getTv().hasCapability(Launcher.Netflix_Params)) 
        {
            netflixButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (netflixButton.isSelected()) {
                        netflixButton.setSelected(false);
                        if (runningAppSession != null) {
                            runningAppSession.close(null);
                        }
                    }
                    else {
                        netflixButton.setSelected(true);
                        getLauncher().launchNetflix("70217913", new Launcher.AppLaunchListener() {

                            @Override
                            public void onSuccess(LaunchSession session) {
                                setRunningAppInfo(session);
                                testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.Launched_Netflix);
                            }

                            @Override
                            public void onError(ServiceCommandError error) {
                            }
                        });
                    }
                }
            });
        }
        else {
            disableButton(netflixButton);
        }

        if (getTv().hasCapability(Launcher.YouTube) ||
                getTv().hasCapability(Launcher.YouTube_Params)) {
            youtubeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (youtubeButton.isSelected()) {
                        youtubeButton.setSelected(false);
                        if (runningAppSession != null) {
                            runningAppSession.close(null);
                        }
                    }
                    else {
                        youtubeButton.setSelected(true);
                        getLauncher().launchYouTube("eRsGyueVLvQ", new Launcher.AppLaunchListener() {

                            @Override
                            public void onSuccess(LaunchSession session) {
                                setRunningAppInfo(session);
                                testResponse =  new TestResponseObject(true, TestResponseObject.SuccessCode, TestResponseObject.Launched_Youtube);
                            }

                            @Override
                            public void onError(ServiceCommandError error) {
                            }
                        });
                    }
                }
            });
        }
        else {
            disableButton(youtubeButton);
        }

        browserButton.setSelected(false);
        netflixButton.setSelected(false);
        youtubeButton.setSelected(false);

        if (getTv().hasCapability(Launcher.RunningApp_Subscribe)) {
            runningAppSubs = getLauncher().subscribeRunningApp(new AppInfoListener() {

                @Override
                public void onSuccess(AppInfo appInfo) {
                    adapter.setRunningAppId(appInfo.getId());
                    adapter.notifyDataSetChanged();

                    int position = adapter.getPosition(appInfo);
                    appListView.setSelection(position);
                }

                @Override
                public void onError(ServiceCommandError error) {

                }
            });
        }

        if (getTv().hasCapability(Launcher.Application_List)) {
            getLauncher().getAppList(new AppListListener() {

                @Override
                public void onSuccess(List<AppInfo> appList) {
                    adapter.clear();
                    for (int i = 0; i < appList.size(); i++) {
                        final AppInfo app = appList.get(i);

                        adapter.add(app);
                    }

                    adapter.sort();
                }

                @Override public void onError(ServiceCommandError error) { }
            });
        }

        appListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (runningAppSession != null) {
                    runningAppSession.close(null);
                }
                AppInfo appInfo = (AppInfo) arg0.getItemAtPosition(arg2);

                getLauncher().launchAppWithInfo(appInfo, null, new Launcher.AppLaunchListener() {

                    @Override
                    public void onSuccess(LaunchSession session) {
                        setRunningAppInfo(session);
                    }

                    @Override
                    public void onError(ServiceCommandError error) {
                    }
                });
            }
        });

        if (getTv().hasCapability(Launcher.Browser)) {
            if (getTv().hasCapability(Launcher.Browser_Params)) {
                browserButton.setText("Open Google");
            }
            else {
                browserButton.setText("Open Browser");
            }
        }

        myAppButton.setEnabled(getTv().hasCapability("Launcher.Levak"));
        myAppButton.setOnClickListener(myAppLaunch);

        appStoreButton.setEnabled(getTv().hasCapability(Launcher.AppStore_Params));
        appStoreButton.setOnClickListener(launchAppStore);
    }

    public View.OnClickListener myAppLaunch = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (myAppSession != null) {
                myAppSession.close(null);

                myAppSession = null;
                myAppButton.setSelected(false);
            } else {
                getLauncher().launchApp("Levak", new AppLaunchListener() {

                    @Override
                    public void onError(ServiceCommandError error) {
                        Log.d("LG", "My app failed: " + error);
                    }

                    @Override
                    public void onSuccess(LaunchSession object) {
                        myAppSession = object;
                        myAppButton.setSelected(true);
                    }
                });
            }
        }
    };

    public View.OnClickListener launchAppStore = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (appStoreSession != null) {
                appStoreSession.close(new ResponseListener<Object>() {

                    @Override
                    public void onError(ServiceCommandError error) {
                        Log.d("LG", "App Store close error: " + error);
                    }

                    @Override
                    public void onSuccess(Object object) {
                        Log.d("LG", "AppStore close success");
                    }
                });

                appStoreSession = null;
                appStoreButton.setSelected(false);
            } else {
                String appId = null;

                if (getTv().getServiceByName("Netcast TV") != null)
                    appId = "125071";
                else if (getTv().getServiceByName("webOS TV") != null)
                    appId = "redbox";
                else if (getTv().getServiceByName("Roku") != null)
                    appId = "13535";

                getLauncher().launchAppStore(appId, new AppLaunchListener() {

                    @Override
                    public void onError(ServiceCommandError error) {
                        Log.d("LG", "App Store failed: " + error);
                    }

                    @Override
                    public void onSuccess(LaunchSession object) {
                        Log.d("LG", "App Store launched!");

                        appStoreSession = object;
                        appStoreButton.setSelected(true);
                    }
                });
            }
        }
    };

    @Override
    public void disableButtons() {
        if (runningAppSubs != null) 
            runningAppSubs.unsubscribe();
        adapter.clear();

        super.disableButtons();
    }

    public void setRunningAppInfo(LaunchSession session) {
        runningAppSession = session;
    }

    protected String getToastIconData() {
        return mContext.getString(R.string.toast_icon_data);
    }
}
