package com.example.arview.friend;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.databaseClasses.post;
import com.example.arview.location.MapsActivity;
import com.example.arview.post.CommentsRecyclerViewAdapter;
import com.example.arview.post.PostCommentsFragment;
import com.example.arview.post.PostDetailsFragment;
import com.example.arview.post.PostRecyclerViewAdapter;
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

public class FriendsPostRecyclerViewAdapter extends RecyclerView.Adapter<FriendsPostRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "PostRecyclerViewAdapter";

    //vars
    private ArrayList<post> PostList = new ArrayList<>();
    private String userID;
    private Context mContext;

    private boolean liked = false;
    private FirebaseMethods firebaseMethods;

    FriendsPostRecyclerViewAdapter(Context context, ArrayList<post> posts, String uID) {
        PostList = posts;
        userID = uID;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friends_post_list, parent, false);

        firebaseMethods = new FirebaseMethods(mContext);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return PostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView location, limit, like, comment;
        public TextView userName, PostName, desc, Nlike, Ncomment, limitTime;
        public RelativeLayout relativeLayout;

        private CircleImageView profilePhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.post_layout);
            location = itemView.findViewById(R.id.location);
            limit = itemView.findViewById(R.id.imageButton1);
            like = itemView.findViewById(R.id.imageButton2);
            comment = itemView.findViewById(R.id.imageButton3);

            userName =  itemView.findViewById(R.id.userName);
            desc =  itemView.findViewById(R.id.textView1);
            PostName = itemView.findViewById(R.id.textView);
            desc = itemView.findViewById(R.id.textView1);
            Nlike = itemView.findViewById(R.id.textView2);
            Ncomment = itemView.findViewById(R.id.textView3);
            limitTime = itemView.findViewById(R.id.textView4);
            profilePhoto = itemView.findViewById(R.id.profile_photo);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");


        holder.PostName.setText(PostList.get(position).getPostName());
        holder.desc.setText(shrinkDesc(PostList.get(position).getPostDesc()));
        holder.Nlike.setText(String.valueOf(PostList.get(position).getLikes()));
        holder.Ncomment.setText(String.valueOf(PostList.get(position).getComments()));

        if (PostList.get(position).getPostEndTime() == null || PostList.get(position).getPostEndTime() == "") {
            holder.limit.setVisibility(View.INVISIBLE);
            holder.limitTime.setText(PostList.get(position).getPostCreatedDate());
        } else if (PostList.get(position).getPostEndTime() != null) {
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
        liked = firebaseMethods.isFLiked(PostList.get(position), holder);
        time(PostList.get(position), holder, position);
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liked) {
                    firebaseMethods.unLike(PostList.get(position), userID);
                    holder.like.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_empty_heart));
                    liked = false;
                } else {
                    firebaseMethods.addLike(PostList.get(position), userID);
                    holder.like.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_red_heart));
                    liked = true;
                }
            }
        });
        getProfilePhoto(PostList.get(position).getOwnerId(), holder);

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostCommentsFragment fragment = PostCommentsFragment.newInstance(String.valueOf(PostList.get(position).isPersonal()) + String.valueOf(PostList.get(position).isVisibilty()), PostList.get(position).getPostId());
                Log.e(TAG, String.valueOf(PostList.get(position).isPersonal()) + String.valueOf(PostList.get(position).isVisibilty()));
                FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                transaction.addToBackStack(null);
                transaction.remove(fragment);
                transaction.replace(R.id.Ffragment_container, fragment);
                transaction.commit();
            }
        });

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostDetailsFragment fragment = PostDetailsFragment.newInstance(PostList.get(position).getOwnerId(), PostList.get(position).getPostId(), String.valueOf(PostList.get(position).isPersonal()) + String.valueOf(PostList.get(position).isVisibilty()));
                FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                transaction.addToBackStack(null);
                transaction.remove(fragment);
                transaction.replace(R.id.Ffragment_container, fragment);
                transaction.commit();
            }
        });

        holder.profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment fragment = ProfileFragment.newInstance(PostList.get(position).getOwnerId());
                FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                transaction.addToBackStack(null);
                transaction.remove(fragment);
                transaction.replace(R.id.Ffragment_container, fragment);
                transaction.commit();
            }
        });
    }

    public void getProfilePhoto(String userID, final FriendsPostRecyclerViewAdapter.ViewHolder holder) {

        DatabaseReference RR = FirebaseDatabase.getInstance().getReference().child("profile").child(userID);
        RR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Uri uri = Uri.parse(dataSnapshot.child("profilePhoto").getValue(String.class));
                Glide.with(mContext)
                        .load(uri)
                        .into(holder.profilePhoto);

                holder.userName.setText(dataSnapshot.child("name").getValue(String.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private String shrinkDesc(String des) {
        String SDes = des;
        if (des != null) {
            if (des.length() > 160) {
                SDes = des.substring(0, 160);
                SDes = SDes + "...";
            }
        }
        return SDes;
    }

    private void time(post p, FriendsPostRecyclerViewAdapter.ViewHolder holder, int i) {
        if (p.getPostEndTime() == null) {
            // set date created
            holder.limit.setVisibility(View.INVISIBLE);
            String timeDiff = CalTimeDiff.getTimestampDifference(p.getPostCreatedDate());
            if (!timeDiff.equals("0")) {
                holder.limitTime.setText(timeDiff + " DAYS AGO");
            } else
                holder.limitTime.setText("Today");

        } else if (p.getPostEndTime() != null) {
            //set date End
            holder.limit.setVisibility(View.VISIBLE);
            String timeDiff = CalTimeDiff.getTimestampDifference(p.getPostEndTime());

            if (!timeDiff.contains("-")) {
                Log.e(TAG, "getTimestampDifference: ----: " + timeDiff);
                firebaseMethods.deletePost(p.getPostId(), p.getOwnerId(), p.isVisibilty(), p.isPersonal());
                PostList.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, PostList.size() - 1);
            }

            if (!timeDiff.equals("0")) {
                String d = timeDiff.substring(timeDiff.indexOf("-") + 1);
                holder.limitTime.setText(d + " DAYS LEFT");
            } else {
                String HtimeDiff = CalTimeDiff.getTimestampDifferenceH(p.getPostEndTime());
                if (!HtimeDiff.equals("0")) {
                    String d = timeDiff.substring(timeDiff.indexOf("-") + 1);
                    holder.limitTime.setText(d + "h LEFT");
                } else {
                    String MtimeDiff = CalTimeDiff.getTimestampDifferenceM(p.getPostEndTime());
                    if (!MtimeDiff.equals("0")) {
                        String d = timeDiff.substring(timeDiff.indexOf("-") + 1);
                        holder.limitTime.setText(d + "m LEFT");
                    } else {
                        String StimeDiff = CalTimeDiff.getTimestampDifferenceS(p.getPostEndTime());
                        if (!StimeDiff.equals("0")) {
                            String d = timeDiff.substring(timeDiff.indexOf("-") + 1);
                            holder.limitTime.setText(d + "s LEFT");
                        } else {
                            Log.e(TAG, "getTimestampDifference: ----: " + StimeDiff);
                            firebaseMethods.deletePost(p.getPostId(), p.getOwnerId(), p.isVisibilty(), p.isPersonal());
                            PostList.remove(i);
                            notifyItemRemoved(i);
                            notifyItemRangeChanged(i, PostList.size() - 1);
                        }
                    }
                }
            }
        }
    }
}
