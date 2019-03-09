package com.example.arview.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.chat.ChatsFragment;
import com.example.arview.chat.InchatFragment;
import com.example.arview.databaseClasses.profile;
import com.example.arview.databaseClasses.userChat;
import com.example.arview.databaseClasses.userChatProfile;
import com.example.arview.friend.FriendsFragment;
import com.example.arview.login.SiginActivity;
import com.example.arview.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class UpdatedsFragment extends Fragment implements ChatsFragment.OnFragmentInteractionListener, FriendsFragment.OnFragmentInteractionListener {

    private static final String TAG = "UpdatedsFragment";


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    //wedgets
    private ImageView chats;
    private ImageView friends;

    private boolean frameState = true ;

    private OnFragmentInteractionListener mListener;

    public UpdatedsFragment() {
    }

    public static UpdatedsFragment newInstance() {
        UpdatedsFragment fragment = new UpdatedsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_updates, container, false);

        setupFirebaseAuth();

        chats = view.findViewById(R.id.chats);
        friends = view.findViewById(R.id.friends);

        if (frameState){
            openFriendsFragment();
        }else{
            openChatsFragment();
        }

        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatsFragment();
                frameState = false;
            }
        });

        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFriendsFragment();
                frameState = true;
            }
        });


        return view;
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
                    Intent intent = new Intent(getActivity(), SiginActivity.class);
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

    @Override
    public void onPause() {
        super.onPause();
    }


    /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void openChatsFragment() {
        ChatsFragment fragment = ChatsFragment.newInstance();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.remove(fragment);
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public void openFriendsFragment() {
        FriendsFragment fragment = FriendsFragment.newInstance();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.remove(fragment);
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    /*************************************************************************/
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
