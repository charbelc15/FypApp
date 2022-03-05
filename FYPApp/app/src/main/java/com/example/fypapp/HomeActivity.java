package com.example.fypapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageButton userBtn = findViewById(R.id.user);
        userBtn.setOnClickListener( v ->
        {
            Intent intent = new Intent( HomeActivity.this , UserActivity.class );
            startActivity(intent);
        });

        ImageButton adminBtn = findViewById(R.id.admin);
        adminBtn.setOnClickListener( v ->
        {
            Intent intent = new Intent( HomeActivity.this , MapsActivity.class );
            startActivity(intent);
        });
    }
}