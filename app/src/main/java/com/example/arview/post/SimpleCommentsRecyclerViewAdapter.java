package com.example.arview.post;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.databaseClasses.comment;
import com.example.arview.profile.ProfileFragment;
import com.example.arview.utils.CalTimeDiff;
import com.example.arview.utils.FirebaseMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class SimpleCommentsRecyclerViewAdapter extends RecyclerView.Adapter<SimpleCommentsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "SimpleCommentsRecyclerViewAdapter";

    //vars
    private ArrayList<comment> CommentsList = new ArrayList<>();
    private String userID;
    private Context mContext;

    public SimpleCommentsRecyclerViewAdapter(Context context, ArrayList<comment> coments, String uID) {
        CommentsList = coments;
        userID = uID;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_simple_comment_list, parent, false);

        firebaseMethods = new FirebaseMethods(mContext);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return CommentsList.size();
    }

    private FirebaseMethods firebaseMethods;

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView userName, comment;
        private CircleImageView profilePhoto;


        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textView);
            comment = itemView.findViewById(R.id.textView1);
            profilePhoto = itemView.findViewById(R.id.profile_photo);

        }
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.userName.setText(CommentsList.get(position).getUserName());
        holder.comment.setText(shrink(CommentsList.get(position).getComment()));


        getProfilePhoto(userID, holder);

        holder.profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment fragment = ProfileFragment.newInstance(CommentsList.get(position).getUserID());
                FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                transaction.addToBackStack(null);
                transaction.remove(fragment);
                transaction.replace(R.id.fragment_container3, fragment);
                transaction.commit();
            }
        });


    }


    private String shrink(String des){
        String SDes = des ;
        if (des != null){
            if (des.length() > 50){
                SDes = des.substring(0, 50);
                SDes = SDes + "...";
            }
        }
        return SDes;
    }




    public void getProfilePhoto(String userID , final ViewHolder holder ){

        DatabaseReference RR = FirebaseDatabase.getInstance().getReference().child("profile").child(userID).child("profilePhoto");
        RR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Uri uri = Uri.parse(dataSnapshot.getValue(String.class));
                Glide.with(mContext)
                        .load(uri)
                        .into(holder.profilePhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
