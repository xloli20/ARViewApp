package com.example.arview.location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.arview.R;
import com.example.arview.databaseClasses.nearPost;
import com.example.arview.databaseClasses.post;
import com.example.arview.friend.FriendsPostRecyclerViewAdapter;
import com.example.arview.login.SiginActivity;
import com.example.arview.main.MainActivity;
import com.example.arview.post.PostDetailsFragment;
import com.example.arview.profile.ProfileFragment;
import com.example.arview.utils.FirebaseMethods;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,
                                                                ProfileFragment.OnFragmentInteractionListener,
                                                                PostDetailsFragment.OnFragmentInteractionListener {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;

    private LatLng pickupLocation;

    //wedgets
    private ImageView upArrow, downArrow, refresh;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private ArrayList<nearPost> nearPostsList = new ArrayList<>();

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseMethods firebaseMethods;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupFirebaseAuth();
        initWedjets();
    }

    // get intent location zoom to it

    //
    // get close posts .....................

    List<Marker> markers = new ArrayList<Marker>();

    private void getPostsAround() {
        DatabaseReference PostsLocation = FirebaseDatabase.getInstance().getReference().child("postsLocations").child("public");

        GeoFire geoFire = new GeoFire(PostsLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLongitude(), mLastLocation.getLatitude()), 10000);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {

                Log.e(TAG, "getPostsAround.onKeyEntered : key " + key);

                for (Marker markerIt : markers) {
                    if (markerIt.getTag().equals(key))
                        return;
                }

                final nearPost nearPost = new nearPost();

                nearPost.setPost(new post());
                nearPost.getPost().setPostId(key);

                Location postLocation = new Location(LocationManager.GPS_PROVIDER);
                postLocation.setLatitude(location.latitude);
                postLocation.setLongitude(location.longitude);

                float Distance = mLastLocation.distanceTo(postLocation) / 1000;  //km
                int dotIndex = String.valueOf(Distance).indexOf(".");
                String distance = String.valueOf(Distance).substring(0, dotIndex + 3);

                nearPost.setDestinace(distance + " km");

                DatabaseReference Postsinfo = FirebaseDatabase.getInstance().getReference().child("posts").child("public").child(key);

                Postsinfo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){
                            nearPost.getPost().setOwnerId(dataSnapshot.child("ownerId").getValue(String.class));
                            nearPost.getPost().setPostName(dataSnapshot.child("postName").getValue(String.class));
                            nearPost.getPost().setLikes(String.valueOf(dataSnapshot.child("likes").getChildrenCount()));
                            nearPost.getPost().setVisibilty(dataSnapshot.child("visibilty").getValue(Boolean.class));
                            nearPost.getPost().setPersonal(dataSnapshot.child("personal").getValue(Boolean.class));

                            DatabaseReference OwnerREf = FirebaseDatabase.getInstance().getReference().child("profile").child(nearPost.getPost().getOwnerId());
                            OwnerREf.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                        if (map.get("name") != null) {
                                            nearPost.setOwnerName(map.get("name").toString());
                                        }
                                        if (map.get("profilePhoto") != null) {
                                            nearPost.setProfilePhoto(map.get("profilePhoto").toString());
                                        }

                                        final LatLng PostLocation = new LatLng(location.latitude, location.longitude);
                                        nearPost.setLocation(PostLocation);

                                        final Uri uri = Uri.parse(nearPost.getProfilePhoto());

                                        Glide.with(getApplicationContext()).asBitmap()
                                                .load(uri)
                                                .into(new SimpleTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                                                        Bitmap icone = Bitmap.createScaledBitmap(resource, 70, 70, false);

                                                        Marker mPostMarker = mMap.addMarker(new MarkerOptions().position(PostLocation)
                                                                .title(nearPost.getPost().getPostName())
                                                                .icon(BitmapDescriptorFactory.fromBitmap(icone)));

                                                        mPostMarker.setTag(nearPost.getPost().getPostName());

                                                        markers.add(mPostMarker);
                                                    }
                                                });
                                    }

                                    nearPostsList.add(nearPost);

                                    Collections.sort(nearPostsList, new Comparator<nearPost>() {
                                        public int compare(nearPost o1, nearPost o2) {
                                            return o1.getDestinace().compareTo(o2.getDestinace());
                                        }
                                    });

                                    adapter.notifyDataSetChanged();

                                    for (int i = 0; i < nearPostsList.size(); i++) {
                                        if (nearPostsList.get(i).getPost().getPostId().equals(nearPost.getPost().getPostId())) {

                                            if (nearPostsList.get(i).getPost().getLikes() != nearPost.getPost().getLikes()) {
                                                Log.e(TAG, "diff ");
                                                nearPostsList.set(i, nearPost);
                                                nearPostsList.remove(i + 1);
                                                adapter.notifyItemRangeChanged(i, nearPostsList.size() - 1);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                for (Marker markerIt : markers) {
                    if (markerIt.getTag().equals(key)) {
                        markerIt.remove();
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker markerIt : markers) {
                    if (markerIt.getTag().equals(key)) {
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        });
    }

    public void moveCam(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(9));
    }

    // ..............................
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            } else {
                checkLocationPermission();
            }
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    mLastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    //use GeoFirebase
                    String userId = mAuth.getCurrentUser().getUid();
                    DatabaseReference GRef = mFirebaseDatabase.getInstance().getReference().child("UsersLocation");

                    GeoFire geoFire = new GeoFire(GRef);
                    geoFire.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()), new
                            GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    Log.e(TAG, "GeoFire Complete");
                                }
                            });

                    pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                    refresh.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nearPostsList.clear();
                            getPostsAround();
                        }
                    });
                }
            }
        }
    };

    //  Permission ........................

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    /*
   ------------------------------------ widgets -------------------------------------------
   */
    private void initWedjets() {
        upArrow = findViewById(R.id.upArrow);
        downArrow = findViewById(R.id.downArrow);
        recyclerView = findViewById(R.id.recyclerView);
        refresh = findViewById(R.id.refresh);

        initiRecyclerView();

        recyclerView.setVisibility(View.INVISIBLE);
        downArrow.setVisibility(View.INVISIBLE);
        upArrow.setVisibility(View.VISIBLE);

        upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                upArrow.setVisibility(View.INVISIBLE);
                downArrow.setVisibility(View.VISIBLE);
            }
        });

        downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.INVISIBLE);
                downArrow.setVisibility(View.INVISIBLE);
                upArrow.setVisibility(View.VISIBLE);
            }
        });
    }

    public void initiRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(this, nearPostsList, mAuth.getUid());
        recyclerView.setAdapter(adapter);
    }
      /*
    ------------------------------------ widgets --------------------------------------------
    */

    /*
 ------------------------------------ Firebase ---------------------------------------------
  */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(this);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(MapsActivity.this, SiginActivity.class);
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
        // GeoFirebase
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference GRef = mFirebaseDatabase.getInstance().getReference("UsersLocation");

        GeoFire geoFire = new GeoFire(GRef);
        geoFire.removeLocation(userId, new
                GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        Log.e(TAG, "GeoFire Complete");
                    }
                });
    }

    /*
    ------------------------------------ Firebase -----------
    */
    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }

    public void OnFragmentInteractionListener(Uri uri) {
        //you can leave it empty
    }
}
