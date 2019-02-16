package com.example.arview.caht;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arview.R;
import com.example.arview.databaseClasses.chatMessage;

import java.util.ArrayList;
import java.util.List;

public class chatMessageAdapterOld extends ArrayAdapter<chatMessage> {
    public chatMessageAdapterOld(Context context, int resource, List<chatMessage> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.layout_item_message, parent, false);
        }

        RelativeLayout right =  convertView.findViewById(R.id.right);
        RelativeLayout left =  convertView.findViewById(R.id.left);

        ImageView photoImageView =  convertView.findViewById(R.id.messagePhoto);
        TextView messageTextView =  convertView.findViewById(R.id.messsageText);

        chatMessage message = getItem(position);

        if (message.getSender() != null){
            left.setVisibility(View.GONE);
        }else {
            right.setVisibility(View.GONE);
        }

        boolean isPhoto = message.getPhotoURL() != null;
        if (isPhoto) {
            messageTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext())
                    .load(message.getPhotoURL())
                    .into(photoImageView);
        } else {
            messageTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            messageTextView.setText(message.getText());
        }

        return convertView;
    }


}
