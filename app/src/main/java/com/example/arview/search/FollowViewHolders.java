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
    TextView mEmail;
    Button mFollow;
    ImageView proImg;
    public TextView name;
    public android.widget.LinearLayout LinearLayout;


    FollowViewHolders(View itemView){
        super(itemView);
        mEmail = itemView.findViewById(R.id.email);
        mFollow = itemView.findViewById(R.id.follow);
        proImg = itemView.findViewById(R.id.profile_photo);
        LinearLayout = itemView.findViewById(R.id.LinearLayout);



    }


}