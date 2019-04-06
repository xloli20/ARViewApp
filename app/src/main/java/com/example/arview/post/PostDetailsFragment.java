package com.example.arview.post;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.databaseClasses.comment;
import com.example.arview.databaseClasses.post;
import com.example.arview.databaseClasses.profile;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import static com.example.arview.utils.CalTimeDiff.getTimestampDifference;
import static com.example.arview.utils.CalTimeDiff.getTimestampDifferenceH;
import static com.example.arview.utils.CalTimeDiff.getTimestampDifferenceM;
import static com.example.arview.utils.CalTimeDiff.getTimestampDifferenceS;


public class PostDetailsFragment extends Fragment {
    private static final String TAG = "PostDetailsFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";


    private String UserID;
    private String PostID;
    private String PostPath;


    private OnFragmentInteractionListener mListener;


    //widgets
    private ImageView backArrow, heart, location, clock, delete ;
    private TextView userName, postName,vibilty, postDescription, likeCount, commentCount, limit;
    private CircleImageView profilePhoto;

    private RecyclerView recyclerView;
    private SimpleCommentsRecyclerViewAdapter adapter;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    //var
    private boolean liked = false;
    private ArrayList<comment> Clist = new ArrayList<>() ;


    public PostDetailsFragment() {
    }

    public static PostDetailsFragment newInstance(String UserID, String PostID, String PostPath) {
            PostDetailsFragment fragment = new PostDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, UserID);
        args.putString(ARG_PARAM2, PostID);
        args.putString(ARG_PARAM3, PostPath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            UserID = getArguments().getString(ARG_PARAM1);
            PostID = getArguments().getString(ARG_PARAM2);
            PostPath = getArguments().getString(ARG_PARAM3);
        }

        Log.e(TAG, "onCreate: getArguments UserID." +  UserID +" PostID " + PostID + " PostPath" + PostPath);


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

        if(PostPath.startsWith("true")){
            //post is personal
            DatabaseReference Prpost = FirebaseDatabase.getInstance().getReference().child("profile").child(UserID).child("personalPosts").child(PostID);
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

            if (PostPath.endsWith("true")) {
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
            if (PostPath.endsWith("false")) {
                //post is private
                DatabaseReference Pvpost = FirebaseDatabase.getInstance().getReference().child("posts").child("private").child(PostID);
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
        commentCount.setText(post.getComments() + " Comments");
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
        time(post);

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

    private void time( post p){

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



    private void CommentList(){

        String ID = FirebaseAuth.getInstance().getUid();
        Log.e(TAG, "postList: UserID in." +  ID );

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SimpleCommentsRecyclerViewAdapter(getContext(), Clist , ID );
        recyclerView.setAdapter(adapter);


        if(PostPath.startsWith("true")) {
            //post is personal
            DatabaseReference Prpost = FirebaseDatabase.getInstance().getReference().child("profile").child(UserID).child("personalPosts").child(PostID).child("comments");
            Query query = Prpost.limitToLast(5);
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    setComment(dataSnapshot);
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

        }else{

            if (PostPath.endsWith("true")) {
                //post is public
                DatabaseReference Pupost = FirebaseDatabase.getInstance().getReference().child("posts").child("public").child(PostID).child("comments");
                Query query = Pupost.limitToLast(5);
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        setComment(dataSnapshot);
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
            if (PostPath.endsWith("false")) {
                //post is private
                DatabaseReference Pvpost = FirebaseDatabase.getInstance().getReference().child("posts").child("private").child(PostID).child("comments");
                Query query = Pvpost.limitToLast(5);
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        setComment(dataSnapshot);
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

        }


    }

    private comment setComment(DataSnapshot dataSnapshot){

        final comment comment = new comment();

        comment.setCommentID(dataSnapshot.getKey());
        comment.setUserID(dataSnapshot.child("userID").getValue(String.class));
        comment.setUserName(dataSnapshot.child("userName").getValue(String.class));
        comment.setComment(dataSnapshot.child("comment").getValue(String.class));
        comment.setCommentDate(dataSnapshot.child("commentDate").getValue(String.class));
        comment.setLikes(String.valueOf(dataSnapshot.child("likes").getChildrenCount()));

        comment.setPostID(PostID);
        comment.setPostPath(PostPath);

        Clist.add(comment);
        adapter.notifyItemInserted(Clist.size());

        return comment;

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
        delete =  view.findViewById(R.id.delete);
        recyclerView  =  view.findViewById(R.id.commentRecyclerView);

        backArrow();
        setUpOwner();
        getPostDetails();
        openComment();
        CommentList();
    }

    private void delete(){

        if (mAuth.getUid().equals(UserID)){
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                            .setTitle("Delete Post")
                            .setMessage("Are you sure you want to delete this Post?")

                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    String p = PostPath.substring(0,  PostPath.indexOf("e")+1);
                                    String v = PostPath.substring(p.length(),  PostPath.length());

                                    firebaseMethods.deletePost(PostID, UserID, Boolean.valueOf(v),  Boolean.valueOf(p));
                                    closefragment();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });

        }else
            delete.setVisibility(View.GONE);
    }

    private void openComment(){
        commentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostCommentsFragment fragment = PostCommentsFragment.newInstance(PostPath , PostID);
                FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                transaction.addToBackStack(null);
                transaction.remove(fragment);
                transaction.replace(R.id.fragment_container3, fragment);
                transaction.commit();
            }
        });
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
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(this).commit();
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


        delete();

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
