package com.example.fypapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.fypapp.directionhelpers.FetchURL;
import com.example.fypapp.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.fypapp.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


//NOTE : WITH EMULATOR YOU HAVE TO SET THE LOCATION OF APP
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private DatabaseReference databaseReference;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;
    private EditText editTextLatitude;
    private EditText editTextLongitude;

    private static MarkerOptions place1, place2;
    private static int counter =0;

    Button getDrivingDirection;
    Button getWalkingDirection;
    //Button getTransitDirection;
    Button clearBtn;
    private Polyline currentPolyline;


    private Geocoder geocoder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        //ActivityCompat.requestPermissions(this, new String[](Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PackageManager.PERMISSION_GRANTED);

        editTextLatitude = findViewById(R.id.editTextTextPersonName);
        editTextLongitude = findViewById(R.id.editTextTextPersonName2);

        databaseReference = FirebaseDatabase.getInstance().getReference("Location");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //show it on the map after data has changed from DB Side + move camera to marker
                try {
                    String databaseLatitudeString = snapshot.child("latitude").getValue().toString().substring(1,snapshot.child("latitude").getValue().toString().length()-1);
                    String databaseLongitudeString = snapshot.child("longitude").getValue().toString().substring(1,snapshot.child("longitude").getValue().toString().length()-1);

                    String[] stringLat = databaseLatitudeString.split(", ");
                    Arrays.sort(stringLat);
                    //latest Latitude
                    String latitude = stringLat[stringLat.length-1].split("=")[1];

                    String[] stringLong = databaseLongitudeString.split(", ");
                    Arrays.sort(stringLong);
                    //latest Longitude
                    String longitude = stringLong[stringLong.length-1].split("=")[1];

                    //create the latitude longitude object
                    LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                    //updating the text fields (indicating location of user) from admin side
                    editTextLatitude.setText(latitude);
                    editTextLongitude.setText(longitude);

                    //zoom camera in on position
                    pointToPosition(latLng);


                    //show it on the map after data has changed from DB Side + move camera to marker
                    mMap.addMarker(new MarkerOptions().position(latLng).title(latitude + ", " + longitude));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //get the 3 path types from place 1 to place 2
        getDrivingDirection = findViewById(R.id.btnDrivingPath);
        getDrivingDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FetchURL(MapsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
            }
        });

        getWalkingDirection = findViewById(R.id.btnWalkingPath);
        getWalkingDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FetchURL(MapsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "walking"), "walking");
            }
        });



        //27.658143,85.3199503
        //27.667491,85.3208583
        //place1 = new MarkerOptions().position(new LatLng(34.13054591944336, 35.66420305520296)).title("Location 1");
        //place2 = new MarkerOptions().position(new LatLng(34.12530657196116, 35.65238390117884)).title("Location 2");


        //clear map
        clearBtn = findViewById(R.id.btnClear);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
            }
        });


        geocoder = new Geocoder(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//         Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

//        this deletes the marker by clicking on it
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                marker.remove();
                return true;
            }
        });


        //doesnt work cause we click on it to remove it
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(@NonNull LatLng latLng) {
//                //Coordinates of click
//                LatLng new_pos = new LatLng(latLng.latitude, latLng.longitude);
//
//                //Needs to be user-editable through an activity or pop up window
//                String name = "name";
//                String snippet = "snippet";
//
//                mMap.addMarker(new MarkerOptions()
//                        .position(new_pos)
//                        .title(name)
//                        .snippet(snippet));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            }
//        });


        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        Log.d("mylog", "Added Markers");

        pointToPosition(new LatLng(34.1157833994, 35.6743240356));




    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyCdA8rXLJ_48ckzOFaVmUHu5k1OqJYBVHw";
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Log.d(TAG, "onMapLongClick: " + latLng.toString());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                Log.d(TAG, "onMapLongClick: " + latLng.toString());
                Log.d(TAG, "counter: " + counter+1 );
//                String streetAddress = address.getAddressLine(0);
//                mMap.addMarker(new MarkerOptions()
//                        .position(latLng)
//                        .title(streetAddress)
//                        .draggable(true)
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
//                );
                counter++;
                //counter is odd (1st/3rd/5th... press)
                if(counter%2 !=0){
//                    //this if statement just considers that we already have an *old place1 marker*, if so, make it invisible until it changes its location (2 lines later) : *new place 1 marker*
//                    if(place1.getTitle()=="Start"){
//                        place1.visible(false);
//                    }
                    place1 = new MarkerOptions().position(latLng).title("Start").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    Log.d(TAG, "onMapLongClick1: PLACE1 is set");
                    mMap.addMarker(place1);
                }
                //counter is even (2nd/4th/6th... press)
                if(counter%2 ==0){
//                    if(place2.getTitle()=="Start"){
//                        place2.visible(false);
//                    }
                    place2 = new MarkerOptions().position(latLng).title("Destination").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    Log.d(TAG, "onMapLongClick1: PLACE2 is set");
                    mMap.addMarker(place2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
        Log.d(TAG, "onMarkerDrag: ");
    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        Log.d(TAG, "onMarkerDragEnd: ");
        LatLng latLng = marker.getPosition();
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                marker.setTitle(streetAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
        Log.d(TAG, "onMarkerDragStart: ");
    }

    private void pointToPosition(LatLng position) {
        //Build camera position
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(20).build();
        //Zoom in and animate the camera.
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}