package com.example.carpooling;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class pending extends AppCompatActivity {

    private View statusCircle1, statusCircle2, statusCircle3, statusCircle4;
    private View statusLine1, statusLine2, statusLine3;
    private TextView statusText1, statusText2, statusText3, statusText4;
    private String usernamePassenger;
    private String usernameDriver;
    DBHelper db;
    int check=0;
    double driverLat,driverLon,passengerLat,passengerLon,passengerLatD,passengerLonD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);

        Intent intent2= getIntent();
        usernamePassenger=intent2.getStringExtra("usernamePassenger");
        usernameDriver=intent2.getStringExtra("usernameDriver");

        db=new DBHelper(this);
        // Initialize circles
        statusCircle1 = findViewById(R.id.status_circle_1);
        statusCircle2 = findViewById(R.id.status_circle_2);
        statusCircle3 = findViewById(R.id.status_circle_3);

        // Initialize lines
        statusLine1 = findViewById(R.id.status_line_1);
        statusLine2 = findViewById(R.id.status_line_2);

        // Initialize text
        statusText1 = findViewById(R.id.status_text_1);
        statusText2 = findViewById(R.id.status_text_2);
        statusText3 = findViewById(R.id.status_text_3);

        // Simulate status updates
        updateStatus();
    }

    private void updateStatus() {
        Handler handler = new Handler();

        handler.postDelayed(() -> {
                statusCircle1.setBackgroundResource(R.drawable.circle_green);
                statusText1.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                statusLine1.setBackgroundResource(R.drawable.line_green);

        }, 10000);

        handler.postDelayed(() -> {
            statusCircle2.setBackgroundResource(R.drawable.circle_green);
            statusText2.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            statusLine2.setBackgroundResource(R.drawable.line_green);
            db.updateDriveStatus(usernamePassenger,usernameDriver,2);
        }, 15000);

        handler.postDelayed(() -> {
            if(db.getDriveStatus(usernamePassenger,usernameDriver)==2) {
                statusCircle3.setBackgroundResource(R.drawable.circle_green);
                statusText3.setTextColor(ContextCompat.getColor(this, android.R.color.black));

                showRatingDialog(usernameDriver,usernamePassenger);
            }}, 20000);
    }

    private void showRatingDialog(String usernameDriver,String usernamePassenger) {
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rate_driver, null);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView userRated = dialogView.findViewById(R.id.rate_driver_text);

        RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
        Button submitButton = dialogView.findViewById(R.id.submit_rating_button);

        userRated.setText("Rate the driver with the following usernme: " + usernameDriver);

        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            db.saveDriverRating(usernamePassenger,usernameDriver, rating);
            dialog.dismiss();
            Intent intent = new Intent(pending.this, driverOptionsForPassengers.class);
            intent.putExtra("username", usernamePassenger);
            startActivity(intent);
            finish();
        });
        // Show the dialog
        dialog.show();
    }
}
