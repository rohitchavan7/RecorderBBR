package com.bbr.recorder.utils;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.bbr.recorder.fragments.AudiosFragment;
import com.bbr.recorder.fragments.RecorderFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    Context context;
    int totalTabs;
    public ViewPagerAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RecorderFragment recorderFragment = new RecorderFragment();
                return recorderFragment;
            case 1:
                AudiosFragment audiosFragment = new AudiosFragment();
                return audiosFragment;

            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}
