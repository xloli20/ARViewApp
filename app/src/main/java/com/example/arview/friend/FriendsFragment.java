package com.example.arview.friend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arview.R;
import com.example.arview.databaseClasses.post;
import com.example.arview.login.SiginActivity;
import com.example.arview.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FriendsFragment extends Fragment {

    private static final String TAG = "FriendsFragment";
    Context context;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    //wedgets
    private RecyclerView recyclerView;

    private FriendsPostRecyclerViewAdapter adapter;
   //var
    private ArrayList<post> Plist = new ArrayList<>() ;
    private ArrayList<String> Flist = new ArrayList<>() ;


    private OnFragmentInteractionListener mListener;

    public FriendsFragment() {
    }

    @SuppressLint("ValidFragment")
    public FriendsFragment(Context context) {
        this.context = context;
    }

    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        setupFirebaseAuth();

        recyclerView = view.findViewById(R.id.postRecyclerView);
        postList();

        return view;
    }

         /*
    -------------------------------wedget on click-----------------------------------------
     */

    private void postList() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FriendsPostRecyclerViewAdapter(getContext(), Plist , mAuth.getUid() );
        recyclerView.setAdapter(adapter);

        DatabaseReference Pupost = FirebaseDatabase.getInstance().getReference().child("posts").child("public");
        DatabaseReference Pvpost = FirebaseDatabase.getInstance().getReference().child("posts").child("private");

        Flist.addAll(FollowingIDs.listFollowing);

        Pupost.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                setPost(dataSnapshot);

                Query query = dataSnapshot.getRef().orderByChild("ownerId").equalTo("muxiRaCd6jchpaajZSaoa2TkyKw2");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.e(TAG, "datasnapshot " + dataSnapshot.getRef().toString());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()){
                    String PostID = dataSnapshot.getKey();

                    if (PostID != null){

                        for (int i =0 ; i < Plist.size() ; i ++){
                            if ( Plist.get(i).getPostId().equals(PostID)){
                                Plist.set(i , setPost(dataSnapshot));
                                Plist.remove(Plist.size()-1);
                                adapter.notifyItemRangeChanged(i,Plist.size()-1);

                            }

                        }
                    }
                }
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

    private post setPost(DataSnapshot dataSnapshot){

        final post post = new post();

        post.setPostId(dataSnapshot.getKey());
        post.setOwnerId(dataSnapshot.child("ownerId").getValue(String.class));
        post.setPostName(dataSnapshot.child("postName").getValue(String.class));
        post.setPostDesc(dataSnapshot.child("postDesc").getValue(String.class));
        post.setPostCreatedDate(dataSnapshot.child("postCreatedDate").getValue(String.class));
        post.setLikes(String.valueOf(dataSnapshot.child("likes").getChildrenCount()));
        post.setComments(String.valueOf(dataSnapshot.child("comments").getChildrenCount()));
        post.setPostEndTime(dataSnapshot.child("postEndTime").getValue(String.class));
        post.setVisibilty(dataSnapshot.child("visibilty").getValue(Boolean.class));
        post.setPersonal(dataSnapshot.child("personal").getValue(Boolean.class));


        Plist.add(post);
        adapter.notifyItemInserted(Plist.size());

        return post;
    }

     /*
    -------------------------------wedget on click-----------------------------------------
     */


        /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseMethods = new FirebaseMethods(getContext());

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

    /***************************************************************/

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
