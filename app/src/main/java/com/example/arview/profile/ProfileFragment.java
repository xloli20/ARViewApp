package com.example.arview.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.databaseClasses.post;
import com.example.arview.databaseClasses.profile;
import com.example.arview.login.SiginActivity;
import com.example.arview.post.PostDetailsFragment;
import com.example.arview.setting.SettingActivity;
import com.example.arview.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class ProfileFragment extends Fragment implements PostDetailsFragment.OnFragmentInteractionListener{
    private static final String TAG = "ProfileActivity";


    private static final String ARG_PARAM1 = "param1";

    private String userID;

    private OnFragmentInteractionListener mListener;


    //widget
    private Button mFollow;
    private ListView postlist;
    private TextView UserName, Name, profileDescription, NFollowers, NPost;
    private ImageView profileMenu , backArrow;
    private CircleImageView profilePhoto;
    private ProgressBar mProgressBar;

    private RecyclerView recyclerView;

    private PostRecyclerViewAdapter adapter;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    //var
    private ArrayList<post> Plist = new ArrayList<>() ;


    public ProfileFragment() {
    }


    public static ProfileFragment newInstance(String param1) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        setUpProfileWidget(view);

        setupFirebaseAuth();
        return view;
    }


     /*
    -------------------------------wedget on click-----------------------------------------
     */


    private void setUpProfileWidget(View view){
        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        profileMenu = (ImageView)  view.findViewById(R.id.profileMenu);
        backArrow = (ImageView) view.findViewById(R.id.backArrow);
        mFollow = (Button) view.findViewById(R.id.follow);
        profilePhoto = (CircleImageView)  view.findViewById(R.id.profile_photo);

        UserName = (TextView)  view.findViewById(R.id.username);
        Name = (TextView)  view.findViewById(R.id.name);
        profileDescription = (TextView)  view.findViewById(R.id.profileDesc);
        NFollowers = (TextView)  view.findViewById(R.id.FollowersCount);
        NPost = (TextView)  view.findViewById(R.id.postCount);

        recyclerView = view.findViewById(R.id.postRecyclerView);


        mProgressBar.setVisibility(View.GONE);

        backArrow();
        setting();
        follow();
        postlist();

    }


    private void backArrow(){
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closefragment();
            }
        });
    }
    private void closefragment() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
    private void setting(){
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SettingActivity.class);
                i.setAction(Intent.ACTION_EDIT);
                startActivity(i);
            }
        });
    }

    private void follow(){
        mFollow.setText("Follow");
        mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                if (mFollow.getText().equals("follow")) {
                    mFollow.setText("following");
                    FirebaseDatabase.getInstance().getReference().child("profile").child(userId).child("following").child(FirebaseAuth.getInstance().getUid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("profile").child(FirebaseAuth.getInstance().getUid()).child("followers").child(userId).setValue(true);

                } else {
                    mFollow.setText("follow");
                    FirebaseDatabase.getInstance().getReference().child("profile").child(userId).child("following").child(FirebaseAuth.getInstance().getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("profile").child(FirebaseAuth.getInstance().getUid()).child("followers").child(userId).removeValue();

                }

            }
        });
    } 


    private void postlist(){

        post p = new post("","","name","desc",new Date(),0,0,"","",true,false);

        Plist.add(p);
        Plist.add(p);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PostRecyclerViewAdapter(getContext(), Plist , "" );
        recyclerView.setAdapter(adapter);

    }

    /*
    -------------------------------wedget on click-----------------------------------------
    */


      /*
    ------------------------------------ Firebase ---------------------------------------------
     */


    private void setProfileWedgets(profile profile){
        profile p = profile;

        if (p.getProfilePhoto() != null){
            Uri uri = Uri.parse(p.getProfilePhoto());
            Glide.with(profilePhoto.getContext())
                    .load(uri)
                    .into(profilePhoto);
        }

        UserName.setText(p.getUserName());
        Name.setText(p.getName());
        profileDescription.setText(p.getProfileDescription());
        if (p.getFollowers() != null)
            NFollowers.setText(String.valueOf(p.getFollowers().size()));
        else
            NFollowers.setText("0");
        NPost.setText(String.valueOf(p.getPost()));

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
                try {
                    setProfileWedgets(firebaseMethods.getProfile(dataSnapshot , userID));
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


    public void openPostDetailsFragment() {
        PostDetailsFragment fragment = PostDetailsFragment.newInstance();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.remove(fragment);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


    /*****************************************************************************************/
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
