package com.example.arview.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.databaseClasses.chatMessage;
import com.example.arview.databaseClasses.profile;
import com.example.arview.login.SiginActivity;
import com.example.arview.profile.ProfileFragment;
import com.example.arview.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class InChatActivity extends AppCompatActivity implements ProfileFragment.OnFragmentInteractionListener {

    private static final String TAG = "InChatActivity";
    private Context context;

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_PHOTO_PICKER =  1;


    private String chatID ;
    private String otherUserID ;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;


    //wedgets
    private ImageView backArrow, imagePicker, profPhoto;
    private EditText messageEditText;
    private TextView SEND, userName;
    private RecyclerView recyclerView ;

    private ChatsRecyclerViewAdapter adapter;


    //var
    private ArrayList<chatMessage> chatMessageList = new ArrayList<>();
    public Uri uriOtherUser ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_chat);

        chatID = getIntent().getStringExtra("ChatID");
        otherUserID = getIntent().getStringExtra("OtherUserId");

        setupFirebaseAuth();
        setupWedgets();
    }





    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER || resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            firebaseMethods.uploadChatPhoto(selectedImageUri, chatID);

        }
    }

    /*
    -------------------------------wedget on click-----------------------------------------
     */

    private void setupWedgets(){
        backArrow = findViewById(R.id.backArrow);
        imagePicker = findViewById(R.id.imagePicker);
        messageEditText = findViewById(R.id.messageEditText);
        profPhoto = findViewById(R.id.profile_photo);
        userName = findViewById(R.id.username);
        SEND = findViewById(R.id.btnsendText);
        recyclerView = findViewById(R.id.chatMessageRecyclerView);

        backArrow();
        otherUser();

        if (chatID != null){
            Message();
            initRecyclerView();
            imagePicker();
        }
        else if (chatID == null){
            startChating();
        }
    }

    private void backArrow(){
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void otherUser(){
        profPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileFragment();
            }
        });
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileFragment();
            }
        });

    }

    //imagePicker
    private void imagePicker(){
        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

    }

    private void startChating(){
        messageEditText.setHint("Say Hi !");

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    FirstSend();
                    SEND.setTextColor(getResources().getColor(R.color.skyblue));
                } else {
                    SEND.setTextColor(getResources().getColor(R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
    }

    private void FirstSend(){
        SEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chatID = firebaseMethods.addChat(otherUserID);

                chatMessage chatText = new chatMessage(messageEditText.getText().toString(), mAuth.getUid(), null);
                firebaseMethods.sendMessage(chatID, chatText);

                Message();
                initRecyclerView();
                imagePicker();

                messageEditText.setHint("Message...");
                // Clear input box
                messageEditText.setText("");
            }
        });

    }

    // Enable Send  when there's text to send
    private void Message(){

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    SEND();
                    SEND.setTextColor(getResources().getColor(R.color.skyblue));
                } else {
                    SEND.setTextColor(getResources().getColor(R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

    }

    // Send button sends a message and clears the EditText
    private void SEND(){
        SEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chatMessage chatText = new chatMessage(messageEditText.getText().toString(), mAuth.getUid(), null);
                firebaseMethods.sendMessage(chatID, chatText);

                // Clear input box
                messageEditText.setText("");
            }
        });

    }


    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview");



        DatabaseReference R = firebaseDatabase.getReference().child("Chats").child(chatID);

        R.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                chatMessage CM =  dataSnapshot.getValue(chatMessage.class);

                chatMessageList.add(CM);

                Log.d(TAG, "initRecyclerView: chatMessageList" + chatMessageList);

                //adapter.notifyItemInserted(chatMessageList.size()-1);
                adapter.notifyDataSetChanged();

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
        });

    }


     /*
    -------------------------------wedget on click-----------------------------------------
     */

     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    private void initPhoto(profile p){
        uriOtherUser = Uri.parse(p.getProfilePhoto());

        Glide.with(profPhoto.getContext())
                .load(uriOtherUser)
                .into(profPhoto);

        userName.setText(p.getUserName());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatsRecyclerViewAdapter(this, chatMessageList, mAuth.getUid(), uriOtherUser);
        recyclerView.setAdapter(adapter);
        layoutManager.setStackFromEnd(true);

    }


    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        firebaseMethods = new FirebaseMethods(this);


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
                    Intent intent = new Intent(InChatActivity.this, SiginActivity.class);
                    startActivity(intent);
                }
                // ...
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //retrieve profile from the database
                try {
                    initPhoto(firebaseMethods.getProfile(dataSnapshot, otherUserID));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
    }
    public void OnFragmentInteractionListener(Uri uri){
    }

    public void openProfileFragment() {
        ProfileFragment fragment = ProfileFragment.newInstance(otherUserID);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.remove(fragment);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }







}
