package com.example.arview.post;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.arview.R;
import com.example.arview.login.SiginActivity;
import com.example.arview.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PostSettingFragment extends Fragment {
    private static final String TAG = "PostSettingFragment";


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //widgets
    private ImageView backArrow;
    private RadioGroup radioGroup;
    private RadioButton Rpersonal, Rpublic, Rprivate;
    private RelativeLayout timer, timeroff;
    private TextView EndTime;
    private EditText postName, postDes;
    private Button publish;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    //var
    private boolean Ppersonal = false;
    private boolean Pvisibelty = true ;

    private String PostEndTime = "" ;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;



    public PostSettingFragment() {
    }


    //public static PostSettingFragment newInstance(String param1, String param2) {
    public static PostSettingFragment newInstance() {
    PostSettingFragment fragment = new PostSettingFragment();
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
        View view = inflater.inflate(R.layout.fragment_post_setting, container, false);

        setupFirebaseAuth();
        setUpPostSettingWidget(view);
        return view;
    }



         /*
    -------------------------------wedget on click-----------------------------------------
    */

    private void setUpPostSettingWidget(View view){
        backArrow = view.findViewById(R.id.backArrow);
        radioGroup = view.findViewById(R.id.radioGroupVis);
        Rpersonal = view.findViewById(R.id.Personal);
        Rprivate = view.findViewById(R.id.Private);
        Rpublic = view.findViewById(R.id.Pubilc);
        timer = view.findViewById(R.id.timer);
        timeroff = view.findViewById(R.id.timeroff);
        EndTime = view.findViewById(R.id.timeTV);
        postName = view.findViewById(R.id.PostNameS);
        postDes = view.findViewById(R.id.PostDesS);
        publish = view.findViewById(R.id.btn_publish);


        backArrow();
        visibelty();
        timer();

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String PN =  postName.getText().toString();
                PN = PN.replaceFirst("^ *", "");

                String PD =  postDes.getText().toString();
                PD = PD.replaceFirst("^ *", "");
                Toast.makeText(getActivity(),  postDes.getText().toString(), Toast.LENGTH_LONG).show();

                if (! PN.equals("") ) {

                    if (! PD.equals("")) {

                        if (! PostEndTime.equals("")) {

                            if (PostEndTime.equals("off")) {
                                firebaseMethods.addPost(PN, PD, null, null, Pvisibelty, Ppersonal);
                                Toast.makeText(getActivity(), "Your post saved as personal post", Toast.LENGTH_LONG).show();
                            }
                            else{
                                firebaseMethods.addPost(PN, PD, null, PostEndTime, Pvisibelty, Ppersonal);
                                Toast.makeText(getActivity(), "publish", Toast.LENGTH_LONG).show();
                            }

                        }else Toast.makeText(getActivity(), "should set all post Setting Make Sure date not empty", Toast.LENGTH_LONG).show();
                    }else Toast.makeText(getActivity(), "should set all post Setting Make Sure Post description not empty", Toast.LENGTH_LONG).show();
                }else Toast.makeText(getActivity(), "should set all post Setting Make Sure Post name not empty", Toast.LENGTH_LONG).show();

            }
        });
    }


    private void visibelty(){

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.Personal ){
                    Ppersonal = true;
                    Pvisibelty = false;
                }
                else if (checkedId == R.id.Private ){
                    Pvisibelty = false;
                    Ppersonal = false;
                }
                else if (checkedId == R.id.Pubilc ){
                    Pvisibelty = true;
                    Ppersonal = false;
                }
            }
        });


    }

    int Year;
    int Month ;
    int Day ;
    private void timer(){

        timeroff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeroff.setBackground(getResources().getDrawable(R.drawable.grey_sqr));
                timer.setBackground(getResources().getDrawable(R.drawable.clear_sqr));
                EndTime.setText("");
                PostEndTime = "off";
            }
        });

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.setBackground(getResources().getDrawable(R.drawable.grey_sqr));
                timeroff.setBackground(getResources().getDrawable(R.drawable.clear_sqr));

                Calendar cal = Calendar.getInstance();
                 int year = cal.get(Calendar.YEAR);
                 int month = cal.get(Calendar.MONTH);
                 int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                Year = year;
                Month = month;
                Day = day;

                month = month + 1;

                String date = day + "-" + month+ "-" + year;
                EndTime.setText(date);


                Calendar cal = Calendar.getInstance();
                int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog2 = new TimePickerDialog(
                        getActivity(),
                        mTimeSetListener,
                        hourOfDay,minute,
                        DateFormat.is24HourFormat(getActivity()));
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog2.show();
            }
        };

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


                String h ;
                if (hourOfDay <10){
                    h = "0"+ String.valueOf(hourOfDay);
                }else
                    h = String.valueOf(hourOfDay);

                String m ;
                if (minute <10){
                    m = "0"+ String.valueOf(minute);
                }else
                    m = String.valueOf(minute);


                String time = h + ":" + m ;
                String date = EndTime.getText().toString();
                EndTime.setText(date + " , " + time );

                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR,Year);
                c.set(Calendar.MONTH, Month);
                c.set(Calendar.DAY_OF_MONTH, Day);
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 00);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
                sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
                java.util.Date ddate = c.getTime();
                String edate = sdf.format(ddate);

                PostEndTime = edate ;

            }
        };

    }



    private void backArrow(){
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closefragment();
            }
        });
    }
    private void closefragment() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }



    /*
  -------------------------------wedget on click-----------------------------------------
  */

      /*
    ------------------------------------ Firebase ---------------------------------------------
     */


    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        firebaseMethods = new FirebaseMethods(getActivity());

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged( FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(getActivity(), SiginActivity.class);
                    startActivity(intent);
                }
                // ...
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
      /*
    ------------------------------------ Firebase ---------------------------------------------
     */


    /*********************************************************************************/

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
        void onFragmentInteraction(Uri uri);
    }
}
