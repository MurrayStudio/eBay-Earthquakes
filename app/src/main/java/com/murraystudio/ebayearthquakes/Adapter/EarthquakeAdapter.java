package com.murraystudio.ebayearthquakes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.murraystudio.ebayearthquakes.Model.Earthquake;
import com.murraystudio.ebayearthquakes.R;

import java.util.ArrayList;

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
        View rowView = mInflater.inflate(R.layout.earthquake_view, viewGroup, false);

        return rowView;
    }
}