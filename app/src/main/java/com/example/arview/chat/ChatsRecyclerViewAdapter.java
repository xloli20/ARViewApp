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

        if (chatMessage.get(position).getSender().equals(userID)){
            holder.left.setVisibility(View.GONE);

            if (chatMessage.get(position).getPhotoURL() != null) {
                holder.messageTextViewR.setVisibility(View.GONE);
                holder.photoImageViewR.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(chatMessage.get(position).getPhotoURL())
                        .into(holder.photoImageViewR);
            } else {
                holder.messageTextViewR.setVisibility(View.VISIBLE);
                holder.photoImageViewR.setVisibility(View.GONE);
                holder.messageTextViewR.setText(chatMessage.get(position).getText());
            }

        }else {
            holder.right.setVisibility(View.GONE);

            Glide.with(mContext)
                    .load(uriP)
                    .into(holder.userPhoto);

            if (chatMessage.get(position).getPhotoURL() != null) {
                holder.messageTextView.setVisibility(View.GONE);
                holder.photoImageView.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(chatMessage.get(position).getPhotoURL())
                        .into(holder.photoImageView);
            } else {
                holder.messageTextView.setVisibility(View.VISIBLE);
                holder.photoImageView.setVisibility(View.GONE);
                holder.messageTextView.setText(chatMessage.get(position).getText());
            }
        }

    }

    @Override
    public int getItemCount() {
        return chatMessage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout right;
        RelativeLayout left;

        ImageView userPhoto;
        ImageView photoImageView;
        TextView messageTextView;

        ImageView photoImageViewR ;
        TextView messageTextViewR ;

        public ViewHolder(View itemView) {
            super(itemView);
            right = (RelativeLayout) itemView.findViewById(R.id.right);
            left = (RelativeLayout) itemView.findViewById(R.id.left);

            userPhoto = (ImageView) itemView.findViewById(R.id.profile_photoL);
            photoImageView = (ImageView) itemView.findViewById(R.id.messagePhoto);
            messageTextView = (TextView) itemView.findViewById(R.id.messsageText);

            photoImageViewR = (ImageView) itemView.findViewById(R.id.messagePhotoR);
            messageTextViewR = (TextView) itemView.findViewById(R.id.messsageTextR);
        }
    }


}
