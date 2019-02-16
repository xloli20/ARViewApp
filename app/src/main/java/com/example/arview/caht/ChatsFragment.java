package com.example.arview.caht;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.databaseClasses.profile;
import com.example.arview.databaseClasses.userChat;
import com.example.arview.login.SiginActivity;
import com.example.arview.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment implements InchatFragment.OnFragmentInteractionListener{

    private static final String TAG = "ChatsFragment";

    //wedgets
    private ListView chatsList;
    private EditText chatsSearch;
    private ImageView addChat;


    private List<userChat> chatUserList = new ArrayList<>() ;

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
         chatsList = (ListView) view.findViewById(R.id.chatsListview);
         chatsSearch = (EditText) view.findViewById(R.id.chatsSearch);
         addChat = (ImageView) view.findViewById(R.id.add_chat);

         addChat();
         setupListView();
     }

     private void addChat(){
         addChat.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //TODO: open search fragment to get new user id then open inchatfragment
                 firebaseMethods.addChat("LFvmo0NrcATUG4Y0O1IXNJrdw4a2");
                 firebaseMethods.addChat("ax2qqsPUjYZ2U0uwCOu8VgHTm5d2");
             }
         });
     }



    private void setupListView(){

        chatUserList = firebaseMethods.getAllchatsUser();
        Toast.makeText(getContext(), chatUserList.toString(), Toast.LENGTH_SHORT).show();

        ChatsFragment.CustomAdapter CA = new ChatsFragment.CustomAdapter();
        CA.notifyDataSetChanged();
        chatsList.setAdapter(CA);

        chatsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            openInChatFragment(chatUserList.get(position).getChatId(), chatUserList.get(position).getOtherUserId());
        }
        });
    }

    class CustomAdapter extends BaseAdapter {

        private ImageView proImg, chatMenu;
        private TextView Uname;
        private TextView name;
        private TextView lastmessage;


        @Override
        public int getCount() {
            return chatUserList.size();

        }

        @Override
        public Object getItem(int i) {
            return chatUserList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.layout_chats_list, null);

            proImg = (ImageView) view.findViewById(R.id.profile_photo);
            chatMenu = (ImageView) view.findViewById(R.id.chatsMenu);
            name = (TextView) view.findViewById(R.id.Name);
            Uname = (TextView) view.findViewById(R.id.userName);
            lastmessage = (TextView) view.findViewById(R.id.textView);


            profile p = firebaseMethods.getProfile(chatUserList.get(i).getOtherUserId());

            Uri uri = Uri.parse(p.getProfilePhoto());

            Glide.with(proImg.getContext())
                    .load(uri)
                    .into(proImg);

            name.setText(p.getName());
            Uname.setText(p.getUserName());


            return view;
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
        myRef = firebaseDatabase.getReference();
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

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


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
