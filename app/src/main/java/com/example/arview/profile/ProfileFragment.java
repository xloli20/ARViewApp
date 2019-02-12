package com.example.arview.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arview.R;
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


public class ProfileFragment extends Fragment implements PostDetailsFragment.OnFragmentInteractionListener{
    private static final String TAG = "ProfileActivity";


    private static final String ARG_PARAM1 = "param1";

    private String userID;

    private OnFragmentInteractionListener mListener;


    //widget
    private Button profileButton;
    private ListView postlist;
    private TextView UserName, Name, profileDescription, NFollowers, NPost;
    private ImageView profileMenu , backArrow;
    private CircleImageView profilePhoto;
    private ProgressBar mProgressBar;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

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
        profileButton =(Button)  view.findViewById(R.id.profileButton);
        postlist = (ListView)  view.findViewById(R.id.postList);
        profilePhoto = (CircleImageView)  view.findViewById(R.id.profile_photo);

        UserName = (TextView)  view.findViewById(R.id.username);
        Name = (TextView)  view.findViewById(R.id.name);
        profileDescription = (TextView)  view.findViewById(R.id.profileDesc);
        NFollowers = (TextView)  view.findViewById(R.id.FollowersCount);
        NPost = (TextView)  view.findViewById(R.id.postCount);


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
        profileButton.setText("Follow");
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add follow

            }
        });
    }


    private void postlist(){
        ProfileFragment.CustomAdapter CA = new ProfileFragment.CustomAdapter();
        postlist.setAdapter(CA);
        postlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openPostDetailsFragment();
            }
        });
    }

    /*
    -------------------------------wedget on click-----------------------------------------
    */


    class CustomAdapter extends BaseAdapter {
        public ImageView location;
        public ImageView limit;
        public ImageView like;
        public ImageView comment;

        public TextView UName;
        public TextView desc;
        public TextView Nlike;
        public TextView Ncomment;
        public TextView limitTime;


        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.layout_post_list, null);

            location = (ImageView) view.findViewById(R.id.location);
            limit = (ImageView) view.findViewById(R.id.imageButton1);
            like = (ImageView) view.findViewById(R.id.imageButton2);
            comment = (ImageView) view.findViewById(R.id.imageButton3);

            UName = (TextView) view.findViewById(R.id.textView);
            desc = (TextView) view.findViewById(R.id.textView1);
            Nlike = (TextView) view.findViewById(R.id.textView2);
            Ncomment = (TextView) view.findViewById(R.id.textView3);
            limitTime = (TextView) view.findViewById(R.id.textView4);

            UName.setText("users name blala");


            return view;
        }
    }



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
        NFollowers.setText(String.valueOf(p.getFollowers()));
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
