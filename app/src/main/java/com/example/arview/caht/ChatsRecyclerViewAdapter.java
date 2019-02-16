package com.example.arview.caht;

import android.content.Context;
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
import com.example.arview.databaseClasses.chatMessage;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 2/12/2018.
 */

public class ChatsRecyclerViewAdapter extends RecyclerView.Adapter<ChatsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    //vars
    private ArrayList<chatMessage> chatMessage = new ArrayList<>();
    private String userID;
    private Context mContext;

    public ChatsRecyclerViewAdapter(Context context, ArrayList<chatMessage> chatMessageS, String uID) {
        chatMessage = chatMessageS;
        userID = uID;
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

            if (chatMessage.get(position).getPhotoURL() != null) {
                holder.messageTextView.setVisibility(View.GONE);
                holder.photoImageView.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(chatMessage.get(position).getPhotoURL())
                        .into(holder.photoImageViewR);
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

        ImageView userPhotoR ;
        ImageView photoImageViewR ;
        TextView messageTextViewR ;

        public ViewHolder(View itemView) {
            super(itemView);
            right = (RelativeLayout) itemView.findViewById(R.id.right);
            left = (RelativeLayout) itemView.findViewById(R.id.left);

            userPhoto = (ImageView) itemView.findViewById(R.id.profile_photoL);
            photoImageView = (ImageView) itemView.findViewById(R.id.messagePhoto);
            messageTextView = (TextView) itemView.findViewById(R.id.messsageText);

            userPhotoR = (ImageView) itemView.findViewById(R.id.profile_photoR);
            photoImageViewR = (ImageView) itemView.findViewById(R.id.messagePhotoR);
            messageTextViewR = (TextView) itemView.findViewById(R.id.messsageTextR);
        }
    }

    
}
