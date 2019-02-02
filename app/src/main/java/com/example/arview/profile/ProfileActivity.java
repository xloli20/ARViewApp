package com.example.arview.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.arview.post.PostDetailsFragment;
import com.example.arview.R;

public class ProfileActivity extends AppCompatActivity implements PostDetailsFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener {

    private FrameLayout profileContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileContainer = (FrameLayout) findViewById(R.id.profile_container);
        openFragment();

    }


    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
    public void OnFragmentInteractionListener(Uri uri){
        //you can leave it empty
    }
    public void openFragment() {
        ProfileFragment fragment = ProfileFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.profile_container, fragment);
        transaction.commit();
    }
}
