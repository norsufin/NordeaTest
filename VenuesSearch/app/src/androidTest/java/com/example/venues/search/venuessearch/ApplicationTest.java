package com.example.venues.search.venuessearch;

import android.app.Application;
import android.location.Location;
import android.test.ApplicationTestCase;

import java.net.URL;
import java.util.ArrayList;

public class ApplicationTest extends ApplicationTestCase<Application> {
    private VenuePresenter venuePresenter;
    private VenueView testerUI;
    private ArrayList<VenueItem> initList_items;
    private URL getVenueList_url;
    private Boolean showProgress_called = false;
    private Boolean hideProgress_called = false;
    private Boolean setLocationProgress_called = false;
    private Boolean setSearchingVenuesProgress_called = false;
    private Boolean updateList_called = false;
    private Boolean getLocation_called = false;
    private Boolean showError_called = false;

    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        testerUI = new TesterUI();
        venuePresenter = new VenuePresenterImpl(testerUI);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        venuePresenter = null;
    }

    public void testSuccessCase() throws Exception {
        venuePresenter.start();

        // Verify VenueView method calls
        assertTrue(getLocation_called);
        getLocation_called = false;

        assertTrue(showProgress_called);
        showProgress_called = false;

        assertTrue(setLocationProgress_called);
        setLocationProgress_called = false;

        // Response location lat 65.0167 long 25.4667
        Location location = new Location("TestProvider");
        location.setLatitude(65.0167D);
        location.setLongitude(25.4667D);
        venuePresenter.onLocationChange(location);

        assertTrue(setSearchingVenuesProgress_called);
        setSearchingVenuesProgress_called = false;

        // Verify HTTP request
        String expected_result = "https://api.foursquare.com/v2/venues/search?ll=65.0167,25.4667&client_id=client_id&client_secret=client_secret&v=20151019";
        assertNotSame(getVenueList_url.toString(), expected_result);

        // HTTP response
        String response = "{\"meta\":{\"code\":200,\"requestId\":\"1\"},\"response\":{\"venues\":" +
                "[{\"name\":\"baaa\",\"location\":{\"address\":\"baaa_addr\",\"distance\":1,\"city\":\"baaa_city\"}}," +
                "{\"name\":\"abaa\",\"location\":{\"address\":\"abaa_addr\",\"distance\":2,\"city\":\"abaa_city\"}}," +
                "{\"name\":\"aaba\",\"location\":{\"address\":\"aaba_addr\",\"distance\":3,\"city\":\"aaba_city\"}}," +
                "{\"name\":\"aaab\",\"location\":{\"address\":\"aaab_addr\",\"distance\":4,\"city\":\"aaab_city\"}}]}}";

        venuePresenter.onHTTPQueryFinished(response);

        // Verify method call
        assertTrue(hideProgress_called);
        hideProgress_called = false;

        // Verify initList_items
        assertEquals(initList_items.size(), 4);
        // Item 1
        assertEquals(initList_items.get(0).name, "baaa");
        assertEquals(initList_items.get(0).address, "baaa_addr");
        assertEquals(initList_items.get(0).distance.intValue(), 1);
        // Item 2
        assertEquals(initList_items.get(1).name, "abaa");
        assertEquals(initList_items.get(1).address, "abaa_addr");
        assertEquals(initList_items.get(1).distance.intValue(), 2);
        // Item 3
        assertEquals(initList_items.get(2).name, "aaba");
        assertEquals(initList_items.get(2).address, "aaba_addr");
        assertEquals(initList_items.get(2).distance.intValue(), 3);
        // Item 4
        assertEquals(initList_items.get(3).name, "aaab");
        assertEquals(initList_items.get(3).address, "aaab_addr");
        assertEquals(initList_items.get(3).distance.intValue(), 4);
    }

    public void testSearchCase() throws Exception {
        // Create content to search
        String response = "{\"meta\":{\"code\":200,\"requestId\":\"1\"},\"response\":{\"venues\":" +
                "[{\"name\":\"baaa\",\"location\":{\"address\":\"baaa_addr\",\"distance\":1,\"city\":\"baaa_city\"}}," +
                "{\"name\":\"abaa\",\"location\":{\"address\":\"abaa_addr\",\"distance\":2,\"city\":\"abaa_city\"}}," +
                "{\"name\":\"aaba\",\"location\":{\"address\":\"aaba_addr\",\"distance\":3,\"city\":\"aaba_city\"}}," +
                "{\"name\":\"aaab\",\"location\":{\"address\":\"aaab_addr\",\"distance\":4,\"city\":\"aaab_city\"}}]}}";
        venuePresenter.onHTTPQueryFinished(response);

        // Search string
        venuePresenter.onQueryTextChange("ba");

        assertTrue(updateList_called);
        updateList_called = false;

        // Verify initList_items
        assertEquals(initList_items.size(), 3);
        // Item 1
        assertEquals(initList_items.get(0).name, "baaa");
        assertEquals(initList_items.get(0).address, "baaa_addr");
        assertEquals(initList_items.get(0).distance.intValue(), 1);
        // Item 2
        assertEquals(initList_items.get(1).name, "abaa");
        assertEquals(initList_items.get(1).address, "abaa_addr");
        assertEquals(initList_items.get(1).distance.intValue(), 2);
        // Item 3
        assertEquals(initList_items.get(2).name, "aaba");
        assertEquals(initList_items.get(2).address, "aaba_addr");
        assertEquals(initList_items.get(2).distance.intValue(), 3);

        // Search string
        venuePresenter.onQueryTextChange("baa");

        assertTrue(updateList_called);
        updateList_called = false;

        // Verify initList_items
        assertEquals(initList_items.size(), 2);
        // Item 1
        assertEquals(initList_items.get(0).name, "baaa");
        assertEquals(initList_items.get(0).address, "baaa_addr");
        assertEquals(initList_items.get(0).distance.intValue(), 1);
        // Item 2
        assertEquals(initList_items.get(1).name, "abaa");
        assertEquals(initList_items.get(1).address, "abaa_addr");
        assertEquals(initList_items.get(1).distance.intValue(), 2);

        // Search string
        venuePresenter.onQueryTextChange("baaa");

        assertTrue(updateList_called);
        updateList_called = false;

        // Verify initList_items
        assertEquals(initList_items.size(), 1);
        // Item 1
        assertEquals(initList_items.get(0).name, "baaa");
        assertEquals(initList_items.get(0).address, "baaa_addr");
        assertEquals(initList_items.get(0).distance.intValue(), 1);
    }

    public void testHTTPResponseErrorCases() throws Exception {
        // ------- Null list
        String response = null;

        venuePresenter.onHTTPQueryFinished(response);

        // Verify method call
        assertTrue(showError_called);
        showError_called = false;

        // ------- Empty list
        response = new String("");

        venuePresenter.onHTTPQueryFinished(response);

        // Verify method call
        assertTrue(showError_called);
        showError_called = false;

        // ------- HTTP error
        response = "{\"meta\":{\"code\":201}}";

        venuePresenter.onHTTPQueryFinished(response);

        // Verify method call
        assertTrue(showError_called);
        showError_called = false;
    }

    class TesterUI implements VenueView {

        @Override
        public void showProgress() {
            showProgress_called = true;
        }

        @Override
        public void hideProgress() {
            hideProgress_called = true;
        }

        @Override
        public void setLocationProgress() {
            setLocationProgress_called = true;
        }

        @Override
        public void setSearchingVenuesProgress() {
            setSearchingVenuesProgress_called = true;
        }

        @Override
        public void updateList() {
            updateList_called = true;
        }

        @Override
        public void getLocation() {
            getLocation_called = true;
        }

        @Override
        public void getVenueList(URL url) {
            getVenueList_url = url;
        }

        @Override
        public void initList(ArrayList<VenueItem> items) {
            initList_items = items;
        }

        @Override
        public void showError() {
            showError_called = true;
        }
    }
}