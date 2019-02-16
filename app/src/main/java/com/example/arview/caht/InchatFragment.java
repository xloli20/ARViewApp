package com.example.arview.caht;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.databaseClasses.chatMessage;
import com.example.arview.databaseClasses.profile;
import com.example.arview.location.RecyclerViewAdapter;
import com.example.arview.login.SiginActivity;
import com.example.arview.profile.ProfileFragment;
import com.example.arview.utils.FirebaseMethods;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class InchatFragment extends Fragment implements ProfileFragment.OnFragmentInteractionListener {

    private static final String TAG = "InchatFragment";
    private Context context;

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_PHOTO_PICKER =  1;



    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String chatID;
    private String otherUserID;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;
    private DatabaseReference mMessagesDatabaseReference;


    //wedgets
    private ImageView backArrow, imagePicker, profPhoto;
    private EditText messageEditText;
    private TextView SEND, userName;
    private RecyclerView recyclerView ;


    //var
    private ArrayList<chatMessage> chatMessage = new ArrayList<>();


    private OnFragmentInteractionListener mListener;

    public InchatFragment() {
    }


    public static InchatFragment newInstance(String chatID, String otherUserID) {
        InchatFragment fragment = new InchatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, chatID);
        args.putString(ARG_PARAM2, otherUserID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chatID = getArguments().getString(ARG_PARAM1);
            otherUserID = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inchat, container, false);

        setupFirebaseAuth();
        setupWedgets(view);


        chatMessage =firebaseMethods.getAllChatMessageTest(chatID);
        initRecyclerView();

        return view;
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

    private void setupWedgets(View view){
        backArrow = (ImageView) view.findViewById(R.id.backArrow);
        imagePicker = (ImageView) view.findViewById(R.id.imagePicker);
        messageEditText = (EditText)view.findViewById(R.id.messageEditText);
        profPhoto = (ImageView) view.findViewById(R.id.profile_photo);
        userName = (TextView) view.findViewById(R.id.username);
        SEND = (TextView)view.findViewById(R.id.btnsendText);
        recyclerView = view.findViewById(R.id.chatMessageRecyclerView);


        backArrow();
        otherUser();
        Message();
        imagePicker();
        //setupMessageCustomListView();
    }

    private void backArrow(){
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closefragment();
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
    private void closefragment() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
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


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        ChatsRecyclerViewAdapter adapter = new ChatsRecyclerViewAdapter(getContext(), chatMessage, mAuth.getUid());
        recyclerView.setAdapter(adapter);

        // Notify recycler view insert one new data.
        adapter.notifyDataSetChanged();
        layoutManager.setStackFromEnd(true);


    }



     /*
    -------------------------------wedget on click-----------------------------------------
     */

     /*
    ------------------------------------ Firebase ---------------------------------------------
     */



     private void setinChatuserWedget(profile p){

         Uri uri = Uri.parse(p.getProfilePhoto());

         Glide.with(profPhoto.getContext())
                 .load(uri)
                 .into(profPhoto);

         userName.setText(p.getUserName());

     }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        firebaseMethods = new FirebaseMethods(getContext());
        mMessagesDatabaseReference = firebaseDatabase.getReference().child("Chats").child(chatID);



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
                    Intent intent = new Intent(getActivity(), SiginActivity.class);
                    startActivity(intent);
                }
                // ...
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                try {
                    setinChatuserWedget(firebaseMethods.getProfile(dataSnapshot, otherUserID));


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
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.remove(fragment);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }



    /**********************************************************************/

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
