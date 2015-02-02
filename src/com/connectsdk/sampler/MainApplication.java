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

package com.connectsdk.sampler;

import android.app.Application;

import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.DIALService;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        DIALService.registerApp("Levak");
        DiscoveryManager.init(getApplicationContext());

        super.onCreate();
    }
}
