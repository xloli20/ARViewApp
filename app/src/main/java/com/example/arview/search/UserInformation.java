package com.example.arview.search;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;

public class UserInformation {
    static ArrayList<String> listFollowing = new ArrayList<>();

    public void startFetching(){
        listFollowing.clear();
        getUserFollowing();
    }

    public void getUserFollowing() {
        DatabaseReference userFollowingDB = FirebaseDatabase.getInstance().getReference().child("profile").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("following");
        userFollowingDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    String uid = dataSnapshot.getRef().getKey();
                    if (uid != null && !listFollowing.contains(uid)){
                        listFollowing.add(uid);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String uid = dataSnapshot.getRef().getKey();
                    if (uid != null){
                        listFollowing.remove(uid);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
