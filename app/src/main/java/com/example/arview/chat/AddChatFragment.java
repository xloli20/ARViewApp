package com.example.arview.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.arview.R;
import com.example.arview.databaseClasses.addChat;
import com.example.arview.databaseClasses.following;
import com.example.arview.databaseClasses.userChat;
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
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddChatFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private OnFragmentInteractionListener mListener;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    //widgets
    private EditText mInput;
    private ImageView mSearch;
    private ImageView backArrow;

    private RecyclerView SuggRecyclerView, SearchRecyclerView;
    private LinearLayout suggLY, searchLY;

    private addChatAdapter suggAdapter;
    private addChatAdapter2 searchAdapter;

    //var
    private ArrayList<following> Suggested = new ArrayList<>();
    private ArrayList<addChat> results = new ArrayList<>();

    public AddChatFragment() {
    }

    public static AddChatFragment newInstance() {
        AddChatFragment fragment = new AddChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_chat, container, false);

        setupFirebaseAuth();
        setupWedgets(view);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                results.clear();
                listenForData();
            }
        });

        searchLY.setVisibility(View.INVISIBLE);
        SuggestedList();

        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    suggLY.setVisibility(View.INVISIBLE);
                    searchLY.setVisibility(View.VISIBLE);

                    results.clear();
                    listenForData();

                } else {
                    suggLY.setVisibility(View.VISIBLE);
                    searchLY.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        return view;
    }

    private void SuggestedList(){
        DatabaseReference R = firebaseDatabase.getReference().child("profile").child(Objects.requireNonNull(mAuth.getUid())).child("following");
        R.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String uid = dataSnapshot.getRef().getKey();

                if (!ChatsFragment.chatUserList.contains(uid)){

                    assert uid != null;
                    DatabaseReference R2 = firebaseDatabase.getReference().child("profile").child(uid);

                    R2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                            following f = new following();

                            f.setUid(uid);
                            assert map != null;
                            f.setUsername(Objects.requireNonNull(map.get("userName")).toString());
                            f.setProfilePhoto(Objects.requireNonNull(map.get("profilePhoto")).toString());
                            f.setName(Objects.requireNonNull(map.get("name")).toString());

                            Suggested.add(f);
                            suggAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
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
    private void listenForData() {
        DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("profile");
        Query query = usersDb.orderByChild("userName").startAt(mInput.getText().toString()).endAt(mInput.getText().toString() + "\uf8ff");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                String username = "";
                String name = "";
                String uid = dataSnapshot.getRef().getKey();
                String photo = "" ;

                if(dataSnapshot.child("userName").getValue() != null){
                    username = Objects.requireNonNull(dataSnapshot.child("userName").getValue()).toString();
                }
                if(dataSnapshot.child("name").getValue() != null){
                    name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                }
                if(dataSnapshot.child("profilePhoto").getValue() != null){
                    photo = Objects.requireNonNull(dataSnapshot.child("profilePhoto").getValue()).toString();
                }
                if(!username.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())){
                    final addChat obj = new addChat(username,name, uid, photo);

                DatabaseReference R = firebaseDatabase.getReference().child("userChat").child(mAuth.getUid()).child(uid);

                R.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            userChat users = dataSnapshot.getValue(userChat.class);
                            obj.setChatId(users.getChatId());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                results.add(obj);
                searchAdapter.notifyDataSetChanged();
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
    }
      /*
    -------------------------------widget on click-----------------------------------------
     */
      private void setupWedgets(View view){
          mSearch = view.findViewById(R.id.search);
          mInput = view.findViewById(R.id.input);
          backArrow = view.findViewById(R.id.backArrow);

          SuggRecyclerView = view.findViewById(R.id.sugrecyclerView);
          SearchRecyclerView = view.findViewById(R.id.searchrecyclerView);
          suggLY = view.findViewById(R.id.suggLayout);
          searchLY = view.findViewById(R.id.searchLayout);

          backarrow();

          LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
          SuggRecyclerView.setLayoutManager(layoutManager);
          suggAdapter = new addChatAdapter(getContext(),Suggested);
          SuggRecyclerView.setAdapter(suggAdapter);

          LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
          SearchRecyclerView.setLayoutManager(layoutManager1);
          searchAdapter = new addChatAdapter2(getContext(),results);
          SearchRecyclerView.setAdapter(searchAdapter);
      }

    private void backarrow(){
          backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closefragment();
            }
        });
    }

    private void closefragment() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        getActivity().getSupportFragmentManager().popBackStack();
    }
        /*
    -------------------------------widget on click-----------------------------------------
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
