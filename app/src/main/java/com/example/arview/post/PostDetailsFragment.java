package com.example.arview.post;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.arview.R;


public class PostDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    //
    private ListView commentList;
    private ImageView backArrow ;
    private OnFragmentInteractionListener mListener;

    public PostDetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    //public static PostDetailsFragment newInstance(String param1, String param2) {
    public static PostDetailsFragment newInstance() {
            PostDetailsFragment fragment = new PostDetailsFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_details, container, false);

        backArrow = (ImageView) view.findViewById(R.id.backArrow);
        //setup the backarrow
        backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closefragment();
            }
        });


        setupListView(view);


        return view;
    }

    private void setupListView(View view){
        commentList = (ListView) view.findViewById(R.id.commentList);
        PostDetailsFragment.CustomAdapter CA = new PostDetailsFragment.CustomAdapter();
        commentList.setAdapter(CA);

    }


    private void closefragment() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }


    class CustomAdapter extends BaseAdapter {

        private ImageView proImg;
        private TextView Uname;
        private TextView comment;
        private TextView commentDate;

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
            view = getLayoutInflater().inflate(R.layout.layout_comment_list, null);

            proImg = (ImageView) view.findViewById(R.id.profile_photo);
            Uname = (TextView) view.findViewById(R.id.textView);
            comment = (TextView) view.findViewById(R.id.textView1);
            commentDate = (TextView) view.findViewById(R.id.textView2);

            return view;
        }
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
