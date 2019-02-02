package com.example.arview.profile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.arview.post.PostDetailsFragment;
import com.example.arview.R;


public class ProfileFragment extends Fragment {

    public Button follow;
    public ListView postlist;
    private FrameLayout postDetailsContainer;
    private FrameLayout settingContainer;


    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        postDetailsContainer = (FrameLayout) view.findViewById(R.id.postDetails_container);
        settingContainer  = (FrameLayout) view.findViewById(R.id.setting_container);
        follow =(Button) view.findViewById(R.id.button2);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapFragment();
            }
        });

        postlist = (ListView) view.findViewById(R.id.postList);
        ProfileFragment.CustomAdapter CA = new ProfileFragment.CustomAdapter();
        postlist.setAdapter(CA);

        postlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openFragment();
            }
        });


        return view;
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

    public void openFragment() {
        PostDetailsFragment fragment = PostDetailsFragment.newInstance();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.postDetails_container, fragment);
        transaction.commit();
    }


    public void swapFragment() {
        SettingFragment fragment = SettingFragment.newInstance();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.setting_container, fragment);
        transaction.commit();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
