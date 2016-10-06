package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    private static final String LOG_TAG = "QueryUtils";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS dataset and return a list of {@link Earthquake} objects.
     * @param requestUrl is the URL in String format, from which the earthquake data
     *                   should be fetched.
     */
    public static List<Earthquake> fetchEarthquakeData(String requestUrl) {

        // Create URL object
        URL url = createURL(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and return a list of {@link Earthquake}s
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     * @param jsonResponse is the String that needs to be parsed.
     */
    private static List<Earthquake> extractFeatureFromJson(String jsonResponse) {

        // Create an empty List that we can start adding earthquakes to
        List<Earthquake> earthquakes = new ArrayList<>();

        // if there's no JSON string to parse, there's no point trying to parse it. Finish early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return earthquakes;
        }

            // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
            // is formatted, a JSONException exception object will be thrown.
            // Catch the exception so the app doesn't crash, and print the error message to the logs
            try {

                // Parse the response given by the jsonResponse string and
                // build up a list of Earthquake objects with the corresponding data.

                // Get the root node
                JSONObject root = new JSONObject(jsonResponse);
                // Get the JSONArray for the key called "features"
                JSONArray featuresArray = root.optJSONArray("features");

                // For each element in the features array, do the following
                for (int i = 0; i < featuresArray.length(); i++) {
                    // Get the JSONObject representing a particular earthquake
                    JSONObject earthquake = featuresArray.optJSONObject(i);

                    // Get the JSONObject representing the properties of that particular earthquake
                    JSONObject earthquakeProperties = earthquake.optJSONObject("properties");

                    // Get the magnitude, location, time, URL (of USGS map) for this earthquake
                    double magnitude = earthquakeProperties.optDouble("mag");
                    String location = earthquakeProperties.optString("place");
                    long time = earthquakeProperties.optLong("time");
                    String url = earthquakeProperties.optString("url");

                    // Create a new Earthquake and store in list of earthquakes
                    earthquakes.add(new Earthquake(location, magnitude, time, url));
                }

            } catch (JSONException e) {
                // If an error is thrown when executing any of the above statements in the "try"
                // block, catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
            }

        // Return the list of earthquakes
        return earthquakes;
    }

    /**
     * Creates a URL object from a given string.
     *
     * @param urlString - the string that needs to be turned into a URL.
     */
    private static URL createURL(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Incorrect URL", e);
        }

        return url;
    }

    /**
     * Create a HTTP request and return a String holding all the unparsed JSON data if successful
     * @param url - the URL at which the network request should be made to retrieve the earthquake
     *            data
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // The HTTP client which will act as a communications link between the application and a URL
        HttpURLConnection connection = null;
        // The stream that we will receive the data over if successful
        InputStream inputStream = null;

        try {
            // open a connection from the URL, set the request method and connect
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.connect();

            // if the response code is successful, proceed to read from stream
            // otherwise, return early
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                inputStream = connection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error Response Code: " + responseCode);
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error retrieving JSON response. Check internet connection?", e);
        } finally {
            // disconnect and close resources
            if (connection != null) {
                connection.disconnect();
            }

            if (inputStream != null) {
                // potentially throws IOException, hence method signature
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        // if there is an InputStream, create a BufferedReader to read from it into a StringBuilder
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // Read lines from the buffer, and update the StringBuilder
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }

            // close resources
            inputStreamReader.close();
            reader.close();
        }

        return output.toString();
    }


}