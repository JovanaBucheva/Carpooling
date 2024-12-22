package com.example.carpooling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class driverInsertsRoute extends AppCompatActivity {

    private EditText startLocationEditText, destinationEditText, timeEditText;
    private Button showOnMapButton, addRouteButton;
    private String username;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_inserts_route);

        db = new DBHelper(this);

        Intent intent = getIntent();
        username = intent.getStringExtra("user");

        // Set up the Toolbar
        Toolbar my_toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);
        TextView us = findViewById(R.id.user);
        us.setVisibility(View.VISIBLE);
        us.setText(username);
        ImageView star = findViewById(R.id.star);
        star.setVisibility(View.VISIBLE);
        TextView rating = findViewById(R.id.raiting);
        rating.setVisibility(View.VISIBLE);

        // Retrieve the rating in the background
        new Thread(() -> {
            String userRating = db.returnRating(username, 1);
            runOnUiThread(() -> rating.setText(userRating));
        }).start();

        ImageButton carInfo = findViewById(R.id.car_info_icon);
        carInfo.setVisibility(View.GONE);

        Switch workingSwitch = findViewById(R.id.workingSwitch);
        workingSwitch.setChecked(true);

        startLocationEditText = findViewById(R.id.addFromLocation);
        destinationEditText = findViewById(R.id.addToLocation);
        timeEditText = findViewById(R.id.startTime);
        showOnMapButton = findViewById(R.id.seeRouteOnMap);
        addRouteButton = findViewById(R.id.enterRoute);

        // Show route on map
        showOnMapButton.setOnClickListener(v -> {
            String start = startLocationEditText.getText().toString();
            String end = destinationEditText.getText().toString();

            if (!start.isEmpty() && !end.isEmpty()) {
                Intent intent2 = new Intent(this, Maps.class);
                intent2.putExtra("start", start);
                intent2.putExtra("end", end);
                intent2.putExtra("username", username);
                startActivity(intent2);
            } else {
                Toast.makeText(this, "FILL IN BOTH START AND END LOCATION!", Toast.LENGTH_SHORT).show();
            }
        });

        // Add route
        addRouteButton.setOnClickListener(v -> {
            String start = startLocationEditText.getText().toString();
            String end = destinationEditText.getText().toString();
            String startTime = timeEditText.getText().toString();

            if (start.isEmpty() || end.isEmpty() || startTime.isEmpty()) {
                Toast.makeText(this, "FILL IN ALL THE FIELDS !", Toast.LENGTH_LONG).show();
            } else {
                new Thread(() -> {
                    boolean inserted = db.insertDriverLocations(username, startTime, start, end);
                    runOnUiThread(() -> {
                        if (inserted) {
                            Toast.makeText(this, "Route entered successfully", Toast.LENGTH_SHORT).show();
                            Intent intent2 = new Intent(this, driverScreen.class);
                            intent2.putExtra("username", username);
                            intent2.putExtra("route",1);
                            startActivity(intent2);
                            finish();
                        } else {
                            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
            }
        });
    }
}