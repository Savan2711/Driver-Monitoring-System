package com.example.admindrivermonitoring;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.FragmentActivity;

//firebase connection imports
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    MarkerOptions origin, destination;

    //references for firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private TextView srcLatTextView;
    private TextView srcLonTextView;
    private TextView dstLatTextView;
    private TextView dstLonTextView;
//    private TextView startTimeTextView;
//    private TextView endTimeTextView;
//    private TextView durationTextView;
//    private TextView yawnCountTextView;
//    private TextView eyeCountTextView;


    String distance;
    String duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Setting marker to draw route between these two points
        origin = new MarkerOptions().position(new LatLng(20.2497076, 72.7665945)).title("Gujarat").snippet("origin");
        destination = new MarkerOptions().position(new LatLng(20.2538455, 72.7554095)).title("Bellandur").snippet("destination");
//        destination = new MarkerOptions().position(new LatLng(12.9304, 77.6784)).title("Bellandur").snippet("destination");

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin.getPosition(), destination.getPosition());

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

        Log.d("URl : ", url);

        /*********************************************************************/
        //For firebase connection

        // below line is used to get the instance
        // of our Firebase database.
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get
        // reference for our database.
        databaseReference = firebaseDatabase.getReference("Data");

        // initializing our object class variable.
        srcLatTextView = findViewById(R.id.srcLat);
        srcLonTextView = findViewById(R.id.srcLon);
        dstLatTextView = findViewById(R.id.dstLat);
        dstLonTextView = findViewById(R.id.dstLon);
//        startTimeTextView = findViewById(R.id.startTime);
//        endTimeTextView = findViewById(R.id.endTime);
//        durationTextView = findViewById(R.id.duration);
//        yawnCountTextView = findViewById(R.id.yawnCount);
//        eyeCountTextView = findViewById(R.id.eyeCount);

        // calling method
        // for getting data.
        getdata();
    }

    public void showRating(View view)
    {
        Intent intent = new Intent(this, RatingActivity.class);
//        duration = "10";
        intent.putExtra("idealDuration", duration);
        startActivity(intent);
    }

    private void getdata() {

        // calling add value event listener method
        // for getting the values from database.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // this method is call to get the realtime
                // updates in the data.
                // this method is called when the data is
                // changed in our Firebase console.
                // below line is for getting the data from
                // snapshot of our database.
                String srcLat = snapshot.child("Source latitude").getValue().toString();
                String srcLon = snapshot.child("Source longitude").getValue().toString();
                String dstLat = snapshot.child("Destination latitude").getValue().toString();
                String dstLon= snapshot.child("Destination longitude").getValue().toString();
//                String startTime = snapshot.child("Start Time").getValue().toString();
//                String endTime = snapshot.child("End Time").getValue().toString();
//                String duration = snapshot.child("Time Diffrence").getValue().toString();
//                String yawnCount = snapshot.child("Yawn Counter").getValue().toString();
//                String eyeCount = snapshot.child("Eye Counter").getValue().toString();


                // after getting the value we are setting
                // our value to our text view in below line.
                srcLatTextView.setText("Source Lattitude : " + srcLat);
                srcLonTextView.setText("Source Longitude : " + srcLon);
                dstLatTextView.setText("Destination Latitute : " + dstLat);
                dstLonTextView.setText("Destination Longitude : " + dstLon);
//                startTimeTextView.setText("Start Time : " + startTime);
//                endTimeTextView.setText("End Time : " + endTime);
//                durationTextView.setText("Travelled Time : " + duration);
//                yawnCountTextView.setText("Yawn Count : " + yawnCount);
//                eyeCountTextView.setText("Eye Count : " + eyeCount);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(MapsActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(origin);
        mMap.addMarker(destination);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin.getPosition(), 10));


    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


    /**
     * A class to parse the JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = new ArrayList();
            PolylineOptions lineOptions = new PolylineOptions();

            for (int i = 0; i < result.size(); i++) {

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route

            if (points.size() != 0)
                mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        //setting transportation mode
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyC4rGfDtyusXvx18MXaQbVUyONq6CB7E8w";


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("Data =", "url"+ data.toString());
            br.close();

            JSONObject array = new JSONObject(data);

            String distance;
            String duration;
            distance = array.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
            duration = array.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("value");

//            duration = String.valueOf(292);
            Log.d("distance : ", distance);
            Log.d("duration : ", duration); // second :  71163, 292

//            Toast.makeText(MapsActivity.this, "It runs", Toast.LENGTH_LONG);

            /*
            * src lat : 20.2497076, lon : 72.7665945
            * destn lat : 20.2538455, lon : 72.7554095
            * duration : 292
            * distance : 1.5 km
            * */

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}

