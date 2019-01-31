package com.example.arview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void chat (View view){
        Intent i = new Intent(MainActivity.this, ProfileActivity.class);
        i.setAction(Intent.ACTION_EDIT);
        startActivity(i);
    }
}
