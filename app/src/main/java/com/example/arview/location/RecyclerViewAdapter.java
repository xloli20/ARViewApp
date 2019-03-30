package com.example.arview.location;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.databaseClasses.nearPost;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 2/12/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    //vars
    private ArrayList<nearPost> nearPostList = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context context, ArrayList<nearPost> posts) {
        nearPostList = posts;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_near_post_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Uri uri = Uri.parse(nearPostList.get(position).getProfilePhoto());
        Glide.with(mContext)
                .load(uri)
                .into(holder.profileImage);

        holder.postName.setText(nearPostList.get(position).getPostName());
        holder.userName.setText(nearPostList.get(position).getOwnerName());
        holder.distance.setText(nearPostList.get(position).getDestinace());
        holder.Nlike.setText(nearPostList.get(position).getLikeCount());


        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, nearPostList.get(position).getOwnerId(), Toast.LENGTH_SHORT).show();
                // to user profile
            }
        });

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, nearPostList.get(position).getPostId(), Toast.LENGTH_SHORT).show();
                // to post details
            }
        });

        holder.direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MapsActivity)mContext).moveCam(nearPostList.get(position).getLocation());
            }
        });

        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.heart.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_red_heart));
                holder.Nlike.setText("1");

            }
        });
    }

    @Override
    public int getItemCount() {
        return nearPostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImage;
        ImageView direction, heart;
        TextView postName, userName, distance, Nlike;
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_photo);
            postName = itemView.findViewById(R.id.postName);
            userName = itemView.findViewById(R.id.username);
            distance = itemView.findViewById(R.id.far);
            Nlike = itemView.findViewById(R.id.likeNum);
            relativeLayout = itemView.findViewById(R.id.NRelativeLayout);
            direction = itemView.findViewById(R.id.direction);
            heart = itemView.findViewById(R.id.heart);

        }
    }
}
