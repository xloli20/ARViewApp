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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

public class FriendsFragment extends Fragment {

    private static final String TAG = "FriendsFragment";
    private Context context;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    //widgets
    private RecyclerView recyclerView;

    private FriendsPostRecyclerViewAdapter adapter;
    //var
    private ArrayList<post> Plist = new ArrayList<>();
    private ArrayList<String> Flist = new ArrayList<>();

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        setupFirebaseAuth();

        recyclerView = view.findViewById(R.id.postRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FriendsPostRecyclerViewAdapter(getContext(), Plist, mAuth.getUid());
        recyclerView.setAdapter(adapter);

        getUserFollowingPosts();

        return view;
    }

    /*
-------------------------------widget on click-----------------------------------------
*/
    private int count = 0;

    private void getUserFollowingPosts() {
        DatabaseReference userFollowingDB = FirebaseDatabase.getInstance().getReference().child("profile").child(Objects.requireNonNull(mAuth.getUid())).child("following");

        userFollowingDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

                String uid = dataSnapshot.getRef().getKey();
                if (uid != null && !Flist.contains(uid)) {
                    Flist.add(uid);

                    count++;
                    DatabaseReference ref = firebaseDatabase.getReference().child("profile").child(uid).child("post");

                    ref.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            final String postID = dataSnapshot.getKey();
                            String v = dataSnapshot.getValue(String.class);

                            Log.e(TAG, "postID " + postID);

                            assert v != null;
                            if (v.startsWith("true")) {
                                //post is personal
                            } else {
                                if (v.endsWith("true")) {
                                    //post is public
                                    DatabaseReference Pupost = firebaseDatabase.getReference().child("posts").child("public").child(postID);

                                    Pupost.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                setPost(dataSnapshot);
                                            }                                        }

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
                                            if (dataSnapshot.exists()){
                                                setPost(dataSnapshot);
                                            }
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
                    if (count >= Flist.size() - 1) {
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        Log.e(TAG, "getUserFollowing " + Flist);
    }

    private post setPost(DataSnapshot dataSnapshot) {
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

        Log.e(TAG, "post " + post.toString());

        Plist.add(post);

        Collections.sort(Plist, new Comparator<post>() {
            public int compare(post o1, post o2) {
                return o2.getPostCreatedDate().compareTo(o1.getPostCreatedDate());
            }
        });
        adapter.notifyDataSetChanged();

        for (int i = 0; i < Plist.size(); i++) {
            if (Plist.get(i).getPostId().equals(post.getPostId())) {

                if (!Plist.get(i).getLikes().equals(post.getLikes())) {
                    Plist.set(i, post);
                    Plist.remove(i + 1);
                    adapter.notifyItemRangeChanged(i, Plist.size() - 1);
                }
            }
        }
        return post;
    }
     /*
    -------------------------------wedget on click-----------------------------------------
     */

    /*
------------------------------------ Firebase ---------------------------------------------
 */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseMethods = new FirebaseMethods(getContext());

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
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
        Plist.clear();
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
