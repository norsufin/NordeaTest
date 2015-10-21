package com.example.venues.search.venuessearch;

import java.net.URL;
import java.util.ArrayList;

public interface VenueView {
    void showProgress();

    void hideProgress();

    void setLocationProgress();

    void setSearchingVenuesProgress();

    void updateList();

    void getLocation();

    void getVenueList(URL url);

    void initList(ArrayList<VenueItem> items);

    void showError();
}
