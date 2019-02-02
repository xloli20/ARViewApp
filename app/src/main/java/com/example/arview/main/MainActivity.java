package com.example.arview.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.arview.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener , CameraFragment.OnFragmentInteractionListener, ChatFragment.OnFragmentInteractionListener {

    private ViewPager viewPager;
    private PagerAdapter mPagerAdapter;
    sectionsPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViewPager();

    }

    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    // add search camera chat
    private void setupViewPager(){
        viewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new sectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(1);

    }

}

