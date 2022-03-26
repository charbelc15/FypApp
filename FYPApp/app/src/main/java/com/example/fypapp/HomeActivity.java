package com.example.fypapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    public FloatingActionButton logout_Btn;
    public TextView DisplayName;
    private FirebaseAuth mAuth;

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

        // from LogInActivity (Regsiter Fragment/LogInFragment) with the user's display name
        try {
            String userEmail = Signer.INSTANCE.getUsername();
            String userDisplayName = userEmail.substring( 0, userEmail.indexOf("@"));
            DisplayName = findViewById(R.id.DisplayName);
            DisplayName.append(userDisplayName + "!");
        } catch (Exception e) {
            e.printStackTrace();
        }





        ImageButton userBtn = findViewById(R.id.user);
        userBtn.setOnClickListener( v ->
        {
            Intent intent = new Intent( HomeActivity.this , UserActivity.class );
            //intent.putExtra("userEmail", userEmail); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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