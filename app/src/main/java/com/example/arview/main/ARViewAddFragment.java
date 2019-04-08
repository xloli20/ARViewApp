package com.example.arview.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arview.DrawActivity;
import com.example.arview.R;
import com.example.arview.location.MapsActivity;
import com.example.arview.login.SiginActivity;
import com.example.arview.post.PostSettingFragment;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.rendering.LocationNode;
import uk.co.appoly.arcorelocation.rendering.LocationNodeRender;
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class ARViewAddFragment extends Fragment implements PostSettingFragment.OnFragmentInteractionListener {

    private static final String TAG = "ARViewAddFragment";

    private static final String ARG_PARAM1 = "param1";
    private String PostImage;


    private boolean installRequested;
    private boolean hasFinishedLoading = false;

    private ArSceneView arSceneView;

    // Renderables for this example
    private ViewRenderable LayoutRenderable;

    // Our ARCore-Location scene
    private LocationScene locationScene;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //wedget
    private RelativeLayout next, back;
    private ImageView unseen;


    private OnFragmentInteractionListener mListener;

    public ARViewAddFragment() {
    }

    public static ARViewAddFragment newInstance(String PostImage) {
        ARViewAddFragment fragment = new ARViewAddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, PostImage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            PostImage = getArguments().getString(ARG_PARAM1);
        }

    }


    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arview_add, container, false);

        setupFirebaseAuth();
        setUpARViewWedget(view);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());


        // Build a renderable from a 2D View.
        CompletableFuture<ViewRenderable> exampleLayout =
                ViewRenderable.builder()
                        .setView(getActivity(), R.layout.layout_arview_simple)
                        .build();


        CompletableFuture.allOf(
                exampleLayout)
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
                                LayoutRenderable = exampleLayout.get();
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

                    ARimage(location);

                }
            }
        }
    };


    LocationMarker locationMarker;
    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void ARimage(Location location){

        Location L = getLocation5m(location);

        locationMarker = new LocationMarker(
                L.getLongitude(),
                L.getLatitude(),
                getExampleView()
        );

        Log.e (TAG, "Location  getLongitude "+ location.getLongitude() );
        Log.e (TAG, "Location  getLatitude "+ location.getLatitude() );


        // An example "onRender" event, called every frame
        // Updates the layout with the markers distance
        locationMarker.setRenderEvent(new LocationNodeRender() {
            @Override
            public void render(LocationNode node) {
                View eView = LayoutRenderable.getView();
                ImageView postImage = eView.findViewById(R.id.postImage);
                Uri uri = Uri.parse(PostImage);
                Glide.with(getActivity())
                        .load(uri)
                        .into(postImage);


                Log.e (TAG, "distance "+ node.getDistance() );

            }});

        locationScene.clearMarkers();
        locationScene.mLocationMarkers.add(locationMarker);

        Log.e (TAG, "locationScene "+ locationScene.mLocationMarkers.size() );


    }

    private Location getLocation5m(Location location){


        double lat = location.getLatitude();
        double lon = location.getLongitude();

        //Earth’s radius, sphere
        double R=6378137;

        double dLat = 2/R;
        double dLon = 2/(R* Math.cos(Math.PI*lat/180));

        double latO = lat + dLat * 180/Math.PI;
        double lonO = lon + dLon * 180/Math.PI;

        Location newlocation = new Location(LocationManager.GPS_PROVIDER);
        newlocation.setLatitude(latO);
        newlocation.setLongitude(lonO);

        return newlocation;
    }





    /**
     * Example node of a layout
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Node getExampleView() {
        Node base = new Node();
        base.setRenderable(LayoutRenderable);
        Context c = getContext();
        // Add  listeners etc here
        View eView = LayoutRenderable.getView();
        eView.setOnTouchListener((v, event) -> {
            Toast.makeText(
                    c, "Location marker touched." , Toast.LENGTH_LONG)
                    .show();
            return false;
        });

        return base;
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

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
     private void setUpARViewWedget(View view){
         next = view.findViewById(R.id.next);
         back = view.findViewById(R.id.back);
         unseen = view.findViewById(R.id.unseen);
         arSceneView = view.findViewById(R.id.ar_scene_view);


         next.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                 //arSceneView.pause();

                 if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                         ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                     return;
                 }

                 mFusedLocationClient.getLastLocation()
                         .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                             @Override
                             public void onSuccess(Location location) {
                                 // Got last known location. In some rare situations this can be null.
                                 if (location != null) {
                                     Log.e (TAG, "getLastLocation "+ location );
                                     PostSettingFragment fragment = PostSettingFragment.newInstance(PostImage, location);
                                     FragmentManager fragmentManager = getFragmentManager();
                                     FragmentTransaction transaction = fragmentManager.beginTransaction();
                                     //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                                     transaction.addToBackStack(null);
                                     transaction.remove(fragment);
                                     transaction.replace(R.id.Sfragment_container, fragment);
                                     transaction.commit();
                                 }
                             }
                         });

             }
         });

         back.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 closefragment();
             }
         });

     }

    private void closefragment() {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        getActivity().getFragmentManager().popBackStack();
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
