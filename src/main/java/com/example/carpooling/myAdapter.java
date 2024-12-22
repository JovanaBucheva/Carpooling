package com.example.carpooling;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myAdapter.ViewHolder> {

    private List<request> myList;
    private int layoutResource;
    private Context mContext;
    private DBHelper db;
    private String username;

    // ViewHolder class for item views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView rating;
        public Button accept, endDrive;

        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user);
            rating = itemView.findViewById(R.id.rating);
            accept = itemView.findViewById(R.id.accept);
            endDrive = itemView.findViewById(R.id.endDrive);
        }
    }

    // Constructor
    public myAdapter(List<request> myList, int layoutResource, Context context, String username) {
        this.myList = myList;
        this.layoutResource = layoutResource;
        this.mContext = context;
        this.username = username;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutResource, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        request entry = myList.get(position);
        db = new DBHelper(mContext);

        viewHolder.username.setText(entry.getUsername());
        viewHolder.rating.setText(String.valueOf(entry.getRating())); // Assuming rating is numeric
        viewHolder.accept.setVisibility(View.VISIBLE);
        viewHolder.endDrive.setVisibility(View.GONE);

        viewHolder.accept.setOnClickListener(v -> {
            viewHolder.accept.setVisibility(View.GONE);
            viewHolder.endDrive.setVisibility(View.VISIBLE);
        });

        viewHolder.endDrive.setOnClickListener(v -> {
            showPassengerRatingDialog(entry.getUsername());
            db.deleteDrive(username,entry.getUsername());
        });
    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }

    private void showPassengerRatingDialog(String passengerUsername) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogView = inflater.inflate(R.layout.dialog_rate_passenger, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView whichUser = dialogView.findViewById(R.id.whichUser);
        RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
        Button submitButton = dialogView.findViewById(R.id.submit_rating_button);

        whichUser.setText("Rate the passenger with the following username: " + passengerUsername);

        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            if (rating == 0) {
                Toast.makeText(mContext, "Please provide a rating.", Toast.LENGTH_SHORT).show();
                return;
            }
            db.savePassengerRating(passengerUsername, username, rating);
            dialog.dismiss();// Close the dialog
            Intent intent = new Intent(mContext,driverScreen.class);
            intent.putExtra("username",username);
            intent.putExtra("route",1);
            mContext.startActivity(intent);
        });

        dialog.show();
    }
}
