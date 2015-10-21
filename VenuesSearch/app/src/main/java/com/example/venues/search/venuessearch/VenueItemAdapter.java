package com.example.venues.search.venuessearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class VenueItemAdapter extends ArrayAdapter<VenueItem> {
    public VenueItemAdapter(Context context, ArrayList<VenueItem> venues) {
        super(context, 0, venues);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Get the data item for this position
        VenueItem venue = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.venue_element, parent, false);
        }
        TextView tvName = (TextView) view.findViewById(R.id.name);
        TextView tvAddress = (TextView) view.findViewById(R.id.address);
        TextView tvDistance = (TextView) view.findViewById(R.id.distance);

        tvName.setText(venue.name);
        tvAddress.setText(venue.address);
        tvDistance.setText(venue.distance.toString());

        return view;
    }
}