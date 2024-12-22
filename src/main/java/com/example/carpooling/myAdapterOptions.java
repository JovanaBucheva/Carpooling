package com.example.carpooling;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class myAdapterOptions extends RecyclerView.Adapter<myAdapterOptions.ViewHolder> {

    private List<option> myList;
    private int optionLayout;
    private Context mContext;
    private String username;
    private DBHelper db;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView rating;
        public TextView price;
        public TextView car_name;
        public TextView from;
        public TextView to;
        public TextView time;
        public Button request;


        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user);
            rating = itemView.findViewById(R.id.rating);
            price = itemView.findViewById(R.id.price);
            car_name = itemView.findViewById(R.id.car);
            request = itemView.findViewById(R.id.request);
            from= itemView.findViewById(R.id.from);
            to= itemView.findViewById(R.id.to);
            time=itemView.findViewById(R.id.time);
        }
    }

    // Constructor
    public myAdapterOptions(List<option> myList, int optionLayout, Context context, String username) {
        this.myList = myList;
        this.optionLayout = optionLayout;
        this.mContext = context;
        this.username = username;
        this.db = new DBHelper(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(optionLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (myList == null || myList.isEmpty()) {
            Log.d("myAdapterOptions", "No data available to display.");
            return;
        }

        option entry = myList.get(position);

        viewHolder.username.setText(entry.getUsername() != null ? entry.getUsername() : "Unknown");
        viewHolder.rating.setText(entry.getRating() >= 0 ? String.valueOf(entry.getRating()) : "N/A");
        viewHolder.price.setText("Price: " + (entry.getPrice() > 0 ? entry.getPrice() + " ден/км" : "Unavailable"));
        viewHolder.car_name.setText("Vehicle: " + (entry.getCarName() != null ? entry.getCarName() : "Not Specified"));
        viewHolder.from.setText("Start location: " + (entry.getFrom() != null ? entry.getFrom(): "Unavailable"));
        viewHolder.to.setText("End location: " + (entry.getTo() != null ? entry.getTo(): "Unavailable"));
        viewHolder.time.setText("Start time: " + (entry.getTime() != null ? entry.getTime(): "Unavailable"));

        viewHolder.request.setOnClickListener(v -> {
            try {
                if(db.addDriveRequest(username, entry.getUsername(), 0)){
                    Toast.makeText(mContext, "Request sent successfully!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(mContext, pending.class);
                    intent.putExtra("usernameDriver", entry.getUsername());
                    intent.putExtra("usernamePassenger", username);
                    mContext.startActivity(intent);
                }else{
                    Toast.makeText(mContext, "Request sent UNsuccessfully!", Toast.LENGTH_SHORT).show();

                }
            } catch (Exception e) {
                Toast.makeText(mContext, "Failed to process the request.", Toast.LENGTH_SHORT).show();
                Log.e("myAdapterOptions", "Error updating drive status", e);
            }
        });
    }


    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }
}
