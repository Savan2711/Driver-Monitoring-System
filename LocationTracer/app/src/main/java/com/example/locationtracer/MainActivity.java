package com.example.locationtracer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    // initializing
    // FusedLocationProviderClient
    // object
    FusedLocationProviderClient mFusedLocationClient;

    // Initializing other items
    // from layout file
    TextView latitudeTextView, longitTextView, isJourneyOnTextView;
    int PERMISSION_ID = 44;

    Handler handler;
    Runnable runnable;

    //For firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    int isJourneyOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isJourneyOn = 0;
        latitudeTextView = findViewById(R.id.latTextView);
        longitTextView = findViewById(R.id.lonTextView);
        isJourneyOnTextView = findViewById(R.id.textView3);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//        handler = new Handler(Looper.getMainLooper());

        //Firebase
        // instance of our FIrebase database.
        firebaseDatabase = FirebaseDatabase.getInstance();
        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("Coordinates");

        // method to get the location

//        new android.os.Handler(Looper.getMainLooper()).postDelayed(
//                new Runnable() {
//                    public void run() {
//                        //getLastLocation();
//                        Log.i("tag", "This'll run 5000 milliseconds later");
//                    }
//                },
//                300);

//        Handler handler = new Handler(Looper.getMainLooper());
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                // Do the task...
//                handler.postDelayed(this, 5000); //
//                getLastLocation();
//                Log.d("tag", "This'll run 5000 milliseconds later");
//            }
//        };
//        handler.postDelayed(runnable, 5000);
    }

    //start tracking(Method bind with start button)
    public void startTracking(View view)
    {
        isJourneyOn = 1;
        getLastLocation(1);
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                // Do the task...
//                handler.postDelayed(this, 5000);
//                getLastLocation();
//                Log.d("tag", "This'll run 5000 milliseconds later");
//            }
//        };
//        handler.postDelayed(runnable, 5000);
        Toast.makeText(MainActivity.this, "Tracking started", Toast.LENGTH_SHORT).show();
        Log.d("Check Start", "Start button clicked");
    }

    //stop tracking(Method bind with stop button)
    public void stopTracking(View view)
    {
        isJourneyOn = 0;

//        if(runnable != null) {
//
//            handler.removeCallbacks(runnable);
            Toast.makeText(MainActivity.this, "Tracking stopped", Toast.LENGTH_SHORT).show();
            Log.d("Check Stop", "Stop button clicked");
//        }
        getLastLocation(0);
//        handler.removeCallbacksAndMessages(null);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(int flag) {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        String lat, lon;

                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            lat = location.getLatitude() + "";
                            lon = location.getLongitude() + "";

                            latitudeTextView.setText(lat);
                            longitTextView.setText(lon);
//                            isJourneyOnTextView.setText(String.valueOf(isJourneyOn));
                            //add data to our database.
                            addDatatoFirebase(lat, lon, flag);


                            Log.d("Latitude : ", location.getLatitude() + "");
                            Log.d("Longitude : ", location.getLongitude() + "");
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(5);
//        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation(0);
            }
        }
    }

    //For add data to firebase
    private void addDatatoFirebase(String lat, String lon, int flag) {

        //Making coordinate object
        Coordinates coo = new Coordinates(lat, lon, flag);

        databaseReference.setValue(coo);
        isJourneyOnTextView.setText(String.valueOf(coo.getIsJourneyOn()));

        // we are use add value event listener method
        // which is called with database reference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // inside the method of on Data change we are setting
                // our object class to our database reference.
                // data base reference will sends data to firebase.
//                databaseReference.setValue(coo);
//                isJourneyOnTextView.setText(String.valueOf(coo.getIsJourneyOn()));

                // after adding this data we are showing toast message.
//                Toast.makeText(MainActivity.this, "data added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                Toast.makeText(MainActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (checkPermissions()) {
//            getLastLocation();
//        }
//    }
}
