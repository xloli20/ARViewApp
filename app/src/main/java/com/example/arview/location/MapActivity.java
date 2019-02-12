package com.example.arview.location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.arview.R;
import com.example.arview.profile.ProfileEditFragment;

public class MapActivity extends AppCompatActivity implements NearListFragment.OnFragmentInteractionListener{

    //wedgets
    private ImageView upArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        upArrow = (ImageView) findViewById(R.id.upArrow);
        upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNearListFragment();
                upArrow.setVisibility(View.INVISIBLE);
            }
        });

        //TODO : get value from fragment when close to set arrow visibile again

    }

    public void openNearListFragment() {
        NearListFragment fragment = NearListFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.remove(fragment);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
    public void OnFragmentInteractionListener(Uri uri){
        //you can leave it empty
    }
}
