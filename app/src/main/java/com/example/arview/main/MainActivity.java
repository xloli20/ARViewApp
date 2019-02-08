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
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.arview.R;
import com.example.arview.login.LoginActivity;
import com.example.arview.login.SiginActivity;
import com.example.arview.utils.SectionsPagerAdapter;
import com.example.arview.utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener , CameraFragment.OnFragmentInteractionListener, ChatFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    //widgets
    private ViewPager viewPager;
    private PagerAdapter mPagerAdapter;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFirebaseAuth();
        initImageLoder();

        setupViewPager();

    }

    /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if users is logged in.");

        if(user == null){
            Intent intent = new Intent(this, SiginActivity.class);
            intent.setAction(Intent.ACTION_EDIT);
            startActivity(intent);
        }
    }


    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged( FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the users is logged in
                checkCurrentUser(user);

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



    /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    private void initImageLoder(){
        UniversalImageLoader universalImageLoader =new UniversalImageLoader(this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }


    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    // add search camera chat
    private void setupViewPager(){
        viewPager = (ViewPager) findViewById(R.id.pager);

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchFragment()); //index 0
        adapter.addFragment(new CameraFragment()); //index 1
        adapter.addFragment(new ChatFragment()); //index 2

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);

    }


}

