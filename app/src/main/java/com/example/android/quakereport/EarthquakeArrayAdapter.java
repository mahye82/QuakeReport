package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An {@link EarthquakeArrayAdapter} knows how to create a list item layout for each earthquake
 * in the data source (a list of {@link Earthquake} objects).
 *
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class EarthquakeArrayAdapter extends ArrayAdapter<Earthquake> {

    /**
     * The part of the location string from the USGS service that we use to determine
     * whether or not there is a location offset present ("5km N of Cairo, Egypt").
     */
    private static final String LOCATION_SEPARATOR = " of ";

    /**
     * Constructs a new {@link EarthquakeArrayAdapter}.
     *
     * @param context of the app
     * @param earthquakes is the list of earthquakes, which is the data source of the adapter
     */
    public EarthquakeArrayAdapter (Context context, List<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    /**
     * Returns a list item view that displays information about the earthquake at the given position
     * in the list of earthquakes.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.earthquake_list_item, parent, false);
        }

        // Get the earthquake data for the list item position we want to update
        Earthquake currentEarthquake = getItem(position);

        // Get the TextView for the magnitude
        TextView magnitudeView = (TextView) listItemView.findViewById(R.id.magnitude);

        // Get the magnitude for this earthquake and then format it to 1 decimal place
        String magnitude = formatMagnitude(currentEarthquake.getMagnitude());

        // Update the magnitude TextView with the correct data
        magnitudeView.setText(magnitude);

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        // Get the TextView for the location offset
        TextView offsetView = (TextView) listItemView.findViewById(R.id.location_offset);

        // Get the TextView for the primary location
        TextView primaryLocationView = (TextView) listItemView.findViewById(R.id.primary_location);

        // Get the full string for the location where the earthquake occurred - both primary
        // location and offset
        String originalLocation = currentEarthquake.getLocation();
        String primaryLocation;
        String locationOffset;

        // If the original location says " of " in it, set the primary location and offset
        // accordingly. Otherwise, set the offset to say "Near the" and make the primary
        // location the same as the original location.
        if (originalLocation.contains(LOCATION_SEPARATOR)) {
            String[] parts = originalLocation.split(LOCATION_SEPARATOR);
            locationOffset = parts[0] + LOCATION_SEPARATOR;
            primaryLocation = parts[1];
        } else {
            locationOffset = getContext().getString(R.string.near_the);
            primaryLocation = originalLocation;
        }

        // Update the TextView for the primary location
        primaryLocationView.setText(primaryLocation);

        // Update the TextView for the offset of the location
        offsetView.setText(locationOffset);

        // Get the TextView for the date
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);

        // Get the TextView for the time
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);

        // Get the Date of the earthquake
        Date dateOfEarthquake = new Date(currentEarthquake.getTimeInMilliseconds());

        // Format the Date object for display
        String date = formatDate(dateOfEarthquake);
        String time = formatTime(dateOfEarthquake);

        // Update the TextViews for date and time with the new earthquake data
        dateView.setText(date);
        timeView.setText(time);

        return listItemView;
    }

    /**
     * Return an integer representing the color that the circle background should have, from the
     * magnitude given.
     * @param magnitude is the size of the earthquake.
     * @return the color as an integer in the form 0xAARRGGBB, for the magnitude given
     */
    private int getMagnitudeColor(double magnitude) {
        // Round down to the largest integer that is less than or equal to the magnitude
        // This means values like 1.69, 1.99, etc are rounded down to 1
        int magnitudeColorResourceID;
        int magnitudeFloor = (int) Math.floor(magnitude);

        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceID = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceID = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceID = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceID = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceID = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceID = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceID = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceID = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceID = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceID = R.color.magnitude10plus;
                break;
        }

        // This needs to be converted into a color value, because there exists no
        // setColor(int resID) method
        return ContextCompat.getColor(getContext(), magnitudeColorResourceID);
    }

    /**
     * Return a formatted String (i.e. Mar 3, 1982) representing the date from a Date object.
     * @param date - The Date object to be formatted
     * @return the date as a String
     */
    private String formatDate(Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM DD, yyyy");
        return dateFormatter.format(date);
    }

    /**
     * Return a formatted String (i.e. 3:00 PM) representing the time from a Date object.
     * @param date - the Date object to be formatted
     * @return - the time of the Date object as a String
     */
    private String formatTime(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(date);
    }

    /**
     * Return the formatted magnitude string showing 1 decimal place (i.e. "3.2")
     * from a decimal magnitude value.
     * @return a String representing the magnitude to 1 decimal place.
     */
    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }
}
