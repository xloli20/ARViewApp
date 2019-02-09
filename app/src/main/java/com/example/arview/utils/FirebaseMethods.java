package com.example.arview.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.arview.R;
import com.example.arview.databaseClasses.chats;
import com.example.arview.databaseClasses.followers;
import com.example.arview.databaseClasses.following;
import com.example.arview.databaseClasses.profile;
import com.example.arview.databaseClasses.users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;

/**
 * Created by User on 6/26/2017.
 */

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;

    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    private String append = "";


    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

/*
    public void updateUsername(String username){
        Log.d(TAG, "updateUsername: upadting username to: " + username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }


    public void updateEmail(String email){
        Log.d(TAG, "updateEmail: upadting email to: " + email);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);

    } */


    public void registerNewEmail(final String email, String password, final String username){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the users. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in users can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed , Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                        else if(task.isSuccessful()){

                            sendVerificationEmail();

                            userID = mAuth.getCurrentUser().getUid();

                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }

                    }
                });
    }



    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(mContext, "Send verification email.", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onComplete: sendEmailVerification: " + userID);

                            }else{
                                Toast.makeText(mContext, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public String formatingUsername ( String username){
        String UserName = username.toLowerCase().replaceAll("\\s+|\\W","");
        return UserName;
    }

    public void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: Checking if  " + username + " already exists.");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        final FirebaseUser user = mAuth.getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("users").orderByChild("userName").equalTo(username + ".");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        //TODO : random num for append
                        //TODO : check again after append add

                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + singleSnapshot.getValue(users.class).getUserName());
                        append = String.valueOf(myRef.push().getKey().substring(3,10));
                        Log.d(TAG, "onDataChange: username already exists. Appending random string to name: " + append);
                    }
                }

                String mUsername = "";

                mUsername = formatingUsername(username) +"." + formatingUsername(append);

                //add new users classes to the database
                addNewUser(user.getEmail(), mUsername, username, "");

                Toast.makeText(mContext, "Signup successful. Sending verification email.", Toast.LENGTH_SHORT).show();

                mAuth.signOut();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void addNewUser(String email, String username , String name , String profile_photo){

        userID = mAuth.getCurrentUser().getUid();


        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date createdAt = new Date();
        String strcreatedAt = dateFormat.format(createdAt);

        users users = new users( userID,  username, name,  email, strcreatedAt, strcreatedAt , 1 );

        myRef.child("users")
                .child(userID)
                .setValue(users);

        profile profile = new profile( username, "" ,profile_photo, "" ,0,0,0);

        myRef.child("users")
                .child(userID)
                .child("profile")
                .setValue(profile);

        followers followers = new followers();

        myRef.child("users")
                .child(userID)
                .child("followers")
                .setValue(followers);

        following following = new following();

        myRef.child("users")
                .child(userID)
                .child("following")
                .setValue(following);

        chats chats= new chats();
        myRef.child("users")
                .child(userID)
                .child("chats")
                .setValue(chats);

    }



}












































