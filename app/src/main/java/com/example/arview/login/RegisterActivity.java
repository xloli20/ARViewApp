package com.example.arview.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arview.R;
import com.example.arview.databaseClasses.user;
import com.example.arview.main.MainActivity;
import com.example.arview.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private Context mContext;
    private String email, username, password, mConPass;
    private EditText mEmail, mPassword, mUsername, mConPassword;
    private Button btnRegister;
    private ProgressBar mProgressBar;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods firebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private String append = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initWidgets();
        firebaseMethods = new FirebaseMethods(mContext);

        setupFirebaseAuth();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });

        TextView linkSignip = (TextView) findViewById(R.id.link_login);
        linkSignip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to register screen");
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }

    private void initWidgets(){
        Log.d(TAG, "initWidgets: Initializing Widgets.");
        mEmail = (EditText) findViewById(R.id.input_email);
        mUsername = (EditText) findViewById(R.id.input_username);
        btnRegister = (Button) findViewById(R.id.btn_register);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mPassword = (EditText) findViewById(R.id.input_password);
        mConPassword  = (EditText) findViewById(R.id.input_confirmpassword);
        mContext = RegisterActivity.this;
        mProgressBar.setVisibility(View.GONE);

    }



    private boolean checkInputs(String email, String username, String password, String mConPass){
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        if(email.equals("") || username.equals("") || password.equals("") || mConPass.equals("")){
            Toast.makeText(mContext, "All fields must be filled out.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.equals(mConPass)){
            Toast.makeText(mContext, "Confirm Password don't equal", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String formatingUsername ( String username){
        String UserName = username.toLowerCase().replaceAll("\\s+|\\W","");

        return UserName;
    }



    /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    //TODO: send username all in lowercase


    private void init(){
        email = mEmail.getText().toString();
        username = mUsername.getText().toString();
        password = mPassword.getText().toString();
        mConPass = mConPassword.getText().toString();

        if(checkInputs(email, username, password , mConPass)){
            mProgressBar.setVisibility(View.VISIBLE);

            checkIfUsernameExists(username);

        }
    }

    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: Checking if  " + username + " already exists.");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("user").orderByChild("userName").equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        //TODO : random num for append
                        //TODO : check again after append add

                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + singleSnapshot.getValue(user.class).getUserName());
                        append = String.valueOf(myRef.push().getKey().substring(3,10));
                        Log.d(TAG, "onDataChange: username already exists. Appending random string to name: " + append);
                    }
                }

                String mUsername = "";

                mUsername = formatingUsername(username) +"." + formatingUsername(append);

                firebaseMethods.registerNewEmail(email, password, mUsername, formatingUsername(username));

                Toast.makeText(mContext, "Signup successful. Sending verification email.", Toast.LENGTH_SHORT).show();

                mAuth.signOut();
                mProgressBar.setVisibility(View.GONE);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


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


}
