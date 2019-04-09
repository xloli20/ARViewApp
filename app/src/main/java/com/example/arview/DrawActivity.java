package com.example.arview;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.arview.main.ARViewAddFragment;
import com.example.arview.post.PostSettingFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.appcompat.app.AppCompatActivity;


public class DrawActivity extends AppCompatActivity implements ARViewAddFragment.OnFragmentInteractionListener{

    private static final String TAG = "drawActivity";

    drawing canvas;

    private static final int RC_PHOTO_PICKER = 2;


    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotosStorageReference;
    private FirebaseUser user;


    private ImageView send;
    private ImageView clear;
    private ImageView mPhotoPickerButton;
    private ImageView backArrow;


    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        canvas = findViewById(R.id.draw);

        backarrow();

        user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mPhotosStorageReference = mFirebaseStorage.getReference().child(userId).child("2dDrawings");


        clear = (ImageView) findViewById(R.id.trash);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvas.clearCanvas();
            }
        });

        mPhotoPickerButton = (ImageView)findViewById(R.id.image);
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        send = (ImageView) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = screenShot(canvas);


                String postImage = "https://firebasestorage.googleapis.com/v0/b/arview-b5eb3.appspot.com/o/LFvmo0NrcATUG4Y0O1IXNJrdw4a2%2FChats%2F-LaxU9NdBLoG5vpkfWiR%2Fimage%3A823?alt=media&token=d7a18e9d-d9fa-447b-9635-5caf2e6f423c";
                ARViewAddFragment fragment = ARViewAddFragment.newInstance(postImage);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                transaction.addToBackStack(null);
                transaction.remove(fragment);
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            // Get a reference to store file
            StorageReference photoRef = mPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When the image has successfully uploaded, we get its download URL
                            photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // getting image uri and converting into string
                                    Uri downloadUrl = uri;
                                    String postImage = downloadUrl.toString();

                                    ARViewAddFragment fragment = ARViewAddFragment.newInstance(postImage);
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                                    //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                                    transaction.addToBackStack(null);
                                    transaction.remove(fragment);
                                    transaction.replace(R.id.fragment_container, fragment);
                                    transaction.commit();
                                }
                            });
                        }
                    });
        }
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void backarrow(){
        backArrow = (ImageView) findViewById(R.id.backArrow);

        //setup the backarrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to 'ProfileActivity'");
                finish();
            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
