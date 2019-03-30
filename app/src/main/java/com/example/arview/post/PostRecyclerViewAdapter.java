package com.example.arview.post;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arview.R;
import com.example.arview.databaseClasses.post;
import com.example.arview.location.MapsActivity;
import com.example.arview.profile.ProfileActivity;
import com.example.arview.utils.FirebaseMethods;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "PostRecyclerViewAdapter";

    //vars
    private ArrayList<post> PostList = new ArrayList<>();
    private String userID;
    private Context mContext;
    private boolean liked = false;

    public PostRecyclerViewAdapter(Context context, ArrayList<post> posts, String uID) {
        PostList = posts;
        userID = uID;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_list, parent, false);

        firebaseMethods = new FirebaseMethods(mContext);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return PostList.size();
    }

    private FirebaseMethods firebaseMethods;

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView location, limit, like, comment;
        public TextView PostName, desc, Nlike, Ncomment, limitTime;
        public RelativeLayout relativeLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.post_layout);

            location =  itemView.findViewById(R.id.location);
            limit = itemView.findViewById(R.id.imageButton1);
            like =  itemView.findViewById(R.id.imageButton2);
            comment = itemView.findViewById(R.id.imageButton3);

            PostName =  itemView.findViewById(R.id.textView);
            desc =  itemView.findViewById(R.id.textView1);
            Nlike = itemView.findViewById(R.id.textView2);
            Ncomment =itemView.findViewById(R.id.textView3);
            limitTime = itemView.findViewById(R.id.textView4);

            PostName.setText("post name");

        }
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");


        // check liked from firebase

        holder.PostName.setText(PostList.get(position).getPostName());
        holder.desc.setText(PostList.get(position).getPostDesc());
        holder.Nlike.setText(String.valueOf(PostList.get(position).getLikes()));
        holder.Ncomment.setText(String.valueOf(PostList.get(position).getComments()));

        if (PostList.get(position).getPostEndTime() == null || PostList.get(position).getPostEndTime() == "" ){
            holder.limit.setVisibility(View.INVISIBLE);
            holder.limitTime.setText(PostList.get(position).getPostCreatedDate());
        }
        else if (PostList.get(position).getPostEndTime()!=null) {
            holder.limit.setVisibility(View.VISIBLE);
            holder.limitTime.setText(PostList.get(position).getPostEndTime());
        }



        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MapsActivity.class);
                intent.putExtra("PostLocation", PostList.get(position).getPostLocation());
                mContext.startActivity(intent);

                //MoveCam

            }
        });

        holder.like.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_empty_heart));
        liked = firebaseMethods.isLiked(PostList.get(position), holder);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liked){
                    firebaseMethods.unLike(PostList.get(position), userID);
                    notifyItemChanged(position);
                    notifyItemRangeChanged(position, Integer.valueOf(PostList.get(position).getLikes()));
                    holder.like.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_empty_heart));
                    liked= false;
                }else {
                    firebaseMethods.addLike(PostList.get(position), userID);
                    notifyItemChanged(position);
                    notifyItemRangeChanged(position, Integer.valueOf(PostList.get(position).getLikes()));
                    holder.like.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_red_heart));
                    liked = true;

                }
            }
        });


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostDetailsFragment fragment = PostDetailsFragment.newInstance(PostList.get(position).getOwnerId(), PostList.get(position).getPostId());
                FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                transaction.addToBackStack(null);
                transaction.remove(fragment);
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();
            }
        });

    }


}
