package com.example.arview.caht;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.databaseClasses.profile;
import com.example.arview.databaseClasses.userChat;
import com.example.arview.databaseClasses.userChatProfile;
import com.example.arview.location.RecyclerViewAdapter;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment implements InchatFragment.OnFragmentInteractionListener{

    private static final String TAG = "ChatsFragment";

    //wedgets
    private EditText chatsSearch;
    private ImageView addChat;
    private RecyclerView recyclerView ;



    private ArrayList<userChatProfile> chatUserprofileList = new ArrayList<>() ;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    private OnFragmentInteractionListener mListener;

    public ChatsFragment() {
    }

    public static ChatsFragment newInstance() {
        ChatsFragment fragment = new ChatsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);


        setupFirebaseAuth();
        chatUserList(view);




        return view;
    }



     /*
    -------------------------------wedget on click-----------------------------------------
     */

     private void chatUserList(View view){
         chatsSearch = (EditText) view.findViewById(R.id.chatsSearch);
         addChat = (ImageView) view.findViewById(R.id.add_chat);
         recyclerView = view.findViewById(R.id.chatMessageRecyclerView);

         addChat();
         initRecyclerView();
     }

     private void addChat(){
         addChat.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //TODO: open search fragment
                 //            get new user id then open inchatfragment
                 firebaseMethods.addChat("LFvmo0NrcATUG4Y0O1IXNJrdw4a2");
                 firebaseMethods.addChat("ax2qqsPUjYZ2U0uwCOu8VgHTm5d2");
             }
         });
     }



    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        final RecyclerViewAdapter1 adapter = new RecyclerViewAdapter1(getContext(),chatUserprofileList );
        recyclerView.setAdapter(adapter);


        DatabaseReference R = firebaseDatabase.getReference().child("userChat").child(mAuth.getUid());


        R.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                userChat users = dataSnapshot.getValue(userChat.class);

                Log.d(TAG, "getAllchatsUser: chatUserList.1" + users.toString());

                final userChatProfile UCP = new userChatProfile();
                UCP.setUserChat(users);

                DatabaseReference R2 = firebaseDatabase.getReference().child("profile").child(users.getOtherUserId());

                R2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        profile p = dataSnapshot.getValue(profile.class);

                        Log.d(TAG, "getAllchatsUser: chatUserList.2" + p.toString());

                        UCP.setProfile(p);

                        chatUserprofileList.add(UCP);

                        Log.d(TAG, "getAllchatsUser: chatUserList.3" + UCP.toString());

                        Log.d(TAG, "getAllchatsUser: chatUserList.4" + chatUserprofileList.toString());


                        int last = chatUserprofileList.size()-1;
                        Log.d(TAG, "getAllchatsUser: chatUserList.size" + last);

                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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


    public class RecyclerViewAdapter1 extends RecyclerView.Adapter<RecyclerViewAdapter1.ViewHolder> {

        private static final String TAG = "RecyclerViewAdapter";

        //vars
        private ArrayList<userChatProfile> UserprofileList ;

        private Context mContext;

        public RecyclerViewAdapter1(Context context, ArrayList<userChatProfile> List) {
            UserprofileList = List;
            mContext = context;

            Log.d(TAG, "RecyclerViewAdapter1: UserprofileList.*" + UserprofileList.toString());

        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chats_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {


            Log.d(TAG, "RecyclerViewAdapter1: UserprofileList.1" + UserprofileList.toString());


            profile p = UserprofileList.get(position).getProfile();

            Log.d(TAG, "RecyclerViewAdapter1: profile.2" + p.toString());

            Uri uri = Uri.parse(p.getProfilePhoto());

            Glide.with(mContext)
                    .load(uri)
                    .into(holder.proImg);

            holder.name.setText(p.getName());
            holder.Uname.setText(p.getUserName());



            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openInChatFragment(UserprofileList.get(position).getUserChat().getChatId(), UserprofileList.get(position).getUserChat().getOtherUserId());
                }
            });

        }


        @Override
        public int getItemCount() {
            return UserprofileList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            public ImageView proImg, chatMenu;
            public TextView Uname;
            public TextView name;
            public TextView lastmessage;
            public RelativeLayout relativeLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                proImg = itemView.findViewById(R.id.profile_photo);
                chatMenu = itemView.findViewById(R.id.chatsMenu);
                name = itemView.findViewById(R.id.Name);
                Uname = itemView.findViewById(R.id.userName);
                lastmessage = itemView.findViewById(R.id.textView);
                relativeLayout =  itemView.findViewById(R.id.chatsrellayout);


            }
        }
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
        firebaseMethods = new FirebaseMethods(getContext());

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void openInChatFragment(String chatID, String otherUserID) {
        InchatFragment fragment = InchatFragment.newInstance(chatID,otherUserID);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.remove(fragment);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


    /*************************************************************************/
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
