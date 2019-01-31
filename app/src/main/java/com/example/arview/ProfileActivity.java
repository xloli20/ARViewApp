package com.example.arview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    public Button follow;
    public ImageButton location;
    public ImageButton limit;
    public ImageButton like;
    public ImageButton comment;
    public TextView UName;
    public TextView desc;
    public TextView Nlike;
    public TextView Ncomment;
    public TextView limitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        follow =(Button) findViewById(R.id.button2);

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "here", Toast.LENGTH_SHORT).show();
                openFragment();
            }
        });


        ListView postlist = (ListView) findViewById(R.id.postList);
        CustomAdapter CA = new CustomAdapter();
        postlist.setAdapter(CA);
    }

    public void openFragment() {

        PostDetailsFragment fragment = PostDetailsFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.postDetails_container, fragment);
        transaction.commit();
    }


    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.layout_post_list, null);

             location = (ImageButton) view.findViewById(R.id.imageButton);
             limit = (ImageButton) view.findViewById(R.id.imageButton1);
             like = (ImageButton) view.findViewById(R.id.imageButton2);
             comment = (ImageButton) view.findViewById(R.id.imageButton3);

             UName = (TextView) view.findViewById(R.id.textView);
             desc = (TextView) view.findViewById(R.id.textView1);
             Nlike = (TextView) view.findViewById(R.id.textView2);
             Ncomment = (TextView) view.findViewById(R.id.textView3);
             limitTime = (TextView) view.findViewById(R.id.textView4);

            UName.setText("user name blala");

            return view;
        }
    }
}
