package com.example.venues.search.venuessearch;

import android.location.Location;

public interface VenuePresenter {
    void onQueryTextChange(String query);

    void onLocationChange(Location location);

    void onHTTPQueryFinished(String response);

    void start();
}
