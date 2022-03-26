package com.example.fypapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LogInActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //check if user already has logged in
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            //user is still logged in --> GO TO Home Page
            String username = currentUser.getEmail();
            Signer.INSTANCE.setUsername(username);
            Intent intent = new Intent(this, HomeActivity.class);
            //intent.putExtra("username", currentUser.getEmail());
            Log.d("STATUS still logged in ", currentUser.getEmail());
            startActivity(intent);
        }else{
            //user is no longer logged in --> replace this activity's         fragment container's content        by the    LogInFragment
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, new LoginFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }




        getSupportFragmentManager().beginTransaction().add(R.id.container,LoginFragment.class,null).commit();

    }

}