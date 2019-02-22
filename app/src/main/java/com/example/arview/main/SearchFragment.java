package com.example.arview.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.arview.databaseClasses.following;
import com.example.arview.login.SiginActivity;
import com.example.arview.profile.ProfileActivity;
import com.example.arview.R;
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

import java.io.IOException;
import java.util.ArrayList;


public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private OnFragmentInteractionListener mListener;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    //wedgets
    public ImageView profile;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    EditText mInput;

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        setupFirebaseAuth();
        setupSearchWedgets(view);

        ImageView mSearch = view.findViewById(R.id.search);
        mInput = view.findViewById(R.id.input);



        mRecyclerView = view.findViewById(R.id.recyclerView);

        RecyclerView();

        mAdapter = new FollowAdapater(getDataSet(),getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
                listenForData();
            }
        });

        return view;
    }

    private void listenForData() {
        DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = usersDb.orderByChild("email").startAt(mInput.getText().toString()).endAt(mInput.getText().toString() + "\uf8ff");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                String email = "";
                String uid = dataSnapshot.getRef().getKey();
                if(dataSnapshot.child("email").getValue() != null){
                    email = dataSnapshot.child("email").getValue().toString();
                }
                if(!email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                    following obj = new following(email, uid);
                    results.add(obj);
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

      /*
    -------------------------------wedget on click-----------------------------------------
     */

      private void setupSearchWedgets(View view){
          profile = view.findViewById(R.id.profile);
          mRecyclerView = view.findViewById(R.id.recyclerView);


          profilePhoto();

      }

      private void profilePhoto(){

          profile.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent i = new Intent(getActivity(), ProfileActivity.class);
                  i.setAction(Intent.ACTION_EDIT);
                  startActivity(i);
              }
          });

      }

      private void RecyclerView(){
          mRecyclerView.setNestedScrollingEnabled(false);
          mRecyclerView.setHasFixedSize(true);
          mLayoutManager = new LinearLayoutManager(getActivity());
          mRecyclerView.setLayoutManager(mLayoutManager);

      }

    private void clear() {
        int size = this.results.size();
        this.results.clear();
        mAdapter.notifyDataSetChanged();
    }



    private ArrayList<following> results = new ArrayList<>();
    private ArrayList<following> getDataSet() {
        listenForData();
        return results;
    }




        /*
    -------------------------------wedget on click-----------------------------------------
     */



     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

     private void setProfilePhoto(Uri uri){
         if (uri != null){
             Glide.with(profile.getContext())
                     .load(uri)
                     .into(profile);
         }
     }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        firebaseMethods = new FirebaseMethods(getActivity());


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
                //retrieve profile from the database
                setProfilePhoto(firebaseMethods.getProfilePhoto(dataSnapshot));
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




    /********************************************************************************/

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
