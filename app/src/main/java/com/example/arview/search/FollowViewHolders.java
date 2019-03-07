package com.example.arview.search;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.arview.R;

import androidx.recyclerview.widget.RecyclerView;

class FollowViewHolders extends RecyclerView.ViewHolder{
    TextView mEmail;
    Button mFollow;

    FollowViewHolders(View itemView){
        super(itemView);
        mEmail = itemView.findViewById(R.id.email);
        mFollow = itemView.findViewById(R.id.follow);

    }


}