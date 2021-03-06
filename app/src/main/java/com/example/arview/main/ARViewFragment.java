package com.example.arview.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.rendering.LocationNode;
import uk.co.appoly.arcorelocation.rendering.LocationNodeRender;
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;
import uk.co.appoly.arcorelocation.utils.LocationUtils;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arview.DrawARActivity;
import com.example.arview.DrawActivity;
import com.example.arview.R;
import com.example.arview.location.MapsActivity;
import com.example.arview.login.SiginActivity;
import com.example.arview.post.PostDetailsFragment;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class ARViewFragment extends Fragment implements PostDetailsFragment.OnFragmentInteractionListener {

    private static final String TAG = "ARViewFragment";

    private boolean installRequested;
    private boolean hasFinishedLoading = false;

    private ArSceneView arSceneView;

    private LocationManager locationManager;

    // Renderables for this example
    private ViewRenderable LayoutRenderable;

    // Our ARCore-Location scene
    private LocationScene locationScene;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //wedget
    private ImageView map;
    private ImageView draw;


    private OnFragmentInteractionListener mListener;

    public ARViewFragment() {
    }

    public static ARViewFragment newInstance() {
        ARViewFragment fragment = new ARViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arview, container, false);

        setupFirebaseAuth();
        setUpARViewWedget(view);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Build a renderable from a 2D View.
        CompletableFuture<ViewRenderable> Layout =
                ViewRenderable.builder()
                        .setView(getActivity(), R.layout.layout_arview)
                        .build();



        CompletableFuture.allOf(
                Layout)
                .handle(
                        (notUsed, throwable) -> {
                            // When you build a Renderable, Sceneform loads its resources in the background while
                            // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                            // before calling get().

                            if (throwable != null) {
                                DemoUtils.displayError(getActivity(), "Unable to load renderables", throwable);
                                return null;
                            }

                            try {
                                LayoutRenderable = Layout.get();
                                hasFinishedLoading = true;

                            } catch (InterruptedException | ExecutionException ex) {
                                DemoUtils.displayError(getActivity(), "Unable to load renderables", ex);
                            }

                            return null;
                        });


        // Set an update listener on the Scene that will hide the loading message once a Plane is
        // detected.
        arSceneView
                .getScene()
                .setOnUpdateListener(
                        frameTime -> {
                            if (!hasFinishedLoading) {
                                return;
                            }

                            if (locationScene == null) {
                                // If our locationScene object hasn't been setup yet, this is a good time to do it
                                // We know that here, the AR components have been initiated.
                                locationScene = new LocationScene(getContext(), getActivity(), arSceneView);

                                location();
                            }

                            Frame frame = arSceneView.getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            if (locationScene != null) {
                                locationScene.processFrame(frame);
                            }


                        });


        // Lastly request CAMERA & fine location permission which is required by ARCore-Location.
        ARLocationPermissionHelper.requestPermission(getActivity());


        return view;
    }



    LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void location(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());


    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getContext() != null) {

                        getPostsLocation(location);

                }
            }
        }
    };


    private void getPostsLocation(Location lastlocation) {


        DatabaseReference GRefP = FirebaseDatabase.getInstance().getReference().child("postsLocations").child("public");

        GRefP.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {

                    Log.e(TAG, "key " + singleSnapshot.getKey());
                    getPostLocation(singleSnapshot.getKey());

                }
                setupLocationMarker(null, null, true);
                Log.e(TAG, "key END *********** ");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostLocation(String PostId){
        Location postLocation = new Location(LocationManager.GPS_PROVIDER);

        DatabaseReference GRefP = FirebaseDatabase.getInstance().getReference().child("postsLocations").child("public");

        GeoFire geoFirel = new GeoFire(GRefP);
        geoFirel.getLocation(PostId, new com.firebase.geofire.LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    postLocation.setLatitude(location.latitude);
                    postLocation.setLongitude(location.longitude);

                    Log.e(TAG, "key location" + postLocation.toString());
                    setupLocationMarker(postLocation, PostId, false);
                }else
                    Log.e(TAG, "key not find ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    boolean once = true;
    int count = 0;
    List<Location> locationList =new ArrayList<>();
    List<String> postsIdList = new ArrayList<>();
    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupLocationMarker(Location Location, String PostID, boolean finish){

        count++;
        Log.e(TAG, "key  entered" + count + " " + PostID + " l " + Location);

        if (!finish){
            locationList.add(Location);
            postsIdList.add(PostID);
        }
        else if (finish && once && locationList.size() != 0){
            once = false;

            for (int i =0 ; i < locationList.size() ; i++) {


                Location location = locationList.get(i);
                String postId = postsIdList.get(i);


                DatabaseReference Postsinfo = FirebaseDatabase.getInstance().getReference().child("posts").child("public").child(postId);


                Postsinfo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String OwnerID = dataSnapshot.child("ownerId").getValue(String.class);

                        LocationMarker locationMarker = new LocationMarker(
                                location.getLongitude(),
                                location.getLatitude(),
                                getExampleView(postId, OwnerID)
                        );


                        // An example "onRender" event, called every frame
                        // Updates the layout with the markers distance
                        locationMarker.setRenderEvent(new LocationNodeRender() {
                            @Override
                            public void render(LocationNode node) {
                                View eView = LayoutRenderable.getView();
                                TextView distanceTextView = eView.findViewById(R.id.distance);
                                ImageView postImage =  eView.findViewById(R.id.postImage);

                                distanceTextView.setText(distance(node.getDistance()));


                                if ( node.getDistance() < 3){
                                    postImage.setVisibility(View.VISIBLE);
                                    distanceTextView.setText("");

                                }else{
                                    postImage.setVisibility(View.GONE);
                                    distanceTextView.setText(distance(node.getDistance()));
                                }


                                TextView postName = eView.findViewById(R.id.postName);
                                postName.setText(dataSnapshot.child("postName").getValue(String.class));

                                if (dataSnapshot.child("postImage").exists()){

                                    Uri uri2 = Uri.parse(dataSnapshot.child("postImage").getValue(String.class));
                                    Glide.with(getActivity())
                                            .load(uri2)
                                            .into(postImage);
                                }


                                DatabaseReference ProfilePhoto = FirebaseDatabase.getInstance().getReference().child("profile").child(dataSnapshot.child("ownerId").getValue(String.class));

                                ProfilePhoto.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Uri uri = Uri.parse(dataSnapshot.child("profilePhoto").getValue(String.class));
                                        CircleImageView profilePhoto = eView.findViewById(R.id.profile_photo);

                                        Glide.with(getActivity())
                                                .load(uri)
                                                .into(profilePhoto);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                            }
                        });

                        locationScene.mLocationMarkers.add(locationMarker);


                        Log.e (TAG, "locationScene "+ locationScene.mLocationMarkers.size() );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        }

    }

    private String distance(int distanceM){
        String StrDistance ;

        if (distanceM < 1000){
            // in m
            StrDistance = String.valueOf(distanceM)+ " m";

        }else {
            // in km
            double distanceKM = distanceM/ 1000;
            StrDistance = distanceKM + " Km";

        }

        return StrDistance;
    }

    /**
     * Example node of a layout
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Node getExampleView( String PostID, String OwnerId) {
        Node base = new Node();
        base.setRenderable(LayoutRenderable);
        Context c = getContext();
        // Add  listeners etc here
        View eView = LayoutRenderable.getView();
        eView.setOnTouchListener((v, event) -> {

            openPostDetails(PostID, OwnerId);

            return true;
        });

        return base;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void openPostDetails(String postID, String ownerId){
        PostDetailsFragment fragment = PostDetailsFragment.newInstance(ownerId, postID,"falsetrue");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.remove(fragment);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


    /**
     * Make sure we call locationScene.resume();
     */
    @Override
    public void onResume() {
        super.onResume();

        if (locationScene != null) {
            locationScene.resume();
        }

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = DemoUtils.createArSession(getActivity(), installRequested);
                if (session == null) {
                    installRequested = ARLocationPermissionHelper.hasPermission(getActivity());
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                DemoUtils.handleSessionException(getActivity(), e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            DemoUtils.displayError(getContext(), "Unable to get camera", ex);
            //finish();
            return;
        }

    }

    /**
     * Make sure we call locationScene.pause();
     */
    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPause() {
        super.onPause();

        if (locationScene != null) {
            locationScene.pause();
        }
        arSceneView.pause();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        arSceneView.destroy();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!ARLocationPermissionHelper.hasPermission(getActivity())) {
            if (!ARLocationPermissionHelper.shouldShowRequestPermissionRationale(getActivity())) {
                // Permission denied with checking "Do not ask again".
                ARLocationPermissionHelper.launchPermissionSettings(getActivity());
            } else {
                Toast.makeText(
                        getContext(), "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }



     /*
    ------------------------------------ wedget ---------------------------------------------
     */

     private void setUpARViewWedget(View view){
         map = view.findViewById(R.id.map);
         draw = (ImageView) view.findViewById(R.id.addPost);
         arSceneView = view.findViewById(R.id.ar_scene_view);


         map.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(getActivity(), MapsActivity.class);
                 startActivity(intent);
             }
         });


         draw.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                 View mView = getLayoutInflater().inflate(R.layout.dialog_drawing2dor3d, null);

                 TextView drawing3d = (TextView) mView.findViewById(R.id.draw3d);
                 TextView drawing2d = (TextView) mView.findViewById(R.id.draw2d);


                 drawing3d.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         startNewActivity(getActivity(),"com.googlecreativelab.drawar");
                     }
                 });
                 drawing2d.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         Intent intent = new Intent(getActivity(), DrawActivity.class);
                         startActivity(intent);

                     }
                 });

                 mBuilder.setView(mView);
                 AlertDialog dialog = mBuilder.create();
                 dialog.show();


                /*
                PostSettingFragment fragment = PostSettingFragment.newInstance();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                transaction.addToBackStack(null);
                transaction.remove(fragment);
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();*/
             }
         });

     }


    public void startNewActivity(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            // We found the activity now start the activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + packageName));
            context.startActivity(intent);
        }
    }

         /*
    ------------------------------------ wedget ---------------------------------------------
     */

     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

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


    /******************************************************************/

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
