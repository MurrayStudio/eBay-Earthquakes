package com.murraystudio.ebayearthquakes.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.murraystudio.ebayearthquakes.R;

/**
 * Created by sushi_000 on 4/12/2017.
 */

public class DetailedEarthquakeFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        float lat = getArguments().getFloat("latKey");
        float lng = getArguments().getFloat("lngKey");
        float magnitude = getArguments().getFloat("magKey");
        String place = getArguments().getString("placeKey");
        String date = getArguments().getString("dateKey");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.earthquake_detail_view, container, false);

        Log.i("Create Fragment", "Create Fragment");
        return rootView;
    }
}
