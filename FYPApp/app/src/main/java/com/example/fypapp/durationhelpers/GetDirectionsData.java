package com.example.fypapp.durationhelpers;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
//import com.google.maps.android.PolyUtil;
import java.io.IOException;
import java.util.HashMap;


public class GetDirectionsData extends AsyncTask<Object,String,String> {

    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    String duration, distance;
    LatLng latLng;


    //my variables
    TextView DurationText,DistanceText;
    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];
        latLng = (LatLng)objects[2];
        DurationText=(TextView) objects[3];
        DistanceText=(TextView) objects[4];



        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleDirectionsData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {
        HashMap<String, String> directionsList = null;
        DataParser2 parser = new DataParser2();
        directionsList = parser.parseDirections(s);
        duration = directionsList.get("duration");
        distance = directionsList.get("distance");

        //For more precise values
        String stripedValue = (duration.replaceAll("[\\s+a-zA-Z :]",""));
        double dbl = Double.parseDouble(stripedValue);
        double duration_fixed = dbl*2.5;

        String stripedValue2 = (duration.replaceAll("[\\s+a-zA-Z :]",""));
        double dbl2 = Double.parseDouble(stripedValue2);
        double distance_fixed = dbl2;

        Log.d("mylog", "woslit lahon");
        DurationText.setText(duration_fixed + " Km");
        DistanceText.setText(distance_fixed + " Minutes");

    }
}

