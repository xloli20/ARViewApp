package com.example.arview;

import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.arview.post.PostSettingFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.appcompat.app.AppCompatActivity;


public class DrawActivity extends AppCompatActivity {

    drawing canvas;
    private StorageReference storageReference;

    private ImageView send;
    private ImageView clear;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        canvas = findViewById(R.id.draw);

        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        /*Uri uri = Uri.fromFile(new File()));
        StorageReference storageReference1 = storageReference.child(userId+"/Drawings2D"+drawName+".jpg");
        storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });*/

        clear = (ImageView) findViewById(R.id.trash);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvas.clearCanvas();
            }
        });

        send = (ImageView) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screenShot(canvas);

                PostSettingFragment fragment = new PostSettingFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();
            }
        });

    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }



}
