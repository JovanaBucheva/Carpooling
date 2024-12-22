package com.example.carpooling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class Maps extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String start, end, username;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        db = new DBHelper(this);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        start = intent.getStringExtra("start");
        end = intent.getStringExtra("end");

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        TextView us = findViewById(R.id.user);
        us.setVisibility(View.VISIBLE);
        us.setText(username);
        ImageView star = findViewById(R.id.star);
        star.setVisibility(View.VISIBLE);
        TextView rating = findViewById(R.id.raiting);
        rating.setVisibility(View.VISIBLE);

        new Thread(() -> {
            String userRating = db.returnRating(username, 1);
            runOnUiThread(() -> rating.setText(userRating));
        }).start();

        ImageButton carInfo = findViewById(R.id.car_info_icon);
        carInfo.setVisibility(View.GONE);

        Switch workingSwitch = findViewById(R.id.workingSwitch);
        workingSwitch.setChecked(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Map Fragment is null!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (!start.isEmpty() && !end.isEmpty()) {
            new Thread(() -> {
                Geocoder geocoder = new Geocoder(this);
                try {
                    List<Address> startAddresses = geocoder.getFromLocationName(start, 1);
                    List<Address> endAddresses = geocoder.getFromLocationName(end, 1);

                    if (startAddresses != null && !startAddresses.isEmpty() &&
                            endAddresses != null && !endAddresses.isEmpty()) {

                        Address startAddress = startAddresses.get(0);
                        Address endAddress = endAddresses.get(0);

                        LatLng startLatLng = new LatLng(startAddress.getLatitude(), startAddress.getLongitude());
                        LatLng endLatLng = new LatLng(endAddress.getLatitude(), endAddress.getLongitude());

                        runOnUiThread(() -> updateMap(startLatLng, endLatLng));
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Локациите не се пронајдени!", Toast.LENGTH_SHORT).show());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(this, "Грешка при пребарување на локациите!", Toast.LENGTH_SHORT).show());
                }
            }).start();
        } else {
            Toast.makeText(this, "Внесете и почетна и дестинациска локација!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMap(LatLng startLatLng, LatLng endLatLng) {
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(startLatLng).title("Почетна локација: " + start));
            mMap.addMarker(new MarkerOptions().position(endLatLng).title("Дестинација: " + end));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(startLatLng);
            builder.include(endLatLng);
            LatLngBounds bounds = builder.build();
            int padding = 100;
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        }
    }
}
