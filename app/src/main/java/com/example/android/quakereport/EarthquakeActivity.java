/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>>{

    /** The class name, for any log messages. */
    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    /** URL for earthquake data from the USGS dataset */
    private static final String USGS_URL =
            "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";

    /** TextView that is displayed when the list is empty */
    private TextView emptyStateTextView;

    /** ProgressBar indicator which shows a spinning wheel for an indeterminate time while data is
     * being loaded to the app from the USGS server.
      */
    private ProgressBar loadingIndicator;

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;

    /** Adapter for the list of earthquakes */
    private EarthquakeArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Setup the UI initially, which can then be updated with new data, each time the onCreate()
        // method is called
        setupUI();

        // If there is a network connection, initialize a loader, otherwise show message to explain
        // there is no connection on the empty state view
        if (hasConnection()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            // Display error
            // Set the loading indicator to invisible, so error message can be visible
            loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            emptyStateTextView.setText(R.string.no_internet);
        }

    }

    /**
     * Checks if there is an internet connection available.
     * @return true if there is a network connection available.
     */
    private boolean hasConnection() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //If there is a network connection, return true
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Create the ListView and Adapter which will be used to update the display.
     */
    private void setupUI() {
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        emptyStateTextView = (TextView) findViewById(R.id.empty_view);

        // Set the empty view that should be displayed when the list for the ListView is empty
        earthquakeListView.setEmptyView(emptyStateTextView);

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new EarthquakeArrayAdapter(this, new ArrayList<Earthquake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Earthquake currentEarthquake = mAdapter.getItem(position);
                String url = currentEarthquake.getUrl();

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(url);

                // Create an implicit intent to open a browser to the given URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                if (websiteIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(websiteIntent);
                }
            }
        });
    }

    /**
     * Create the {@link EarthquakeLoader} and pass the USGS URL so it knows where to retrieve the
     * data from. Note that this method is called by the initLoader() method, but is only
     * invoked when a loader (with the ID that was passed into the initLoader() as an argument)
     * does not exist.
     * @param id is the ID we gave to the loader to be called.
     * @param bundle are optional arguments that can be supplied by the caller.
     * @return a Loader for a list of {@link Earthquake}s.
     */
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle bundle) {
        // Create a new loader for the given URL
        return new EarthquakeLoader(this, USGS_URL);
    }

    /**
     * Handles the load after the {@link EarthquakeLoader} is done attempting to retrieve the data
     * from the USGS Earthquakes server. It updates the EarthquakeArrayAdapter with the data
     * from the Loader, so that it can be seen in the UI.
     * @param loader is the Loader that has finished attempting to retrieve data.
     * @param earthquakes is the list of {@link Earthquake}s from the USGS Earthquakes server.
     */
    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        // Update the TextView which should display the text explaining that "No Earthquakes found."
        // This text is updated here, as opposed to in the XML for the TextView because otherwise
        // the "No Earthquakes found." would display when starting the app, before the
        // list is populated.
        emptyStateTextView.setText(R.string.no_earthquakes);

        // Set the loading indicator to invisible after the loading is complete
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
    }

    /**
     * Called when the load from the {@link EarthquakeLoader} is no longer valid, and will clear
     * the data set of the adapter. (This isn't actually a callback that will occur often in this
     * app, however all implementations of {@link LoaderManager.LoaderCallbacks} require this
     * method.)
     * @param loader is the loader being reset.
     */
    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}
