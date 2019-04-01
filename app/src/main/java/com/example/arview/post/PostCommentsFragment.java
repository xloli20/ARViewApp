package com.example.arview.post;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.databaseClasses.chatMessage;
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
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.arview.chat.InChatActivity.DEFAULT_MSG_LENGTH_LIMIT;
import static com.example.arview.utils.CalTimeDiff.getTimestampDifference;
import static com.example.arview.utils.CalTimeDiff.getTimestampDifferenceH;
import static com.example.arview.utils.CalTimeDiff.getTimestampDifferenceM;
import static com.example.arview.utils.CalTimeDiff.getTimestampDifferenceS;


public class PostCommentsFragment extends Fragment {
    private static final String TAG = "PostCommentsFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String Path;
    private String PostID;


    private OnFragmentInteractionListener mListener;


    //widgets
    private ImageView backArrow ;
    private TextView Add ;
    private CircleImageView profilePhoto;
    private EditText commentET;
    private RelativeLayout enterComment;
    private RecyclerView recyclerView;

    private CommentsRecyclerViewAdapter adapter;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    //var
    private ArrayList<comment> Clist = new ArrayList<>() ;


    public PostCommentsFragment() {
    }

    public static PostCommentsFragment newInstance(String Path, String PostID) {
            PostCommentsFragment fragment = new PostCommentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, Path);
        args.putString(ARG_PARAM2, PostID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Path = getArguments().getString(ARG_PARAM1);
            PostID = getArguments().getString(ARG_PARAM2);
        }

        Log.e(TAG, "onCreate: getArguments Path." +  Path +" PostID " + PostID);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_comments, container, false);

        setUpPostCommentsWidget(view);
        setupFirebaseAuth();

        return view;
    }

    public void setUpCommentsList(){

        if(Path.startsWith("true")){
            //post is personal
            DatabaseReference Prpost = FirebaseDatabase.getInstance().getReference().child("profile").child(mAuth.getUid()).child("personalPosts").child(PostID).child("comments");
            Prpost.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.e(TAG, "snapshot " + dataSnapshot.getRef());
                    setComment(dataSnapshot);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()){
                        String CommentID = dataSnapshot.getKey();

                        if (CommentID != null){

                            for (int i =0 ; i < Clist.size() ; i ++){
                                if ( Clist.get(i).getCommentID().equals(CommentID)){
                                    Clist.set(i , setComment(dataSnapshot));
                                    Clist.remove(Clist.size()-1);
                                    adapter.notifyItemRangeChanged(i,Clist.size()-1);

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

        } else {

            if (Path.endsWith("true")) {
                //post is public
                DatabaseReference Pupost = FirebaseDatabase.getInstance().getReference().child("posts").child("public").child(PostID).child("comments");
                Pupost.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        setComment(dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        if (dataSnapshot.exists()){
                            String CommentID = dataSnapshot.getKey();

                            if (CommentID != null){

                                for (int i =0 ; i < Clist.size() ; i ++){
                                    if ( Clist.get(i).getCommentID().equals(CommentID)){
                                        Clist.set(i , setComment(dataSnapshot));
                                        Clist.remove(Clist.size()-1);
                                        adapter.notifyItemRangeChanged(i,Clist.size()-1);

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
            if (Path.endsWith("false")) {
                //post is private
                DatabaseReference Pvpost = FirebaseDatabase.getInstance().getReference().child("posts").child("private").child(PostID).child("comments");
                Pvpost.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Log.e(TAG, "snapshot " + dataSnapshot.getRef());
                        setComment(dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()){
                            String CommentID = dataSnapshot.getKey();

                            if (CommentID != null){

                                for (int i =0 ; i < Clist.size() ; i ++){
                                    if ( Clist.get(i).getCommentID().equals(CommentID)){
                                        Clist.set(i , setComment(dataSnapshot));
                                        Clist.remove(Clist.size()-1);
                                        adapter.notifyItemRangeChanged(i,Clist.size()-1);

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
        comment.setPostPath(Path);

        Clist.add(comment);
        adapter.notifyItemInserted(Clist.size());

        return comment;

    }



         /*
    -------------------------------wedget on click-----------------------------------------
    */

    private void setUpPostCommentsWidget(View view){
        backArrow = (ImageView) view.findViewById(R.id.backArrow);
        profilePhoto = (CircleImageView)  view.findViewById(R.id.profile_photo);
        Add =  view.findViewById(R.id.btnAdd);
        commentET =  view.findViewById(R.id.commentEditText);
        enterComment =  view.findViewById(R.id.enterComment);
        recyclerView = view.findViewById(R.id.commentRecyclerView);

        backArrow();
        enterCommet();

        String ID = FirebaseAuth.getInstance().getUid();
        Log.e(TAG, "postList: UserID in." +  ID );

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommentsRecyclerViewAdapter(getActivity(), Clist , ID );
        recyclerView.setAdapter(adapter);
        setUpCommentsList();

    }

    private void enterCommet(){

        commentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    Add();
                    Add.setTextColor(getResources().getColor(R.color.skyblue));
                    Add.setBackground(getResources().getDrawable(R.drawable.sun_rounded));
                } else {
                    Add.setTextColor(getResources().getColor(R.color.grey));
                    Add.setBackground(getResources().getDrawable(R.drawable.white_rounded));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        commentET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

    }


    private void Add(){
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
                sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
                String date = sdf.format(new Date());

                comment c = new comment(mAuth.getUid(),userName, commentET.getText().toString(), date );
                firebaseMethods.addComment(PostID, Path, c);
                // Clear input box
                commentET.setText("");
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

      /*
    ------------------------------------ Firebase ---------------------------------------------
     */


      private String userName;
    private void setProfileWedgets(profile profile){
        profile p = profile;

        if (p.getProfilePhoto() != null){
            Uri uri = Uri.parse(p.getProfilePhoto());
            Glide.with(profilePhoto.getContext())
                    .load(uri)
                    .into(profilePhoto);
        }

        userName = p.getUserName();

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
                    setProfileWedgets(firebaseMethods.getProfile(dataSnapshot));
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
