package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {
    /* Query URLs */
    private String[] urls;

    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    /**
     * Constructs a new {@link EarthquakeLoader}.
     *
     * @param context of the activity
     * @param urls to load data from
     */
    public EarthquakeLoader(Context context, String... urls) {
        super(context);
        this.urls = urls;
    }

    /**
     * Calls the loadInBackground() method after the Loader is started.
     */
    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG, "onStartLoading()");
        forceLoad();
    }

    /**
     * On the background thread, this method performs the network request, parses the JSON response,
     * and returns a list of {@link Earthquake}s.
     * @return a list of {@link Earthquake}s.
     */
    @Override
    public List<Earthquake> loadInBackground() {
        Log.v(LOG_TAG, "loadInBackground()");

        // Don't perform the request if there are no URLs, or the first URL is null
        if (urls.length < 1 || urls[0] == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes
        return QueryUtils.fetchEarthquakeData(urls[0]);
    }
}
