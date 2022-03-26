package com.example.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Locale;

public class UserActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private DatabaseReference databaseReference;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;
    private EditText editTextLatitude;
    private EditText editTextLongitude;


    //Text to Speech Part
    private TextToSpeech mTTS;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeed;

    //for root of DB
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        //ActivityCompat.requestPermissions(this, new String[](Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PackageManager.PERMISSION_GRANTED);

        editTextLatitude = findViewById(R.id.editTextTextPersonName);
        editTextLongitude = findViewById(R.id.editTextTextPersonName2);



        // Getting the name at user registry to input as Database root  !!!!! THE METHOD createUserWithEmailAndPassword of Firebase already checks if I have multiple users with same email so MIX doesnt happen

        String userEmail = Signer.INSTANCE.getUsername();
        String root = userEmail.substring( 0, userEmail.indexOf("@"));
        databaseReference = FirebaseDatabase.getInstance().getReference(root);

        //updates Text field and DB automatically (input: GPS location ,, destination:user TF / Firebase DB / admin TF)
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    editTextLatitude.setText(Double.toString(location.getLatitude()));
                    editTextLongitude.setText(Double.toString(location.getLongitude()));
                    databaseReference.child("latitude").push().setValue(editTextLatitude.getText().toString());
                    databaseReference.child("longitude").push().setValue(editTextLongitude.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);

        }
        catch (Exception e){
            e.printStackTrace();
        }



        //Text To Speech part
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        mSeekBarPitch = findViewById(R.id.PitchBar);
        mSeekBarSpeed = findViewById(R.id.SpeedBar);

        //implement on Data Change of TextFlag to automatically say the string
        speak(root);  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    }

    // getting lat / long from text fields + pushing child infos (latitude, longitude) to the Location Node of the DB
    //updates  DB manually (input: user TF ,, destination: Firebase DB / admin TF)
    public void updateButtonOnClick(View view){

        databaseReference.child("latitude").push().setValue(editTextLatitude.getText().toString());
        databaseReference.child("longitude").push().setValue(editTextLongitude.getText().toString());

    }


    //For Text to Speech
    private void speak(String username) { //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        //just to be said first time on create
        final String[] text = {"Hello I am your assistant for today"}; //replace by data got from DB
        mTTS.speak(text[0],TextToSpeech.QUEUE_FLUSH,null, null);

        databaseReference = FirebaseDatabase.getInstance().getReference(username); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


        //  To be said everytime TextFlag value changes

        //!!!!!!!!!!ON DATA CHANGE OF CHILD NODE TEXT FLAG ONLY
        // to not affect long and latitude
        databaseReference.child("TextFlag").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            //show it on the map after data has changed from DB Side + move camera to marker
            try {
                String textFlag = snapshot.getValue().toString();
                Log.d("mylog", textFlag);

                switch(textFlag) {
                    case "1":
                        text[0] = "1 near range object to the left";
                        break;
                    case "2":
                        text[0] = "1 near range object in front";
                        break;
                    case "3":
                        text[0] = "1 near range object to the right";
                        break;
                    case "4":
                        text[0] = "1 medium range object to the left";
                        break;
                    case "5":
                        text[0] = "1 medium range object in front";
                        break;
                    case "6":
                        text[0] = "1 medium range object to the right";
                        break;
                    case "7":
                        text[0] = "1 far range object to the left";
                        break;
                    case "8":
                        text[0] = "1 far range object in front";
                        break;
                    case "9":
                        text[0] = "1 far range object to the right";
                        break;
                    case "10":
                        text[0] = "2 near range objects to the front and right";
                        break;
                    case "11":
                        text[0] = "2 medium range objects to the front and right";
                        break;
                    case "12":
                        text[0] = "2 far range objects to the front and right";
                        break;
                    case "13":
                        text[0] = "2 near range objects to the left and right";
                        break;
                    case "14":
                        text[0] = "2 medium range objects to the left and right";
                        break;
                    case "15":
                        text[0] = "2 far range objects to the left and right";
                        break;
                    case "16":
                        text[0] = "2 near range objects to the left and front";
                        break;
                    case "17":
                        text[0] = "2 medium range objects to the left and front";
                        break;
                    case "18":
                        text[0] = "2 far range objects to the left and front";
                        break;
                    case "19":
                        text[0] = "many objects in near range";
                        break;
                    case "20":
                        text[0] = "many objects in medium range";
                        break;
                    case "21":
                        text[0] = "many objects in far range";
                        break;
                    default:
                        text[0] = "No objects";
                }


                //Pitch and Speed Controlled by the Bars
                float pitch = (float) mSeekBarPitch.getProgress() / 50;
                if (pitch < 0.1) pitch = 0.1f;
                float speed = (float) mSeekBarSpeed.getProgress() / 50;
                if (speed < 0.1) speed = 0.1f;

                mTTS.setPitch(pitch);
                mTTS.setSpeechRate(speed);

                //same command but for different device versions

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.d("mylog1", text[0]);
                    mTTS.speak(text[0], TextToSpeech.QUEUE_FLUSH, null, null);
                }
                else {
                    mTTS.speak(text[0], TextToSpeech.QUEUE_FLUSH, null);
                }

                Log.d("mylog2", text[0]);
                mTTS.speak(text[0],TextToSpeech.QUEUE_FLUSH,null, null);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    //Text  to Speech : on exit : destroy engine
    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }

    //Text to Speech : On create : initialize engine with these configrations
    @Override
    public void onInit(int i) {


        if (i == TextToSpeech.SUCCESS) {
            //Setting speech Language
            mTTS.setLanguage(Locale.ENGLISH);
            mTTS.setPitch(1);
        }
    }
}