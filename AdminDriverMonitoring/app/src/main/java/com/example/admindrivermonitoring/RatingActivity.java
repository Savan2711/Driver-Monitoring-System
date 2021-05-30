package com.example.admindrivermonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

//firebase connection imports
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RatingActivity extends AppCompatActivity {

    //references for firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private TextView srcLatTextView;
    private TextView srcLonTextView;
    private TextView dstLatTextView;
    private TextView dstLonTextView;
    private TextView startTimeTextView;
    private TextView endTimeTextView;
    private TextView durationTextView;
    private TextView yawnCountTextView;
    private TextView eyeCountTextView;

    private ProgressBar progressBar;
    private TextView progressbarText;

    private double finalRatingValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating1);

        /*********************************************************************/
        //For firebase connection

        // below line is used to get the instance
        // of our Firebase database.
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get
        // reference for our database.
        databaseReference = firebaseDatabase.getReference("Data");

        // initializing our object class variable.
//        srcLatTextView = findViewById(R.id.srcLat);
//        srcLonTextView = findViewById(R.id.srcLon);
//        dstLatTextView = findViewById(R.id.dstLat);
//        dstLonTextView = findViewById(R.id.dstLon);
        startTimeTextView = findViewById(R.id.startTime);
        endTimeTextView = findViewById(R.id.endTime);
        durationTextView = findViewById(R.id.duration);
        yawnCountTextView = findViewById(R.id.yawnCount);
        eyeCountTextView = findViewById(R.id.eyeCount);

        progressBar = findViewById(R.id.progress_bar);
        progressbarText = findViewById(R.id.text_view_progress);

//        progressBar.setProgress(80);
//        progressbarText.setText("80 %");

        // calling method
        // for getting data.
        getdata();
    }

    private double finalRating(int actualDuration, int idealDuration, int yawnCount, int eyeCount)
    {
        double rat = 0.0;
        double durationRat, yawnRat, eyeRat;
        int cnt = 3;
        durationRat = durationRating(actualDuration, idealDuration);
        yawnRat = yawnRating(yawnCount);
        eyeRat = eyeRating(eyeCount);
        rat = (durationRat + yawnRat + eyeRat) / cnt;
        Log.d("Duration Rating ", String.valueOf(durationRat));
        Log.d("Yawn Ratung ", String.valueOf(yawnRat));
        Log.d("Eye Rating ", String.valueOf(eyeRat));
        Log.d("Final Rating ", String.valueOf(rat));
        progressBar.setProgress((int) (rat * 10));
        progressbarText.setText(String.format("%.2f", (rat * 10)) + " %" );
        return rat;
    }

    double durationRating(int actualDuration, int idealDuration)
    {
        if (((idealDuration) - (0.03125 * idealDuration)) <= actualDuration && actualDuration <= (idealDuration) + (0.03125 * idealDuration))
        {
            return 10;
        }
        else if (((idealDuration) - (0.0625 * idealDuration)) <= actualDuration && actualDuration < (idealDuration) - (0.03125 * idealDuration))
        {
            return 10 - 1.5;
        }
        else if (((idealDuration) - (0.09375 * idealDuration)) <= actualDuration && actualDuration < (idealDuration) - (0.0625 * idealDuration))
        {
            return 10 - 2.5;
        }
        else if (((idealDuration) - (0.125 * idealDuration)) <= actualDuration && actualDuration < (idealDuration) - (0.09375 * idealDuration))
        {
            return 10 - 3.5;
        }
        else if (((idealDuration) - (0.1875 * idealDuration)) <= actualDuration && actualDuration < (idealDuration) - (0.125 * idealDuration))
        {
            return 10 - 4.5;
        }
        else if (((idealDuration) - (0.25 * idealDuration)) <= actualDuration && actualDuration < (idealDuration) - (0.1875 * idealDuration))
        {
            return 10 - 5.5;
        }
        else if (((idealDuration) - (0.25 * idealDuration)) >= actualDuration)
        {
            return 2;
        }
        //for actualtime > idealTime
        if(((idealDuration) + (0.03125 * idealDuration)) < actualDuration && actualDuration <= ((idealDuration) + (0.0625 * idealDuration)))
        {
            return 10 - 0.5;
        }
        else if(((idealDuration) + (0.0625 * idealDuration)) < actualDuration && actualDuration <= ((idealDuration) + (0.09375 * idealDuration)))
        {
            return 10 - 1.5;
        }
        else if(((idealDuration) + (0.09375 * idealDuration)) < actualDuration && actualDuration <= ((idealDuration) + (0.125 * idealDuration)))
        {
            return 10 - 2.5;
        }
        else if(((idealDuration) + (0.125 * idealDuration)) < actualDuration && actualDuration <= ((idealDuration) + (0.1875 * idealDuration)))
        {
            return 10 - 3.5;
        }
        else if(((idealDuration) + (0.1875 * idealDuration)) < actualDuration && actualDuration <= ((idealDuration) + (0.25 * idealDuration)))
        {
            return 10 - 4.5;
        }
        else if(((idealDuration) + (0.25 * idealDuration)) < actualDuration && actualDuration <= ((idealDuration) + (0.5 * idealDuration)))
        {
            return 10 - 6.5;
        }
        else if(((idealDuration) + (0.5 * idealDuration)) < actualDuration)
        {
            return 2;
        }
        else
        {
            return 0;
        }

    }

    private double yawnRating(int yawn)
    {
        return 10 - (yawn * 0.1);
    }

    private double eyeRating(int eye)
    {
        return 10 - (eye * 0.2);
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

//                String srcLat = snapshot.child("Source latitude").getValue().toString();
//                String srcLon = snapshot.child("Source longitude").getValue().toString();
//                String dstLat = snapshot.child("Destination latitude").getValue().toString();
//                String dstLon= snapshot.child("Destination longitude").getValue().toString();
                String startTime = snapshot.child("Start Time").getValue().toString();
                String endTime = snapshot.child("End Time").getValue().toString();
                String duration = snapshot.child("Time Diffrence").getValue().toString();
                String yawnCount = snapshot.child("Yawn Counter").getValue().toString();
                String eyeCount = snapshot.child("Eye Counter").getValue().toString();

                finalRatingValue = finalRating(Integer.parseInt(duration), Integer.parseInt(getIntent().getStringExtra("idealDuration")), Integer.parseInt(yawnCount), Integer.parseInt(eyeCount));
                Log.d("ideal ", getIntent().getStringExtra("idealDuration"));

                // after getting the value we are setting
                // our value to our text view in below line.
//                srcLatTextView.setText("Source Lattitude : " + srcLat);
//                srcLonTextView.setText("Source Longitude : " + srcLon);
//                dstLatTextView.setText("Destination Latitute : " + dstLat);
//                dstLonTextView.setText("Destination Longitude : " + dstLon);
                startTimeTextView.setText("Start Time : " + startTime);
                endTimeTextView.setText("End Time : " + endTime);
                durationTextView.setText("Travelled Time : " + duration + " s");
                yawnCountTextView.setText("Yawn Count : " + yawnCount);
                eyeCountTextView.setText("Eye Count : " + eyeCount);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(RatingActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}