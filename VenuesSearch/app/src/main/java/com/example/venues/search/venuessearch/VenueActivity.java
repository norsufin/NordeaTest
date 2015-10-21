package com.example.venues.search.venuessearch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;

public class VenueActivity extends Activity implements VenueView {
    private static final String TAG = "VenueActivity";
    private ProgressBar mProgressBar;
    private TextView mProgressBarText;
    private ListView mListView;
    private SearchView mSearchView;
    private VenuePresenter mPresenter;
    private VenueItemAdapter mAdapter;

    @Override
    public void setLocationProgress() {
        mProgressBarText.setText(this.getString(R.string.locating_device));
    }

    @Override
    public void setSearchingVenuesProgress() {
        mProgressBarText.setText(this.getString(R.string.searching_venues));
    }

    @Override
    public void updateList() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBarText = (TextView) findViewById(R.id.progressTextView);
        mListView = (ListView) findViewById(R.id.listview);
        mSearchView = (SearchView) findViewById(R.id.searchBox);

        mPresenter = new VenuePresenterImpl(this);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.v(TAG, "onQueryTextSubmit:" + query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.v(TAG, "onQueryTextChange:" + query);
                mPresenter.onQueryTextChange(query);
                return true;
            }
        });

        mPresenter.start();
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBarText.setVisibility(View.VISIBLE);
        mSearchView.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
        mProgressBarText.setVisibility(View.GONE);
        mSearchView.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.VISIBLE);
        mListView.setEmptyView(findViewById(R.id.noVenues));
    }

    @Override
    public void getLocation() {
        new LocationRequest(mPresenter, this);
    }

    @Override
    public void getVenueList(URL url) {
        new HTTPGetAsyncTask(mPresenter).execute(url);
    }

    @Override
    public void initList(ArrayList<VenueItem> items) {
        mAdapter = new VenueItemAdapter(this, items);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void showError() {
        Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
        finish();
    }
}

