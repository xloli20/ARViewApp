package com.example.arview.main;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.arview.R;

import androidx.recyclerview.widget.RecyclerView;

public class FollowViewHolders extends RecyclerView.ViewHolder{
    public TextView mEmail;
    public Button mFollow;

    public FollowViewHolders(View itemView){
        super(itemView);
        mEmail = itemView.findViewById(R.id.email);
        mFollow = itemView.findViewById(R.id.follow);

    }


}