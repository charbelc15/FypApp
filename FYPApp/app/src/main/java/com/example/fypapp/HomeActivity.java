package com.example.fypapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    public FloatingActionButton logout_Btn;
    public TextView DisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // To Log Out
        FirebaseAuth.getInstance().signOut();
        logout_Btn = findViewById(R.id.logout_btn);
        logout_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( HomeActivity.this , LogInActivity.class );
                startActivity(intent);
            }
        });

        // extra sent to this activity from LogInActivity with the user's display name
        Bundle extras = getIntent().getExtras();
        String userEmail = extras.getString("username");
        String userDisplayName = userEmail.substring( 0, userEmail.indexOf("@")); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        DisplayName = findViewById(R.id.DisplayName);
        DisplayName.append(userDisplayName + "!");




        ImageButton userBtn = findViewById(R.id.user);
        userBtn.setOnClickListener( v ->
        {
            Intent intent = new Intent( HomeActivity.this , UserActivity.class );
            intent.putExtra("userEmail", userEmail); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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