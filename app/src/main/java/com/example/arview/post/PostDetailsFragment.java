package com.example.arview.post;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.databaseClasses.post;
import com.example.arview.databaseClasses.profile;
import com.example.arview.login.SiginActivity;
import com.example.arview.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class PostDetailsFragment extends Fragment {
    private static final String TAG = "PostDetailsFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String UserID;
    private String PostID;


    private OnFragmentInteractionListener mListener;


    //widgets
    private ImageView backArrow, heart, location, clock ;
    private TextView userName, postName,vibilty, postDescription, likeCount, commentCount, limit;
    private CircleImageView profilePhoto;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    //var
    private boolean liked = false;


    public PostDetailsFragment() {
    }

    public static PostDetailsFragment newInstance(String UserID, String PostID) {
            PostDetailsFragment fragment = new PostDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, UserID);
        args.putString(ARG_PARAM2, PostID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            UserID = getArguments().getString(ARG_PARAM1);
            PostID = getArguments().getString(ARG_PARAM2);
        }

        Log.e(TAG, "onCreate: getArguments UserID." +  UserID +" PostID " + PostID);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_details, container, false);

        setUpPostDetailWidget(view);
        setupFirebaseAuth();

        return view;
    }

    public void getPostDetails(){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("profile").child(UserID).child("post").child(PostID);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String v = dataSnapshot.getValue(String.class);

                if(v.startsWith("true")){
                    //post is personal
                    DatabaseReference Prpost = firebaseDatabase.getReference().child("profile").child("personalPosts").child(PostID);
                    Prpost.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            setPost(dataSnapshot, "PERSONAL");
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else {

                    if (v.endsWith("true")) {
                        //post is public

                        DatabaseReference Pupost = FirebaseDatabase.getInstance().getReference().child("posts").child("public").child(PostID);

                        Pupost.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                setPost(dataSnapshot, "PUBLIC");
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                    if (v.endsWith("false")) {
                        //post is private
                        DatabaseReference Pvpost = firebaseDatabase.getReference().child("posts").child("private").child(PostID);
                        Pvpost.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                setPost(dataSnapshot, "PRIVATE");
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setPost(DataSnapshot dataSnapshot, String visibelty){

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

        postName.setText(post.getPostName());
        postDescription.setText(post.getPostDesc());
        vibilty.setText(visibelty);
        likeCount.setText(post.getLikes());
        commentCount.setText(post.getComments());
        limit.setText(post.getPostEndTime());

        if (dataSnapshot.child("likes").hasChild(mAuth.getUid())){
            liked = true;
            heart.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_red_heart));
        }

        if (visibelty.equals("PUBLIC")){
            vibilty.setBackground(getResources().getDrawable(R.drawable.green_rounded_border));
        }
        if (visibelty.equals("PERSONAL")){
            vibilty.setBackground(getResources().getDrawable(R.drawable.red_rounded_border));
        }
        if (visibelty.equals("PRIVATE")){
            vibilty.setBackground(getResources().getDrawable(R.drawable.yellow_rounded_border));
        }



        like(dataSnapshot);
        time(dataSnapshot, post);
    }

    private void like(final DataSnapshot dataSnapshot){
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(liked){
                    dataSnapshot.getRef().child("likes").child(mAuth.getUid()).removeValue();
                    heart.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_empty_heart));
                    liked = false;
                }else{
                    dataSnapshot.getRef().child("likes").child(mAuth.getUid()).setValue("true");
                    heart.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_red_heart));
                    liked = true;
                }
            }
        });
    }

    private void time(final DataSnapshot dataSnapshot, post p){

        if (p.getPostEndTime() == null ){
            // set date created
            clock.setVisibility(View.INVISIBLE);
            String timeDiff = getTimestampDifference(p.getPostCreatedDate());
            if(!timeDiff.equals("0")){
                limit.setText(timeDiff + " DAYS AGO");
            }else
                limit.setText("Today");

        }else if (p.getPostEndTime() != null ){
            //set date End
            clock.setVisibility(View.VISIBLE);
            String timeDiff = getTimestampDifference(p.getPostEndTime());


            if ( timeDiff.indexOf("-") == -1){
                Log.e(TAG, "getTimestampDifference: ----: "  );
                firebaseMethods.deletePost(p.getPostId(),p.getOwnerId(), p.isVisibilty(), p.isPersonal());
            }

            if(! timeDiff.equals("0") ){
                String d = timeDiff.substring(timeDiff.indexOf("-") + 1);
                limit.setText(d + " DAYS LEFT");
            }else {
                String HtimeDiff = getTimestampDifferenceH(p.getPostEndTime());
                if(! HtimeDiff.equals("0")){
                    String d = timeDiff.substring(timeDiff.indexOf("-") + 1);
                    limit.setText(d + "h LEFT");

                }else{
                    String MtimeDiff = getTimestampDifferenceM(p.getPostEndTime());
                    if(! MtimeDiff.equals("0") ){
                        String d = timeDiff.substring(timeDiff.indexOf("-") + 1);
                        limit.setText(d + "m LEFT");
                    }else{
                        String StimeDiff = getTimestampDifferenceS(p.getPostEndTime());
                        if(! StimeDiff.equals("0") ){
                            String d = timeDiff.substring(timeDiff.indexOf("-") + 1);
                            limit.setText(d + "s LEFT");
                        }else {
                            Log.e(TAG, "getTimestampDifference: ----: "  );

                            firebaseMethods.deletePost(p.getPostId(),p.getOwnerId(), p.isVisibilty(), p.isPersonal());

                        }
                    }

                }
            }

        }

    }

    private void location(){

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    private String getTimestampDifference(String Date){

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = Date;
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }

    private String getTimestampDifferenceH(String Date){

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = Date;
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifferenceH: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }

    private String getTimestampDifferenceM(String Date){

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = Date;
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60  )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifferenceM: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }
    private String getTimestampDifferenceS(String Date){

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = Date;
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000  )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifferenceM: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }




         /*
    -------------------------------wedget on click-----------------------------------------
    */

    private void setUpPostDetailWidget(View view){
        backArrow = (ImageView) view.findViewById(R.id.backArrow);
        profilePhoto = (CircleImageView)  view.findViewById(R.id.profile_photo);
        heart = (ImageView) view.findViewById(R.id.heart);
        location = (ImageView) view.findViewById(R.id.location);
        clock  = (ImageView) view.findViewById(R.id.clockCalnder);
        userName =  view.findViewById(R.id.UserName);
        postName =  view.findViewById(R.id.PostName);
        vibilty =  view.findViewById(R.id.visiblty);
        postDescription =  view.findViewById(R.id.postDescription);
        likeCount =  view.findViewById(R.id.likeNum);
        commentCount =  view.findViewById(R.id.comment);
        limit =  view.findViewById(R.id.limit);

        backArrow();
        setUpOwner();
        getPostDetails();

    }



    private void setUpOwner() {

        DatabaseReference owner = FirebaseDatabase.getInstance().getReference().child("profile").child(UserID);
        owner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Uri uri = Uri.parse(dataSnapshot.child("profilePhoto").getValue(String.class));
                Glide.with(profilePhoto.getContext())
                        .load(uri)
                        .into(profilePhoto);

                userName.setText(dataSnapshot.child("name").getValue(String.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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



      /*
    -------------------------------wedget on click-----------------------------------------
    */
      class CustomAdapter extends BaseAdapter {

          private ImageView proImg;
          private TextView Uname;
          private TextView comment;
          private TextView commentDate;

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
              view = getLayoutInflater().inflate(R.layout.layout_comment_list, null);

              proImg = (ImageView) view.findViewById(R.id.profile_photo);
              Uname = (TextView) view.findViewById(R.id.textView);
              comment = (TextView) view.findViewById(R.id.textView1);
              commentDate = (TextView) view.findViewById(R.id.textView2);

              return view;
          }
      }

      /*
    ------------------------------------ Firebase ---------------------------------------------
     */


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


      /*********************************************************************************/

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
