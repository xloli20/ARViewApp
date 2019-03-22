package com.example.arview.setting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.chat.InChatActivity;
import com.example.arview.databaseClasses.following;
import com.example.arview.databaseClasses.profile;
import com.example.arview.databaseClasses.userChat;
import com.example.arview.databaseClasses.userChatProfile;
import com.example.arview.login.SiginActivity;
import com.example.arview.profile.ProfileFragment;
import com.example.arview.search.UserInformation;
import com.example.arview.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;




public class FollowingFragment extends Fragment implements ProfileFragment.OnFragmentInteractionListener{

    private static final String TAG = "ChatsFragment";

    //wedgets
    private RecyclerView recyclerView ;
    private ImageView backArrow;


    private RecyclerViewAdapter1 adapter;
    private ArrayList<following> followingList = new ArrayList<>() ;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    private OnFragmentInteractionListener mListener;

    public FollowingFragment() {
    }

    public static FollowingFragment newInstance() {
        FollowingFragment fragment = new FollowingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);


        setupFirebaseAuth();
        backarrow(view);

        recyclerView = view.findViewById(R.id.followingRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter1(getContext(),followingList );
        recyclerView.setAdapter(adapter);

        initRecyclerView();


        return view;
    }



     /*
    -------------------------------wedget on click-----------------------------------------
     */

    private void backarrow(View view){
        backArrow = view.findViewById(R.id.backArrow);

        //setup the backarrow
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



    private void initRecyclerView(){

        DatabaseReference R = firebaseDatabase.getReference().child("profile").child(mAuth.getUid()).child("following");

        R.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final String uid = dataSnapshot.getRef().getKey();

                DatabaseReference R2 = firebaseDatabase.getReference().child("profile").child(uid);

                R2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                        following f = new following();

                        f.setUid(uid);
                        f.setUsername(map.get("userName").toString());
                        f.setProfilePhoto(map.get("profilePhoto").toString());
                        f.setName(map.get("name").toString());

                        followingList.add(f);

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
                if (dataSnapshot.exists()){
                    String uid = dataSnapshot.getRef().getKey();
                    if (uid != null){
                        //followingList.remove(followingList.size()-1);
                    }
                }
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
        private ArrayList<following> List ;
        private Context mContext;

        public RecyclerViewAdapter1(Context context, ArrayList<following> list) {
            List = list;
            mContext = context;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_following_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {



            Uri uri = Uri.parse(List.get(position).getProfilePhoto());

            Glide.with(mContext)
                    .load(uri)
                    .into(holder.proImg);

            holder.userName.setText(List.get(position).getUsername());
            holder.name.setText(List.get(position).getName());

            holder.mFollow.setText("unfollow");


            holder.mFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                    FirebaseDatabase.getInstance().getReference().child("profile").child(userId).child("following").child(List.get(holder.getLayoutPosition()).getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("profile").child(List.get(holder.getLayoutPosition()).getUid()).child("followers").child(userId).removeValue();

                    followingList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, followingList.size());
                    //holder.itemView.setVisibility(View.GONE);

                }
            });


            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileFragment fragment = ProfileFragment.newInstance(List.get(position).getUid());
                    FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.remove(fragment);
                    transaction.replace(R.id.Porfragment_container, fragment);
                    transaction.commit();
                }
            });

        }

        @Override
        public int getItemCount() {
            return List.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            Button mFollow;
            ImageView proImg;
            TextView name;
            TextView userName;
            RelativeLayout relativeLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                mFollow = itemView.findViewById(R.id.follow);
                proImg = itemView.findViewById(R.id.profile_photo);
                userName = itemView.findViewById(R.id.userName);
                name = itemView.findViewById(R.id.Name);
                relativeLayout = itemView.findViewById(R.id.followingrellayout);


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

    @Override
    public void onPause() {
        super.onPause();
        followingList.clear();
    }


    /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    public void onFragmentInteraction(Uri uri){
    }
    public void OnFragmentInteractionListener(Uri uri){
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
