package com.example.arview.chat;

import android.content.Context;
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

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


public class ChatsRecyclerViewAdapter extends RecyclerView.Adapter<ChatsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "ChatsRecyclerViewAdapter";

    //vars
    private ArrayList<chatMessage> chatMessage = new ArrayList<>();
    private String userID;
    private Uri uriP;
    private Context mContext;

    public ChatsRecyclerViewAdapter(Context context, ArrayList<chatMessage> chatMessageS, String uID, Uri uri) {
        chatMessage = chatMessageS;
        userID = uID;
        uriP = uri ;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");


        if (chatMessage.get(position).getSender() == userID){
            holder.left.setVisibility(View.GONE);

            if (chatMessage.get(position).getPhotoURL() != null) {
                holder.messageTextViewR.setVisibility(View.GONE);
                holder.photoImageViewR.setVisibility(View.VISIBLE);
                holder.postR.setVisibility(View.GONE);
                Glide.with(mContext)
                        .load(chatMessage.get(position).getPhotoURL())
                        .into(holder.photoImageViewR);
            }
            else if (chatMessage.get(position).getText() != null){
                holder.messageTextViewR.setVisibility(View.VISIBLE);
                holder.photoImageViewR.setVisibility(View.GONE);
                holder.postR.setVisibility(View.GONE);
                holder.messageTextViewR.setText(chatMessage.get(position).getText());
            }
            else if (chatMessage.get(position).getPostID() != null){
                holder.messageTextViewR.setVisibility(View.GONE);
                holder.photoImageViewR.setVisibility(View.GONE);
                holder.postR.setVisibility(View.VISIBLE);
            }

        }else {
            holder.right.setVisibility(View.GONE);

            Glide.with(mContext)
                    .load(uriP)
                    .into(holder.userPhoto);

            if (chatMessage.get(position).getPhotoURL() != null) {
                holder.messageTextView.setVisibility(View.GONE);
                holder.photoImageView.setVisibility(View.VISIBLE);
                holder.postL.setVisibility(View.GONE);

                Glide.with(mContext)
                        .load(chatMessage.get(position).getPhotoURL())
                        .into(holder.photoImageView);
            }
            else if (chatMessage.get(position).getText() != null){
                holder.messageTextView.setVisibility(View.VISIBLE);
                holder.photoImageView.setVisibility(View.GONE);
                holder.postL.setVisibility(View.GONE);
                holder.messageTextView.setText(chatMessage.get(position).getText());
            }
            else if (chatMessage.get(position).getPostID() != null){
                holder.messageTextViewR.setVisibility(View.GONE);
                holder.photoImageViewR.setVisibility(View.GONE);
                holder.postR.setVisibility(View.VISIBLE);

            }
        }

    }

    @Override
    public int getItemCount() {
        return chatMessage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout right, left, postL, postR;


        ImageView userPhoto;
        ImageView photoImageView;
        TextView messageTextView;

        ImageView photoImageViewR ;
        TextView messageTextViewR ;

        public ViewHolder(View itemView) {
            super(itemView);
            right =itemView.findViewById(R.id.right);
            left = itemView.findViewById(R.id.left);
            postL =itemView.findViewById(R.id.messagePost);
            postR = itemView.findViewById(R.id.messagePostR);

            userPhoto = itemView.findViewById(R.id.profile_photoL);
            photoImageView =itemView.findViewById(R.id.messagePhoto);
            messageTextView =itemView.findViewById(R.id.messsageText);

            photoImageViewR = itemView.findViewById(R.id.messagePhotoR);
            messageTextViewR = itemView.findViewById(R.id.messsageTextR);
        }
    }


}
