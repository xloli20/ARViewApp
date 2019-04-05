package com.example.arview.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arview.databaseClasses.post;
import com.example.arview.databaseClasses.profile;
import com.example.arview.databaseClasses.users;
import com.example.arview.login.SiginActivity;
import com.example.arview.post.PostDetailsFragment;
import com.example.arview.R;
import com.example.arview.post.PostRecyclerViewAdapter;
import com.example.arview.setting.SettingActivity;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class ProfileActivity extends AppCompatActivity implements PostDetailsFragment.OnFragmentInteractionListener,
                                                                    ProfileEditFragment.OnFragmentInteractionListener,
                                                                    ProfileFragment.OnFragmentInteractionListener{

    private static final String TAG = "ProfileActivity";

    //widget
    private Button profileButton;
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
    private String UserID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setUpProfileWidget();
        setupFirebaseAuth();


    }

     /*
    -------------------------------wedget on click-----------------------------------------
     */

     private void setUpProfileWidget(){
         mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
         profileMenu = (ImageView) findViewById(R.id.profileMenu);
         backArrow = (ImageView) findViewById(R.id.backArrow);
         profileButton = (Button) findViewById(R.id.follow);
         profilePhoto = (CircleImageView) findViewById(R.id.profile_photo);

         UserName = (TextView) findViewById(R.id.username);
         Name = (TextView) findViewById(R.id.name);
         profileDescription = (TextView) findViewById(R.id.profileDesc);
         NFollowers = (TextView) findViewById(R.id.FollowersCount);
         NPost = (TextView) findViewById(R.id.postCount);

         recyclerView = findViewById(R.id.postRecyclerView);

         mProgressBar.setVisibility(View.GONE);

         backArrow();
         setting();
         edit();

     }


    private void backArrow(){
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setting(){
        profileMenu.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, SettingActivity.class);
                i.setAction(Intent.ACTION_EDIT);
                startActivity(i);
            }
        });
    }

    private void edit(){
        profileButton.setText("Edit profile");
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileEditFragment();
            }
        });
    }

    private void postList() {

        UserID = mAuth.getUid();
        Log.e(TAG, "postList: UserID in." +  UserID );


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PostRecyclerViewAdapter(this, Plist , UserID );
        recyclerView.setAdapter(adapter);


        DatabaseReference ref = firebaseDatabase.getReference().child("profile").child(UserID).child("post");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String postID = dataSnapshot.getKey();
                String v = dataSnapshot.getValue(String.class);

                Log.e(TAG, "postList: postID." +  postID +" " + v );

                if(v.startsWith("true")){
                    //post is personal

                } else {

                    if (v.endsWith("true")) {
                        //post is public
                        DatabaseReference Pupost = firebaseDatabase.getReference().child("posts").child("public").child(postID);

                        Pupost.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                setPost(dataSnapshot);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    }
                    if (v.endsWith("false")) {
                        //post is private
                        DatabaseReference Pvpost = firebaseDatabase.getReference().child("posts").child("private").child(postID);
                        Pvpost.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                setPost(dataSnapshot);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
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


    private void setPost(DataSnapshot dataSnapshot){

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

        Collections.sort(Plist, new Comparator<post>() {
            public int compare(post o1, post o2) {
                return o2.getPostCreatedDate().compareTo(o1.getPostCreatedDate());
            }
        });

        adapter.notifyDataSetChanged();

        for (int i =0 ; i < Plist.size() ; i ++){
            if ( Plist.get(i).getPostId().equals(post.getPostId())){

                if ( Plist.get(i).getLikes() != post.getLikes()){
                    Plist.set(i , post);
                    Plist.remove(i+1);
                    adapter.notifyItemRangeChanged(i,Plist.size()-1);
                }
            }
        }

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

          if (p.getPost() != null)
              NPost.setText(String.valueOf(p.getPost().size()));
          else
              NPost.setText("0");

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
                    Intent intent = new Intent(ProfileActivity.this, SiginActivity.class);
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
                    setProfileWedgets(firebaseMethods.getProfile(dataSnapshot));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postList();


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


    public void openProfileEditFragment() {
        ProfileEditFragment fragment = ProfileEditFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.remove(fragment);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


}
