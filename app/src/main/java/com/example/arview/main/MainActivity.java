package com.example.arview.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.arview.R;
import com.example.arview.chat.ChatsFragment;
import com.example.arview.chat.InchatFragment;
import com.example.arview.login.SiginActivity;
import com.example.arview.profile.ProfileFragment;
import com.example.arview.utils.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener ,
                                                                CameraFragment.OnFragmentInteractionListener,
                                                                ChatsFragment.OnFragmentInteractionListener,
                                                                InchatFragment.OnFragmentInteractionListener,
                                                                ProfileFragment.OnFragmentInteractionListener {

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
                    Intent intent = new Intent(MainActivity.this, SiginActivity.class);
                    startActivity(intent);
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


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    // view pager for swap
    private void setupViewPager(){
        viewPager = (ViewPager) findViewById(R.id.pager);

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchFragment()); //index 0
        adapter.addFragment(new CameraFragment()); //index 1
        adapter.addFragment(new ChatsFragment()); //index 2

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);

    }


}

