package com.example.venues.search.venuessearch;

public class VenueItem {
    public final String name;
    public final String address;
    public final Integer distance;

    public VenueItem(String name, String address, Integer distance) {
        this.name = name;
        this.address = address;
        this.distance = distance;
    }
}
