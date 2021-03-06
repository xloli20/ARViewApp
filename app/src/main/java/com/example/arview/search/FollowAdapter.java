package com.example.arview.search;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.databaseClasses.following;
import com.example.arview.databaseClasses.profile;
import com.example.arview.databaseClasses.users;
import com.example.arview.profile.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class FollowAdapter extends RecyclerView.Adapter<FollowViewHolders>{

    private List<following> usersList;
    private Context context;

    FollowAdapter(List<following> usersList, Context context){
        this.usersList = usersList;
        this.context = context;
    }
    @NonNull
    @Override
    public FollowViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_user_list, null);
        return new FollowViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FollowViewHolders holder, final int position) {
        holder.name.setText(usersList.get(position).getUsername());
        Uri uri = Uri.parse(usersList.get(position).getProfilePhoto());

        Glide.with(context)
                .load(uri)
                .into(holder.proImg);


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment fragment = ProfileFragment.newInstance(usersList.get(position).getUid());
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.addToBackStack(null);
                transaction.remove(fragment);
                transaction.replace(R.id.fragment_container0, fragment);
                transaction.commit();
            }
        });

        if(UserInformation.listFollowing.contains(usersList.get(holder.getLayoutPosition()).getUid())){
            holder.mFollow.setText("following");
        }else{
            holder.mFollow.setText("follow");
        }

        holder.mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                if(holder.mFollow.getText().equals("follow")){
                    holder.mFollow.setText("following");
                    FirebaseDatabase.getInstance().getReference().child("profile").child(userId).child("following").child(usersList.get(holder.getLayoutPosition()).getUid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("profile").child(usersList.get(holder.getLayoutPosition()).getUid()).child("followers").child(userId).setValue(true);

                    NotificationUtils.notifyUserGetsFollower(context,userId);


                }else{
                    holder.mFollow.setText("follow");
                    FirebaseDatabase.getInstance().getReference().child("profile").child(userId).child("following").child(usersList.get(holder.getLayoutPosition()).getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("profile").child(usersList.get(holder.getLayoutPosition()).getUid()).child("followers").child(userId).removeValue();

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.usersList.size();
    }

}
