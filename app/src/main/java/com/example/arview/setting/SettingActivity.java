package com.example.arview.setting;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.arview.R;
import com.example.arview.login.SiginActivity;
import com.example.arview.profile.ProfileEditFragment;
import com.example.arview.utils.FirebaseMethods;
import com.example.arview.utils.SectionsStatePagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity implements ProfileEditFragment.OnFragmentInteractionListener,
                                                                SignOutFragment.OnFragmentInteractionListener,
                                                                PersonalPostFragment.OnFragmentInteractionListener ,
                                                                FollowingFragment.OnFragmentInteractionListener{

    private static final String TAG = "AccountSettingsActivity";

    public SectionsStatePagerAdapter pagerAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;

    //wedgets
    private EditText PN , email, oldPass, newPass, conPass;
    private Button PNSave, emailSave, PassSave;
    private ImageView backArrow;



    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods firebaseMethods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        firebaseMethods = new FirebaseMethods(this);

        setupFirebaseAuth();
        setupSettingsList();
        backarrow();


    }

    private void backarrow(){
        backArrow = (ImageView) findViewById(R.id.backArrow);

        //setup the backarrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to 'ProfileActivity'");
                finish();
            }
        });

    }


    private void setupSettingsList(){
        ListView listView = findViewById(R.id.lvAccountSettings);

        ArrayList<String> options = new ArrayList<>();
        options.add("Edit Profile"); // 0
        options.add("Phone Number"); // 1
        options.add("Email"); // 2
        options.add("Password"); // 3
        options.add("My following"); // 4
        options.add("My Personal Posts"); // 5
        options.add("Notification"); // 6
        options.add("Share Location"); // 7
        options.add("Support"); // 8
        options.add("Sign Out"); // 9

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: navigating to fragment#: " + position);
                openSettingFragment(position);
            }
        });

    }


    private void openSettingFragment(int i){

        switch (i){
            case 0:
                ProfileEditFragment fragment = ProfileEditFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.addToBackStack(null);
                transaction.remove(fragment);
                transaction.replace(R.id.fragment_container0, fragment);
                transaction.commit();

            break;
            case 1:
                PhoneNumberDialog();
                break;
            case 2:
                EmailDialog();
                break;
            case 3:
                PasswordDialog();
                break;
            case 4:
                FollowingFragment fragment1 = FollowingFragment.newInstance();
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                FragmentTransaction transaction1 = fragmentManager1.beginTransaction();
                transaction1.addToBackStack(null);
                transaction1.replace(R.id.fragment_container0, fragment1);
                transaction1.commit();
                break;
            case 5:
                PersonalPostFragment fragment2 = PersonalPostFragment.newInstance();
                FragmentManager fragmentManager2 = getSupportFragmentManager();
                FragmentTransaction transaction2 = fragmentManager2.beginTransaction();
                transaction2.addToBackStack(null);
                transaction2.replace(R.id.fragment_container0, fragment2);
                transaction2.commit();
                break;
            case 6:
                NotificationDialog();
                break;
            case 7:
                LocationDialog();
                break;
            case 8:

                break;
            case 9:
                SignOutFragment fragment7 = SignOutFragment.newInstance();
                FragmentManager fragmentManager7 = getSupportFragmentManager();
                FragmentTransaction transaction7 = fragmentManager7.beginTransaction();
                //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                transaction7.addToBackStack(null);
                transaction7.remove(fragment7);
                transaction7.replace(R.id.fragment_container0, fragment7);
                transaction7.commit();

                break;



        }

    }

    private void PhoneNumberDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_phone_number, null);

        PN = (EditText) mView.findViewById(R.id.input_PN);
        PNSave = (Button) mView.findViewById(R.id.btn_PN);

        PNSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseMethods.setPhoneNumber(PN.getText().toString());
                Toast.makeText(SettingActivity.this, "save.", Toast.LENGTH_SHORT).show();

            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }

    private void EmailDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_email, null);

        email = (EditText) mView.findViewById(R.id.input_email);
        emailSave = (Button) mView.findViewById(R.id.btn_email);

        emailSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "until now you can't change email.", Toast.LENGTH_SHORT).show();


            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void PasswordDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_password, null);

        oldPass = (EditText) mView.findViewById(R.id.input_oldP);
        newPass = (EditText) mView.findViewById(R.id.input_newP);
        conPass = (EditText) mView.findViewById(R.id.input_confirm);
        PassSave = (Button) mView.findViewById(R.id.btn_pass);

        PassSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (newPass.getText().toString().equals(conPass.getText().toString())){
                    firebaseMethods.updatePassword(oldPass.getText().toString(), newPass.getText().toString());
                }else{

                    Toast.makeText(SettingActivity.this, "not Match.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void NotificationDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_notification, null);


        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void LocationDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_location, null);


        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged( FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(SettingActivity.this, SiginActivity.class);
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

    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
    public void OnFragmentInteractionListener(Uri uri){
        //you can leave it empty
    }
}
