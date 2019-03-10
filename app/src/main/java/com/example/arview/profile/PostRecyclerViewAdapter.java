package com.example.arview.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.databaseClasses.chatMessage;
import com.example.arview.databaseClasses.post;
import com.example.arview.location.MapsActivity;
import com.example.arview.post.PostDetailsFragment;
import com.example.arview.setting.SettingActivity;

import java.util.ArrayList;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "PostRecyclerViewAdapter";

    //vars
    private ArrayList<post> PostList = new ArrayList<>();
    private String userID;
    private Context mContext;

    public PostRecyclerViewAdapter(Context context, ArrayList<post> posts, String uID) {
        PostList = posts;
        userID = uID;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");


        // check liked from firebase

        holder.PostName.setText(PostList.get(position).getPostName());
        holder.desc.setText(PostList.get(position).getPostDesc());
        holder.Nlike.setText(String.valueOf(PostList.get(position).getLikesCount()));
        holder.Ncomment.setText(String.valueOf(PostList.get(position).getCommentsCount()));

        if (PostList.get(position).getPostEndDate()== null && PostList.get(position).getPostEndTime() == null
                || PostList.get(position).getPostEndDate()== "" && PostList.get(position).getPostEndTime() == "" ){
            holder.limit.setVisibility(View.INVISIBLE);
            holder.limitTime.setText(PostList.get(position).getPostCreatedDate());
        }
        else if (PostList.get(position).getPostEndDate()!=null) {
            holder.limit.setVisibility(View.VISIBLE);
            holder.limitTime.setText(PostList.get(position).getPostEndDate());
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
                intent.setAction(Intent.ACTION_EDIT);
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContext instanceof ProfileActivity) {
                    ((ProfileActivity)mContext).openPostDetailsFragment();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return PostList.size();
    }

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



}
