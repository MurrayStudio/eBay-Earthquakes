package com.murraystudio.ebayearthquakes.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.murraystudio.ebayearthquakes.Adapter.EarthquakeAdapter;
import com.murraystudio.ebayearthquakes.MainActivity;
import com.murraystudio.ebayearthquakes.Model.Earthquake;
import com.murraystudio.ebayearthquakes.R;

import java.util.ArrayList;

/*
 * Author: Shamus Murray
 *
 * Fragment that handles the listview UI of all earthquake objects
 */
public class EarthquakeListFragment extends Fragment {

    private ArrayList<Earthquake> mDataSourceMainActivity;

    private EarthquakeAdapter adapter;
    private ListView mListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataSourceMainActivity = new ArrayList<Earthquake>();

        //keeps our onCreateView IDs intact
        setRetainInstance(true);

        adapter = new EarthquakeAdapter(getContext(), mDataSourceMainActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_view_earthquakes, container, false);
        rootView.setTag(MainActivity.TAG__EARTHQUAKE_LIST_FRAGMENT);

        mListView = (ListView) rootView.findViewById(R.id.earthquake_list_view);

        //allows collapsing toolbar to function when scrolling
        mListView.setNestedScrollingEnabled(true);
        mListView.setAdapter(adapter);

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //callback to MainActivity
                ((MainActivity)getActivity()).earthQuakeListOnClick(i);
            }
        };

        mListView.setOnItemClickListener (listener);


        return rootView;
    }

    //any updates to our earthquake objects from MainActivity pass through here
    //and the adapter for listview is notified of the dataset change.
    public void updateAdapter(ArrayList<Earthquake> mDataSourceMainActivity){
        this.mDataSourceMainActivity.clear();
        this.mDataSourceMainActivity.addAll(mDataSourceMainActivity);
        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }
}
