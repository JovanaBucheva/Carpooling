package com.example.carpooling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class driverOptionsForPassengers extends AppCompatActivity {
    RecyclerView mRecyclerView;
    EditText searchLocation;
    myAdapterOptions mAdapter;
    private String username;
    private String start="";
    Button search;
    TextView no_options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_options_for_passengers);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        DBHelper db=new DBHelper(this);
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
        rating.setText(db.returnRating(username,0));

        searchLocation=findViewById(R.id.searchLocation);
        search=findViewById(R.id.search);
        no_options=findViewById(R.id.no_options);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start=searchLocation.getText().toString();
                if(start!=""){

                    List<option> options = db.returnOptions(start);
                    if (options.isEmpty()) {
                        no_options.setVisibility(View.VISIBLE);
                    } else {
                        no_options.setVisibility(View.GONE);
                        //сетирање на RecyclerView контејнерот
                        mRecyclerView = (RecyclerView) findViewById(R.id.baranja);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        // оваа карактеристика може да се користи ако се знае дека промените
                        // во содржината нема да ја сменат layout големината на RecyclerView
                        mRecyclerView.setHasFixedSize(true);

                        // ќе користиме LinearLayoutManager
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(driverOptionsForPassengers.this));

                        // и default animator (без анимации)
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                        // сетирање на кориснички дефиниран адаптер myAdapter (посебна класа)
                        mAdapter = new myAdapterOptions(options, R.layout.option, driverOptionsForPassengers.this,username);

                        //прикачување на адаптерот на RecyclerView
                        mRecyclerView.setAdapter(mAdapter);
                    }

                }
            }
        });

    }

}
