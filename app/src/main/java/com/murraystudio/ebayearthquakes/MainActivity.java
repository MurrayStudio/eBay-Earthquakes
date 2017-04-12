package com.murraystudio.ebayearthquakes;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.murraystudio.ebayearthquakes.Adapter.EarthquakeAdapter;
import com.murraystudio.ebayearthquakes.Fragments.EarthquakeTaskFragment;
import com.murraystudio.ebayearthquakes.Fragments.GeoCodeTaskFragment;
import com.murraystudio.ebayearthquakes.Model.Earthquake;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.R.attr.value;

public class MainActivity extends AppCompatActivity implements EarthquakeTaskFragment.AsyncTaskCallbacks, GeoCodeTaskFragment.GeoCodeAsyncTaskCallbacks {

    private ListView mListView;

    private static final String TAG_EARTHQUAKE_TASK_FRAGMENT = "earthquake_task_fragment";
    private EarthquakeTaskFragment mEarthquakeTaskFragment;

    private ArrayList<Earthquake> mDataSourceMainActivity;

    private EarthquakeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mDataSourceMainActivity = new ArrayList<Earthquake>();

        if (savedInstanceState != null) {
            float[] magValues = savedInstanceState.getFloatArray("magKey");
            float[] latValues = savedInstanceState.getFloatArray("latKey");
            float[] lngValues = savedInstanceState.getFloatArray("lngKey");
            String[] dateValues = savedInstanceState.getStringArray("dateKey");
            for(int i = 0; i < magValues.length; i++){
                Earthquake earthquake = new Earthquake();

                earthquake.setDate(dateValues[i]);
                earthquake.setMagnitude(magValues[i]);
                earthquake.setLng(lngValues[i]);
                earthquake.setLat(latValues[i]);

                if(earthquake.getDate() != null) {
                    mDataSourceMainActivity.add(earthquake);
                }
            }


        }

        FragmentManager fm = getFragmentManager();
        mEarthquakeTaskFragment = (EarthquakeTaskFragment) fm.findFragmentByTag(TAG_EARTHQUAKE_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mEarthquakeTaskFragment == null) {
            mEarthquakeTaskFragment = new EarthquakeTaskFragment();
            fm.beginTransaction().add(mEarthquakeTaskFragment, TAG_EARTHQUAKE_TASK_FRAGMENT).commit();
        }


        mListView = (ListView) findViewById(R.id.earthquake_list_view);
        //allows collapsing toolbar to function when scrolling
        mListView.setNestedScrollingEnabled(true);

        adapter = new EarthquakeAdapter(this, mDataSourceMainActivity);
        mListView.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        float[] magValues = new float[mDataSourceMainActivity.size()];
        float[] latValues = new float[mDataSourceMainActivity.size()];
        float[] lngValues = new float[mDataSourceMainActivity.size()];
        String[] dateValues = new String[mDataSourceMainActivity.size()];


        for(int i = 0; i < mDataSourceMainActivity.size(); i++) {
            magValues[i] = mDataSourceMainActivity.get(i).getMagnitude();
            latValues[i] = mDataSourceMainActivity.get(i).getLat();
            lngValues[i] = mDataSourceMainActivity.get(i).getLng();
            dateValues[i] = mDataSourceMainActivity.get(i).getDate();
        }

        outState.putFloatArray("magKey", magValues);
        outState.putFloatArray("latKey", latValues);
        outState.putFloatArray("lngKey", lngValues);
        outState.putStringArray("dateKey", dateValues);

    }

    // method below are called by the EarthquakeTaskFragment when new
    // progress updates or results are available. The MainActivity
    // should respond by updating its UI to indicate the change.

    @Override
    public void onPostExecute(String rawData) {
        String raw = rawData;
        try{
            JSONObject parent = new JSONObject(raw);
            JSONArray jsonData = parent.getJSONArray("earthquakes");

            for(int i=0; i < jsonData.length(); i++) {
                JSONObject cur = jsonData.getJSONObject(i);

                Earthquake earthquake = new Earthquake();

                earthquake.setDate(cur.optString("datetime"));
                earthquake.setdepth((float) cur.optDouble("depth"));
                earthquake.setMagnitude((float) cur.optDouble("magnitude"));
                earthquake.setLng((float) cur.optDouble("lng"));
                earthquake.setLat((float) cur.optDouble("lat"));


                Bundle args = new Bundle();
                args.putLong("key", value);
                yourFragment.setArguments(args);

                if(earthquake.getDate() != null) {
                    mDataSourceMainActivity.add(earthquake);
                }
            }


            ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();


        }catch(Exception e){
            Log.e("fetchPosts()",e.toString());
        }

    }

    @Override
    public void onPostExecuteGeoCode(String place) {

    }
}
