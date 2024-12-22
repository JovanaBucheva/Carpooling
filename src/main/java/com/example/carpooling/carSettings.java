package com.example.carpooling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class carSettings extends AppCompatActivity {

    DBHelper db;

    // UI Components
    TextView carNameShown, pricePerKmShown, peopleNumberShown;
    EditText carName, pricePerKm, peopleNumber;
    Button add, change;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_car_settings);

        db = new DBHelper(this);
        username = getIntent().getStringExtra("username");

        // Set up the Toolbar
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

        initializeViews();

        if (db.hasCarInfo(username)) {
            displayCarInfo();
        } else {
            switchToEditMode(false); // Show edit fields for new data
        }

        setupListeners();
    }

    private void initializeViews() {
        carNameShown = findViewById(R.id.carNameShown);
        pricePerKmShown = findViewById(R.id.pricePerKmShown);
        peopleNumberShown = findViewById(R.id.peopleNumberShown);

        carName = findViewById(R.id.carName);
        pricePerKm = findViewById(R.id.pricePerKm);
        peopleNumber = findViewById(R.id.peopleNumber);

        add = findViewById(R.id.submitButton);
        change = findViewById(R.id.makeChange);
    }

    private void setupListeners() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCarInfo();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToEditMode(true); // Switch to edit mode
            }
        });
    }

    private void displayCarInfo() {
        car Car = db.getCarInfo(username); // Retrieve car data from the database

        if (Car != null) {
            // Set TextViews with car data
            carNameShown.setText("Vehicle name: " + Car.getName());
            pricePerKmShown.setText("Price: " + Car.getPrice());
            peopleNumberShown.setText("Available seats: " + Car.getPeople());

            switchToViewMode();
        } else {
            Toast.makeText(this, "Error retrieving car data.", Toast.LENGTH_SHORT).show();
            switchToEditMode(false);
        }
    }


    private void switchToEditMode(boolean isEditing) {
        carNameShown.setVisibility(View.GONE);
        pricePerKmShown.setVisibility(View.GONE);
        peopleNumberShown.setVisibility(View.GONE);
        change.setVisibility(View.GONE);

        carName.setVisibility(View.VISIBLE);
        pricePerKm.setVisibility(View.VISIBLE);
        peopleNumber.setVisibility(View.VISIBLE);
        add.setVisibility(View.VISIBLE);

        if (isEditing) {
            add.setText("Make a change"); // Update button text for editing
        } else {
            add.setText("Add vehicle");
        }
    }
    private void switchToViewMode() {
        carNameShown.setVisibility(View.VISIBLE);
        pricePerKmShown.setVisibility(View.VISIBLE);
        peopleNumberShown.setVisibility(View.VISIBLE);
        change.setVisibility(View.VISIBLE);

        carName.setVisibility(View.GONE);
        pricePerKm.setVisibility(View.GONE);
        peopleNumber.setVisibility(View.GONE);
        add.setVisibility(View.GONE);
    }

    private void saveCarInfo() {
        String car_name = carName.getText().toString().trim();
        String priceText = pricePerKm.getText().toString().trim();
        String peopleText = peopleNumber.getText().toString().trim();

        if (car_name.isEmpty() || priceText.isEmpty() || peopleText.isEmpty()) {
            Toast.makeText(this, R.string.error_fill_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int price = Integer.parseInt(priceText);
            int people = Integer.parseInt(peopleText);

            if (db.setCarInfo(username, car_name, price, people)) {
                Toast.makeText(this, R.string.success_save, Toast.LENGTH_SHORT).show();
                displayCarInfo();
            } else {
                Toast.makeText(this, R.string.error_save, Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_invalid_input, Toast.LENGTH_SHORT).show();
        }
    }
}

