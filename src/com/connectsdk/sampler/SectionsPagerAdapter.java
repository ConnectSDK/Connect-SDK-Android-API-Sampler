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

import android.content.Context;
import android.content.res.TypedArray;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.connectsdk.sampler.fragments.AppsFragment;
import com.connectsdk.sampler.fragments.BaseFragment;
import com.connectsdk.sampler.fragments.KeyControlFragment;
import com.connectsdk.sampler.fragments.MediaPlayerFragment;
import com.connectsdk.sampler.fragments.SystemFragment;
import com.connectsdk.sampler.fragments.TVFragment;
import com.connectsdk.sampler.fragments.WebAppFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private int[] mItems;
    private String[] mTitles;
    private FragmentManager mFragmentManager;
    private Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mFragmentManager = fm;

        TypedArray items = context.getResources().obtainTypedArray(R.array.tab_icons);
        mTitles = context.getResources().getStringArray(R.array.tab_titles);

        mItems = new int[items.length()];

        for (int i = 0; i < items.length(); i++) {
            mItems[i] = items.getResourceId(i, -1);
        }

        items.recycle();
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment newFragment;

        switch (position)
        {
            case 1:
                newFragment = new WebAppFragment(mContext);
                break;

            case 2:
                newFragment = new KeyControlFragment(mContext);
                break;

            case 3:
                newFragment = new AppsFragment(mContext);
                break;

            case 4:
                newFragment = new TVFragment(mContext);
                break;

            case 5:
                newFragment = new SystemFragment(mContext);
                break;

            case 0:
            default:
                newFragment = new MediaPlayerFragment(mContext);
        }

        return newFragment;
    }

    public BaseFragment getFragment(int position) {
        return (BaseFragment) mFragmentManager.findFragmentByTag("android:switcher:" + R.id.pager + ":" + position);
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    public int getIcon(int position) {
        return mItems[position];
    }

    public String getTitle(int position) {
        return mTitles[position];
    }
}