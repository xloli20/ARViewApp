package com.example.arview.search;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.arview.R;

import androidx.recyclerview.widget.RecyclerView;

class FollowViewHolders extends RecyclerView.ViewHolder{
    Button mFollow;
    ImageView proImg;
    TextView name;
    RelativeLayout relativeLayout;


    FollowViewHolders(View itemView){
        super(itemView);
        mFollow = itemView.findViewById(R.id.follow);
        proImg = itemView.findViewById(R.id.profile_photo);
        name = itemView.findViewById(R.id.userName);
        relativeLayout = itemView.findViewById(R.id.userRelLayout);


    }


}