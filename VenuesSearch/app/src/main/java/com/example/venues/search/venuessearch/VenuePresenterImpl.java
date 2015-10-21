package com.example.venues.search.venuessearch;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class VenuePresenterImpl implements VenuePresenter {
    private static final String TAG = "VenuePresenterImpl";
    private static final String ClientID = "client_id";
    private static final String ClientSecret = "client_secret";
    private final VenueView venueView;
    private final ArrayList<VenueItem> arrayOfVenues = new ArrayList<>();
    private String venuesList;

    public VenuePresenterImpl(VenueView venueView) {
        Log.v(TAG, "VenuePresenterImpl");
        this.venueView = venueView;
    }

    @Override
    public void onQueryTextChange(String query) {
        Log.v(TAG, "onQueryTextChange");
        createVenuesList(arrayOfVenues, venuesList, query);
        venueView.updateList();
    }

    @Override
    public void onLocationChange(Location location) {
        Log.v(TAG, "onLocationChange");
        venueView.setSearchingVenuesProgress();
        URL url = null;
        try {
            url = new URL("https://api.foursquare.com/v2/venues/search?ll=" +
                    location.getLatitude() + "," +
                    location.getLongitude() +
                    "&client_id=" + ClientID +
                    "&client_secret=" + ClientSecret +
                    "&v=20151019");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url != null) {
            venueView.getVenueList(url);
        }
    }

    @Override
    public void onHTTPQueryFinished(String response) {
        Log.v(TAG, "onHTTPQueryFinished:" + response);

        if (response != null && !response.isEmpty()) {
            venuesList = response;
            if (createVenuesList(arrayOfVenues, venuesList, "")) {
                venueView.hideProgress();
                venueView.initList(arrayOfVenues);
            } else {
                venueView.showError();
            }
        } else {
            venueView.showError();
        }
    }

    @Override
    public void start() {
        venueView.getLocation();
        venueView.showProgress();
        venueView.setLocationProgress();
    }

    private boolean createVenuesList(ArrayList<VenueItem> arrayOfVenues, String sb, String searchStr) {
        arrayOfVenues.clear();
        try {
            JSONObject json = new JSONObject(sb);
            JSONObject meta = json.getJSONObject("meta");
            int code = meta.getInt("code");
            if (code != HttpsURLConnection.HTTP_OK) {
                return false;
            }
            JSONObject response = json.getJSONObject("response");
            JSONArray venueArray = response.getJSONArray("venues");

            Log.v(TAG, "meta:" + meta.toString());
            Log.v(TAG, "response:" + response.toString());
            Log.v(TAG, "venueArray:" + venueArray.toString());

            for (int i = 0; i < venueArray.length(); i++) {
                JSONObject venue = venueArray.getJSONObject(i);
                JSONObject location = venue.getJSONObject("location");
                String name = venue.getString("name");
                Log.v(TAG, "name:" + name + " searchStr:" + searchStr);
                if (name.toLowerCase().contains(searchStr.toLowerCase())) {
                    String address;
                    if (location.has("address")) {
                        address = location.getString("address");
                    } else if (location.has("city")) {
                        // use city
                        address = location.getString("city");
                    } else {
                        address = ((Activity) venueView).getApplicationContext().getResources().getString(R.string.no_address);
                    }
                    Integer distance = location.getInt("distance");
                    Log.v(TAG, "add");
                    arrayOfVenues.add(new VenueItem(name, address, distance));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}
