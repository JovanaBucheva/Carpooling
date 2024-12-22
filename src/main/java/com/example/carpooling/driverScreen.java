package com.example.carpooling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Switch;
import android.widget.Toast;
import java.util.List;

public class driverScreen extends AppCompatActivity {

    RecyclerView mRecyclerView;
    myAdapter mAdapter;
    TextView noActivity, requestText;
    ImageButton carSettings;
    String username;

    Button enterRoute;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_screen);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        int route = intent.getIntExtra("route", 0);

        db = new DBHelper(driverScreen.this);

        noActivity = findViewById(R.id.no_activity);
        requestText = findViewById(R.id.requestText);
        mRecyclerView = findViewById(R.id.baranja);
        carSettings = findViewById(R.id.car_info_icon);
        enterRoute =findViewById(R.id.enterRoute);

        requestText.setVisibility(View.VISIBLE);
        Toolbar my_toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);
        TextView us = findViewById(R.id.user);
        us.setVisibility(View.VISIBLE);
        us.setText(username);
        ImageView star = findViewById(R.id.star);
        star.setVisibility(View.VISIBLE);
        TextView rating=findViewById(R.id.raiting);
        rating.setVisibility(View.VISIBLE);
        rating.setText(db.returnRating(username,1));


        ImageButton carInfo = findViewById(R.id.car_info_icon);
        carInfo.setVisibility(View.VISIBLE);

        Switch workingSwitch = findViewById(R.id.workingSwitch);
        TextView workingText = findViewById(R.id.workingText);

        if (route == 1) {
            workingSwitch.setChecked(true);
        }

        workingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (db.hasCarInfo(username)) {
                    // User has car info, mark as active.
                    db.setActive(username, 1); // Update the database.
                    Intent intent3 = new Intent(driverScreen.this, driverScreen.class);
                    intent3.putExtra("username", username);
                    intent3.putExtra("route",1);
                    startActivity(intent3);
                    finish();
                } else {
                    // User lacks car info, reset the switch to LEFT (inactive).
                    workingSwitch.setChecked(false); // Reset the switch.
                    Toast.makeText(this, "Fill in information for the vehicle", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Switch is toggled to the LEFT (inactive).
                db.setActive(username, 0); // Update the database.
                db.deleteRoute(username);
                Toast.makeText(this, "Deactivate driver", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(driverScreen.this, driverScreen.class);
                intent3.putExtra("username", username);
                startActivity(intent3);
            }
        });


        carSettings.setOnClickListener(v -> {
            Intent intent1 = new Intent(driverScreen.this, carSettings.class);
            intent1.putExtra("username", username);
            startActivity(intent1);
        });

        if (db.returnActive(username) == 1) {
            noActivity.setVisibility(View.GONE);

            if(db.hasRoute(username)){
                mRecyclerView.setVisibility(View.VISIBLE);

                List<request> requests = db.returnRequests(username);

                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                mAdapter = new myAdapter(requests, R.layout.request, this, username);
                mRecyclerView.setAdapter(mAdapter);
            }else{
                enterRoute.setVisibility(View.VISIBLE);
                enterRoute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent3 = new Intent(driverScreen.this, driverInsertsRoute.class);
                        intent3.putExtra("user", username);
                        startActivity(intent3);
                    }
                });
            }

        } else if (db.returnActive(username) == 0) {
            // Show "no activity" message
            noActivity.setVisibility(View.VISIBLE);
            requestText.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            enterRoute.setVisibility(View.GONE);
        }
    }
}
