package com.murraystudio.ebayearthquakes.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.murraystudio.ebayearthquakes.R;

/*
 * Author: Shamus Murray
 *
 * Fragment handles the more detailed view of earthquake information (including a mapview)
 */
public class DetailedEarthquakeFragment extends Fragment {

    private float lat;
    private float lng;
    private float magnitude;
    private String date;
    private float depth;

    protected MapView mMapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        lat = getArguments().getFloat("latKey");
        lng = getArguments().getFloat("lngKey");
        magnitude = getArguments().getFloat("magKey");
        date = getArguments().getString("dateKey");
        depth = getArguments().getFloat("depthKey");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.earthquake_detail_view, container, false);

        TextView magTextView = (TextView) rootView.findViewById(R.id.magnitude);
        TextView dateTextview = (TextView) rootView.findViewById(R.id.date);
        TextView timeTextView = (TextView) rootView.findViewById(R.id.time);
        TextView loccationTextView = (TextView) rootView.findViewById(R.id.location);
        TextView depthTextView = (TextView) rootView.findViewById(R.id.depth);


        magTextView.setText("Richter Scale: " + Float.toString(magnitude));

        //parse date and time from date field in earthquake object
        int dateTextIndex = date.indexOf(" ");
        String dateText = date.substring(0, dateTextIndex);
        String timeText = date.substring(dateTextIndex + 1);

        dateTextview.setText(dateText);
        timeTextView.setText(timeText);
        loccationTextView.setText(Float.toString(lat) + ", " + Float.toString(lng));
        depthTextView.setText(Float.toString(depth));

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        //add marker to our map and pan to it
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Marker earthquakeMarker;
                earthquakeMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title("Hello world"));
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(earthquakeMarker.getPosition()));
            }
        });

        return rootView;
    }

    //below lifecycles methods are needed for mapview to function

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mMapView != null) {
            try {
                mMapView.onDestroy();
            } catch (NullPointerException e) {
                Log.e("DetailedEarthquakeFrag", "Error while attempting MapView.onDestroy(), ignoring exception", e);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

}
