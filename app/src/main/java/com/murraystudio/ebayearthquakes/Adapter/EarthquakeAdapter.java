package com.murraystudio.ebayearthquakes.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.murraystudio.ebayearthquakes.Model.Earthquake;
import com.murraystudio.ebayearthquakes.R;

import java.util.ArrayList;

import static com.murraystudio.ebayearthquakes.R.id.place;

/**
 * Created by sushi_000 on 4/11/2017.
 */

public class EarthquakeAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Earthquake> mDataSource;

    public EarthquakeAdapter(Context context, ArrayList<Earthquake> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int i) {
        return mDataSource.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        // Get view for row item
        if(view==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(R.layout.earthquake_view, viewGroup, false);
        }

        if(mDataSource.size() > 0) {

            TextView magnitudeText = (TextView) view.findViewById(R.id.Mag);
            TextView latLngText = (TextView) view.findViewById(R.id.lat_long);
            TextView dateText = (TextView) view.findViewById(R.id.date);
            TextView placeText = (TextView) view.findViewById(place);

            magnitudeText.setText(Float.toString(mDataSource.get(i).getMagnitude()));
            latLngText.setText(Float.toString(mDataSource.get(i).getLat()) + ", " + Float.toString(mDataSource.get(i).getLng()));
            dateText.setText(mDataSource.get(i).getDate());

            if(mDataSource.get(i).getPlace() != null){
                placeText.setText(mDataSource.get(i).getPlace());
            }
            else{
                placeText.setText("Unknown Location");
            }

        }


        return view;
    }

}
