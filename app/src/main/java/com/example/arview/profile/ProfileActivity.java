package com.example.arview.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.arview.post.PostDetailsFragment;
import com.example.arview.R;
import com.example.arview.profile.setting.SettingActivity;
import com.example.arview.utils.UniversalImageLoader;

public class ProfileActivity extends AppCompatActivity implements PostDetailsFragment.OnFragmentInteractionListener,
                                                                    ProfileEditFragment.OnFragmentInteractionListener {

    private static final String TAG = "ProfileActivity";

    //widget
    public Button profileButton;
    public ListView postlist;
    private Toolbar toolbar;
    private ImageView profileMenu ,profilePhoto;
    private FrameLayout profileContainer;
    private ProgressBar mProgressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setUpProfileWidget();

        mProgressBar.setVisibility(View.GONE);
        setProfileimage();

        //setting
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, SettingActivity.class);
                i.setAction(Intent.ACTION_EDIT);
                startActivity(i);
            }
        });

        //profile button
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileEditFragment();
            }
        });

        // post list
        ProfileActivity.CustomAdapter CA = new ProfileActivity.CustomAdapter();
        postlist.setAdapter(CA);
        postlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openPostDetailsFragment();
            }
        });


    }


    private void setProfileimage(){
        String imgURL = "https://www.android.com/static/2016/img/share/andy-lg.png";
        UniversalImageLoader.setImage(imgURL, profilePhoto, null , "https://");

    }

    private void setUpProfileWidget(){
        profileContainer = (FrameLayout) findViewById(R.id.profile_container);
        mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        toolbar = (Toolbar) findViewById(R.id.profileToolBar);
        profileMenu = (ImageView) findViewById(R.id.profileMenu);
        profileButton =(Button) findViewById(R.id.profileButton);
        postlist = (ListView) findViewById(R.id.postList);
        profilePhoto = (ImageView) findViewById(R.id.profile_photo);

    }


    class CustomAdapter extends BaseAdapter {
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


    public void openPostDetailsFragment() {
        PostDetailsFragment fragment = PostDetailsFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.remove(fragment);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


    public void openProfileEditFragment() {
        ProfileEditFragment fragment = ProfileEditFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.remove(fragment);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }



    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
    public void OnFragmentInteractionListener(Uri uri){
        //you can leave it empty
    }

}
