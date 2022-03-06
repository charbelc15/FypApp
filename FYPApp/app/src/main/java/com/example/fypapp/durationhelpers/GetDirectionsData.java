package com.example.fypapp.durationhelpers;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.fypapp.directionhelpers.DataParser;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
//import com.google.maps.android.PolyUtil;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.List;


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
        Log.d("mylog", "woslit lahon");
        DurationText.setText(duration);
        DistanceText.setText(distance);

    }

//    @Override
//    protected void onPostExecute(String s) {
//
//        String[] directionsList;
//        DataParser2 parser = new DataParser2();
//        directionsList = parser.parseDirections(s);
//        displayDirection(directionsList);
//
//    }

//    public void displayDirection(String[] directionsList)
//    {
//
//        int count = directionsList.length;
//        for(int i = 0;i<count;i++)
//        {
//            PolylineOptions options = new PolylineOptions();
//            options.color(Color.RED);
//            options.width(10);
//            options.addAll(PolyUtil.decode(directionsList[i]));
//
//            mMap.addPolyline(options);
//        }
//    }






}

