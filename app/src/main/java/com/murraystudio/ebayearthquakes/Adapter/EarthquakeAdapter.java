package com.murraystudio.ebayearthquakes.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.murraystudio.ebayearthquakes.Model.Earthquake;
import com.murraystudio.ebayearthquakes.R;

import java.util.ArrayList;

import static com.murraystudio.ebayearthquakes.R.id.place;

/*
 * Author: Shamus Murray
 *
 * Adapter for our listview that display Earthquake information
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

        //make sure we have data to populate
        if(mDataSource.size() > 0) {

            TextView magnitudeText = (TextView) view.findViewById(R.id.Mag);
            TextView latLngText = (TextView) view.findViewById(R.id.lat_long);
            TextView dateText = (TextView) view.findViewById(R.id.time);
            TextView placeText = (TextView) view.findViewById(place);

            magnitudeText.setText(Float.toString(mDataSource.get(i).getMagnitude()));
            //if magnitude is over 8.0, color red to signify extra dangerous
            if(mDataSource.get(i).getMagnitude() >= 8.0){
                magnitudeText.setTextColor(mContext.getResources().getColor(R.color.colorHighMagnitude));
            }
            //otherwise color dark grey
            else{
                magnitudeText.setTextColor(Color.DKGRAY);
            }

            latLngText.setText(Float.toString(mDataSource.get(i).getLat()) + ", " + Float.toString(mDataSource.get(i).getLng()));
            dateText.setText(mDataSource.get(i).getDate());

            //if our GeoCoder found a place, set it as place text.
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
