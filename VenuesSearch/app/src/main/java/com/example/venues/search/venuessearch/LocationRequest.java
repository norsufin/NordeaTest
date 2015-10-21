package com.example.venues.search.venuessearch;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationRequest {

    private final VenuePresenter presenter;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener = new MyLocationListener();

    public LocationRequest(VenuePresenter presenter, VenueView venueView) {
        this.presenter = presenter;
        mLocationManager = (LocationManager) ((Activity) venueView).getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, mLocationListener);
    }

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            presenter.onLocationChange(location);
            mLocationManager.removeUpdates(mLocationListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
