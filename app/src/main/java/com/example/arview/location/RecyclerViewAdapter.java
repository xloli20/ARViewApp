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
import com.example.arview.post.PostDetailsFragment;
import com.example.arview.profile.ProfileFragment;
import com.example.arview.utils.FirebaseMethods;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
    private boolean liked = false;
    private String userID ;
    private FirebaseMethods firebaseMethods;



    public RecyclerViewAdapter(Context context, ArrayList<nearPost> posts, String userId) {
        userID = userId;
        nearPostList = posts;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_near_post_list, parent, false);

        firebaseMethods = new FirebaseMethods(mContext);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Uri uri = Uri.parse(nearPostList.get(position).getProfilePhoto());
        Glide.with(mContext)
                .load(uri)
                .into(holder.profileImage);

        holder.postName.setText(nearPostList.get(position).getPost().getPostName());
        holder.userName.setText(nearPostList.get(position).getOwnerName());
        holder.distance.setText(nearPostList.get(position).getDestinace());
        holder.Nlike.setText(nearPostList.get(position).getPost().getLikes());


        holder.direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MapsActivity)mContext).moveCam(nearPostList.get(position).getLocation());
            }
        });


        holder.heart.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_empty_heart));

        liked = firebaseMethods.isNLiked(nearPostList.get(position), holder);


        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (liked){
                    firebaseMethods.unLike(nearPostList.get(position).getPost(), userID);
                    holder.heart.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_empty_heart));
                    liked= false;
                }else {
                    firebaseMethods.addLike(nearPostList.get(position).getPost(), userID);
                    holder.heart.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_red_heart));
                    liked = true;

                }
            }
        });


        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment fragment = ProfileFragment.newInstance(nearPostList.get(position).getPost().getOwnerId());
                FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                transaction.addToBackStack(null);
                transaction.remove(fragment);
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();
            }
        });

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostDetailsFragment fragment = PostDetailsFragment.newInstance(nearPostList.get(position).getPost().getOwnerId(), nearPostList.get(position).getPost().getPostId(),
                        String.valueOf(nearPostList.get(position).getPost().isPersonal())+ String.valueOf(nearPostList.get(position).getPost().isVisibilty()));
                FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                transaction.addToBackStack(null);
                transaction.remove(fragment);
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return nearPostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImage;
        ImageView direction;
        public ImageView heart;
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
