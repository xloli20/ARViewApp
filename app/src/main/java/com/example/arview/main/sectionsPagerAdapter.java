package com.example.arview.main;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


// class that store fragment for main

public class sectionsPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "sectionsPagerAdapter";
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public sectionsPagerAdapter (FragmentManager fm) {
        super (fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return SearchFragment.newInstance();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return CameraFragment.newInstance();
            case 2: // Fragment # 1 - This will show SecondFragment
                return ChatFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }


}
