package com.example.arview.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.arview.R;
import com.example.arview.databaseClasses.chatMessage;
import com.example.arview.databaseClasses.followers;
import com.example.arview.databaseClasses.following;
import com.example.arview.databaseClasses.profile;
import com.example.arview.databaseClasses.userChat;
import com.example.arview.databaseClasses.users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private UploadTask uploadTask;
    private String userID;
    private ChildEventListener mChildEventListener;
    private ChildEventListener RChildEventListener;
    private DatabaseReference mMessagesDatabaseReference;



    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    private String append = "";

    private String defaultProfilePhoto = "https://firebasestorage.googleapis.com/v0/b/arview-b5eb3.appspot.com/o/profile_photo.png?alt=media&token=edd70c89-7133-49fb-928e-4a3c579bbcec";



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
                addNewUser(user.getEmail(), mUsername, username);

                Toast.makeText(mContext, "Signup successful. Sending verification email.", Toast.LENGTH_SHORT).show();

                mAuth.signOut();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addNewUser(String email, String username , String name){

        userID = mAuth.getCurrentUser().getUid();


        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date createdAt = new Date();
        String StrCreatedAt = dateFormat.format(createdAt);

        users users = new users(username,  email, StrCreatedAt, StrCreatedAt , 0 );

        myRef.child("users")
                .child(userID)
                .setValue(users);



        profile profile = new profile( username, name, "" ,defaultProfilePhoto, "" ,0,0,0);

        myRef.child("profile")
                .child(userID)
                .setValue(profile);

        followers followers = new followers();

        myRef.child("followers")
                .child(userID)
                .setValue(followers);

        following following = new following();

        myRef.child("following")
                .child(userID)
                .setValue(following);



    }


    /*************************************************************************************/

    public Uri getProfilePhoto(DataSnapshot dataSnapshot){

        Uri uri = null;

        for(DataSnapshot ds: dataSnapshot.getChildren()){

            if(ds.getKey().equals("profile")) {

                try {

                uri =Uri.parse (( ds.child(userID)
                        .getValue(profile.class)
                        .getProfilePhoto()));

                } catch (NullPointerException e) {
                    Log.e(TAG, "getprofile: NullPointerException: " + e.getMessage());
                }

            }


        }
        return uri;
    }


    public profile getProfile(DataSnapshot dataSnapshot) throws IOException {
        Log.d(TAG, "getProfile: retrieving profile from firebase.");

        final profile profile  = new profile();

        for(DataSnapshot ds: dataSnapshot.getChildren()){

            // profile node
            if(ds.getKey().equals("profile")) {
                Log.d(TAG, "getProfile: user profile node datasnapshot: " + ds);

                try {

                    profile.setUserName(
                            ds.child(userID)
                                    .getValue(profile.class)
                                    .getUserName()
                    );
                    profile.setName(
                            ds.child(userID)
                                    .getValue(profile.class)
                                    .getName()
                    );
                    profile.setUserLocation(
                            ds.child(userID)
                                    .getValue(profile.class)
                                    .getUserLocation()
                    );
                    profile.setProfilePhoto(
                            ds.child(userID)
                                    .getValue(profile.class)
                                    .getProfilePhoto()
                    );
                    profile.setProfileDescription(
                            ds.child(userID)
                                    .getValue(profile.class)
                                    .getProfileDescription()
                    );

                    profile.setFollowers(
                            ds.child(userID)
                                    .getValue(profile.class)
                                    .getFollowers()
                    );

                    profile.setFollowing(
                            ds.child(userID)
                                    .getValue(profile.class)
                                    .getFollowing()
                    );
                    profile.setPost(
                            ds.child(userID)
                                    .getValue(profile.class)
                                    .getPost()
                    );


                } catch (NullPointerException e) {
                    Log.e(TAG, "getprofile: NullPointerException: " + e.getMessage());
                }

            }


        }

        Log.d(TAG, "geProfile: retrieved profile information: " + profile.toString());

        return profile;

    }

    public profile getProfile(DataSnapshot dataSnapshot , String userID) throws IOException {
        Log.d(TAG, "getProfile: retrieving profile from firebase.");

        final profile profile  = new profile();

        for(DataSnapshot ds: dataSnapshot.getChildren()){

            // profile node
            if(ds.getKey().equals("profile")) {
                Log.d(TAG, "getProfile: user profile node datasnapshot: " + ds);

                try {

                    profile.setUserName(
                            ds.child(userID)
                                    .getValue(profile.class)
                                    .getUserName()
                    );
                    profile.setName(
                            ds.child(userID)
                                    .getValue(profile.class)
                                    .getName()
                    );
                    profile.setUserLocation(
                            ds.child(userID)
                                    .getValue(profile.class)
                                    .getUserLocation()
                    );
                    profile.setProfilePhoto(
                            ds.child(userID)
                                    .getValue(profile.class)
                                    .getProfilePhoto()
                    );
                    profile.setProfileDescription(
                            ds.child(userID)
                                    .getValue(profile.class)
                                    .getProfileDescription()
                    );

                    profile.setFollowers(
                            ds.child(userID)
                                    .getValue(profile.class)
                                    .getFollowers()
                    );

                    profile.setFollowing(
                            ds.child(userID)
                                    .getValue(profile.class)
                                    .getFollowing()
                    );
                    profile.setPost(
                            ds.child(userID)
                                    .getValue(profile.class)
                                    .getPost()
                    );


                } catch (NullPointerException e) {
                    Log.e(TAG, "getprofile: NullPointerException: " + e.getMessage());
                }

            }


        }

        Log.d(TAG, "geProfile: retrieved profile information: " + profile.toString());

        return profile;

    }


    public profile getProfile(String uID){

        final profile[] profile = {new profile()};

        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("profile").child(uID);


        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    profile[0] = dataSnapshot.getValue(profile.class);

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }

        return profile[0];
    }


    public List<String> getAllchatsgggUserID(DataSnapshot dataSnapshot){
        List<String> chatsUserId = new ArrayList<>();


        chatsUserId.add("LFvmo0NrcATUG4Y0O1IXNJrdw4a2");

        Log.d(TAG, "getAllchatsUsersID: snapshot key: " + chatsUserId);
        return chatsUserId;
    }


    public List<userChat> getAllchatsUser(){
        final List<userChat> chatsUser = new ArrayList<>();

        Log.d(TAG, "getAllchatsUser: chatUserList" + chatsUser.toString());

        DatabaseReference R = mFirebaseDatabase.getReference().child("userChat").child(userID);


        if (RChildEventListener == null) {
                RChildEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        userChat users = dataSnapshot.getValue(userChat.class);
                        Log.d(TAG, "getAllchatsUser: chatUserList.1" + users.toString());
                        chatsUser.add(users);

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {


                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };

            R.addChildEventListener(RChildEventListener);
            Log.d(TAG, "getAllchatsUser: chatUserList * " + chatsUser.toString());

        }

        return chatsUser;
    }




    /*

    PERFECT

     */

    public void deleteProfilePhoto(){

        // delete from Storage
        StorageReference delRef =  mStorageReference.child(userID).child("profilePhoto").child("profile_photo.png" );

        delRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });

        //delete download url from profile
        myRef.child("profile")
                .child(userID)
                .child("profilePhoto")
                .setValue(defaultProfilePhoto);

    }

    public void uploadPhoto( Uri selectedImageUri){

        // Get a reference to store file
        final StorageReference upRef =  mStorageReference.child(userID).child("profilePhoto").child("profile_photo.png" );

        // Upload file to Firebase Storage
        uploadTask = upRef.putFile(selectedImageUri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

        // When the image has successfully uploaded, we get its download URL
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return upRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String downloadURL = String.valueOf(downloadUri);
                    // set download uri on profile
                    myRef.child("profile")
                            .child(userID)
                            .child("profilePhoto")
                            .setValue(downloadURL);

                } else {
                    // Handle failures
                    // ...
                }
            }
        });

    }

    public void uploadChatPhoto(Uri selectedImageUri, final String chatId){

        // Get a reference to store file
        final StorageReference upRef =  mStorageReference.child(userID).child("Chats").child(chatId).child(selectedImageUri.getLastPathSegment());

        // Upload file to Firebase Storage
        uploadTask = upRef.putFile(selectedImageUri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

        // When the image has successfully uploaded, we get its download URL
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return upRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String downloadURL = String.valueOf(downloadUri);
                    // set download uri on profile

                    chatMessage chatPhoto = new chatMessage(null, userID, downloadURL);

                    sendMessage(chatId, chatPhoto);


                } else {
                    // Handle failures
                    // ...
                }
            }
        });

    }

    public void editProfile(String name , String desc){
        myRef.child("profile")
                .child(userID)
                .child("name")
                .setValue(name);

        myRef.child("profile")
                .child(userID)
                .child("profileDescription")
                .setValue(desc);

    }




    public void addChat(final String OthetUserID){

        String chatID = String.valueOf(myRef.push().getKey());


        final userChat chatUser = new userChat(OthetUserID, chatID);
        final userChat chatUser1 = new userChat(userID , chatID);

        final DatabaseReference chat =myRef.child("userChat").child(userID);

        chat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild(OthetUserID)) {

                    chat.child(OthetUserID)
                            .setValue(chatUser);

                    myRef.child("userChat")
                            .child(OthetUserID)
                            .child(userID)
                            .setValue(chatUser1);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void sendMessage(String chatId, chatMessage chatMessge){

        String messageID = String.valueOf(myRef.push().getKey());

        myRef.child("Chats")
                .child(chatId)
                .child(messageID)
                .setValue(chatMessge);


    }


    public  ArrayList<chatMessage> getAllChatMessageTest(String ChatID){

        final ArrayList<chatMessage> chatM = new ArrayList<>();

        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("Chats").child(ChatID);


        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    chatMessage chatTExt = dataSnapshot.getValue(chatMessage.class);
                    chatM.add(chatTExt);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }

        return chatM;
    }

}












































