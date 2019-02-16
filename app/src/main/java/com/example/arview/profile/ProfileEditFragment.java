package com.example.arview.profile;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.databaseClasses.chatMessage;
import com.example.arview.databaseClasses.profile;
import com.example.arview.login.SiginActivity;
import com.example.arview.utils.FirebaseMethods;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.Serializable;

import static android.app.Activity.RESULT_OK;


public class ProfileEditFragment extends Fragment {

    private static final String TAG = "ProfileEditFragment";
    private Context mContext;

    private static final int RC_PHOTO_PICKER =  1;



    //widget
    private ImageView backArrow, profilePhoto;
    private TextView cancel, changePhoto, deletePhoto ;
    private EditText Ename, EprofileDescription;
    private Button save;
    private ProgressBar mProgressBar;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;


    private OnFragmentInteractionListener mListener;

    public ProfileEditFragment() {
    }

    public static ProfileEditFragment newInstance() {
        ProfileEditFragment fragment = new ProfileEditFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profile_edit, container, false);

        setupFirebaseAuth();

        setUpEditProfileWidget(view);

        return view;
    }

     /*
    -------------------------------wedget on click-----------------------------------------
     */

    private void setProfileWedgets(profile profile){
        profile p = profile;

        if (p.getProfilePhoto() != null){
            Uri uri = Uri.parse(p.getProfilePhoto());
            Glide.with(profilePhoto.getContext())
                    .load(uri)
                    .into(profilePhoto);
        }

        Ename.setText(p.getName());
        EprofileDescription.setText(p.getProfileDescription());

    }

    private void setUpEditProfileWidget(View view){
        profilePhoto = (ImageView) view.findViewById(R.id.profile_photo);
        backArrow = (ImageView) view.findViewById(R.id.backArrow);
        cancel = (TextView) view.findViewById(R.id.cancel);
        changePhoto = (TextView) view.findViewById(R.id.changePhoto);
        deletePhoto = (TextView) view.findViewById(R.id.deletePhoto);
        Ename = (EditText)  view.findViewById(R.id.name);
        EprofileDescription = (EditText) view.findViewById(R.id.description);
        save = (Button)  view.findViewById(R.id.save);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);


        mProgressBar.setVisibility(View.GONE);

        backArrow();
        changePhoto();
        DeletePhoto();
        SAVE();
    }


    private void backArrow(){
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closefragment();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closefragment();
            }
        });
    }

    private void closefragment() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void changePhoto(){
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

    }

    private void DeletePhoto(){
        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseMethods.deleteProfilePhoto();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            firebaseMethods.uploadPhoto(selectedImageUri);

        }
    }

    private void SAVE(){

        //TODO: add username edit text ad chick exist

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseMethods.editProfile(Ename.getText().toString(),EprofileDescription.getText().toString());
                closefragment();
            }
        });
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




    /************************************************************************************/

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
