package com.example.arview.chat;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.databaseClasses.addChat;
import com.example.arview.databaseClasses.following;
import com.example.arview.profile.ProfileFragment;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class addChatAdapter2 extends RecyclerView.Adapter<addChatAdapter2.ViewHolder> {

    private static final String TAG = "addChatAdapter";

    //vars
    private ArrayList<addChat> List ;
    private Context mContext;

    public addChatAdapter2(Context context, ArrayList<addChat> list ) {
        List = list;
        mContext = context;

    }

    @NonNull
    @Override
    public addChatAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_add_chat_list, parent, false);
        return new addChatAdapter2.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final addChatAdapter2.ViewHolder holder, final int position) {

        Uri uri = Uri.parse(List.get(position).getProfilePhoto());

        Glide.with(mContext)
                .load(uri)
                .into(holder.proImg);

        holder.userName.setText(List.get(position).getUsername());
        holder.name.setText(List.get(position).getName());

        if (List.get(position).getChatId()== null){
            holder.mMessage.setText("start chatting");
        }
        else if (List.get(position).getChatId()!= null) {
            holder.mMessage.setText("open chat");
        }


        holder.mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(mContext, InChatActivity.class);
                    intent.putExtra("ChatID",List.get(position).getChatId());
                    intent.putExtra("OtherUserId",List.get(position).getUid());
                    mContext.startActivity(intent);
            }
        });


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        Button mMessage;
        ImageView proImg;
        TextView name;
        TextView userName;
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mMessage = itemView.findViewById(R.id.message);
            proImg = itemView.findViewById(R.id.profile_photo);
            userName = itemView.findViewById(R.id.userName);
            name = itemView.findViewById(R.id.Name);
            relativeLayout = itemView.findViewById(R.id.addChatRelLayout);


        }
    }
}