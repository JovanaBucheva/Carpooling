package com.example.carpooling;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Corrected
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Button registration = findViewById(R.id.registration);
        if (registration != null) { // Check if button exists in layout
            registration.setOnClickListener(v -> {
                Intent intent = new Intent(login.this, Registration.class);
                startActivity(intent);
            });
        }

    }
}
