package com.murraystudio.ebayearthquakes;

import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.murraystudio.ebayearthquakes.Fragments.DetailedEarthquakeFragment;
import com.murraystudio.ebayearthquakes.Fragments.EarthquakeListFragment;
import com.murraystudio.ebayearthquakes.Fragments.ASyncHeadlessFragments.EarthquakeTaskFragment;
import com.murraystudio.ebayearthquakes.Fragments.ASyncHeadlessFragments.GeoCodeTaskFragment;
import com.murraystudio.ebayearthquakes.Model.Earthquake;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/*
 * Author: Shamus Murray
 *
 * Earthquake data is collected here and appropriately given to UI fragments.
 */
public class MainActivity extends AppCompatActivity implements EarthquakeTaskFragment.AsyncTaskCallbacks, GeoCodeTaskFragment.GeoCodeAsyncTaskCallbacks {

    public static final String TAG_EARTHQUAKE_TASK_FRAGMENT = "earthquake_task_fragment";
    public static final String TAG_GEOCODE_TASK_FRAGMENT = "geo_code_task_fragment";
    public static final String TAG__EARTHQUAKE_LIST_FRAGMENT = "_earthquake_list_fragment";

    private EarthquakeTaskFragment mEarthquakeTaskFragment;
    private GeoCodeTaskFragment mGeoCodeTaskFragment;
    private DetailedEarthquakeFragment mDetailedEarthquakeFragment;
    private EarthquakeListFragment mEarthquakeListFragment;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    //contains Earthquake objects made from JSON file
    private ArrayList<Earthquake> mDataSourceMainActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Exit app if we aren't online
        if (!isOnline()) {
            Toast.makeText(this, "Please connect to the internet.", Toast.LENGTH_LONG).show();
            finish();
        }

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        mDataSourceMainActivity = new ArrayList<Earthquake>();

        //if we had a configuration change, repopulate values
        if (savedInstanceState != null) {
            restoreInstance(savedInstanceState);
        }

        FragmentManager fm = getFragmentManager();
        mEarthquakeTaskFragment = (EarthquakeTaskFragment) fm.findFragmentByTag(TAG_EARTHQUAKE_TASK_FRAGMENT);
        mEarthquakeListFragment = (EarthquakeListFragment) fm.findFragmentByTag(TAG__EARTHQUAKE_LIST_FRAGMENT);

        //Launches an AsyncTask attached to a headless fragment (so we have no issues
        //with the task getting interrupted during an orientation change). This
        //Async task downloads the Json from our earthquake API.
        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mEarthquakeTaskFragment == null) {
            mEarthquakeTaskFragment = new EarthquakeTaskFragment();
            fm.beginTransaction().add(mEarthquakeTaskFragment, TAG_EARTHQUAKE_TASK_FRAGMENT).commit();
        }
        //This is a fully functional fragment containing a listview to display our earthquake objects
        if (mEarthquakeListFragment == null) {
            mEarthquakeListFragment = new EarthquakeListFragment();
            fm.beginTransaction().add(R.id.fragment_container, mEarthquakeListFragment, TAG__EARTHQUAKE_LIST_FRAGMENT).commit();
        } else {
            //if non-null, update adapter with restored values.
            mEarthquakeListFragment.updateAdapter(mDataSourceMainActivity);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveInstance(outState);
        super.onSaveInstanceState(outState);
    }

    // Method below is called by the EarthquakeTaskFragment when new
    // progress updates or results are available. Supplied is a JSON
    //file in string form which is parsed into Earthquake objects.
    @Override
    public void onPostExecute(String rawData) {
        String raw = rawData;

        FragmentManager fm = getFragmentManager();
        try {
            JSONObject parent = new JSONObject(raw);
            JSONArray jsonData = parent.getJSONArray("earthquakes");

            for (int i = 0; i < jsonData.length(); i++) {
                JSONObject cur = jsonData.getJSONObject(i);

                Earthquake earthquake = new Earthquake();

                earthquake.setDate(cur.optString("datetime"));
                earthquake.setdepth((float) cur.optDouble("depth"));
                earthquake.setMagnitude((float) cur.optDouble("magnitude"));
                earthquake.setLng((float) cur.optDouble("lng"));
                earthquake.setLat((float) cur.optDouble("lat"));

                mGeoCodeTaskFragment = (GeoCodeTaskFragment) fm.findFragmentByTag(TAG_GEOCODE_TASK_FRAGMENT);

                //Create another ASyncTask that gets Geo-location based off
                //the lat and long coordinates from the JSON file.
                // If the Fragment is non-null, then it is currently being
                // retained across a configuration change.
                if (mGeoCodeTaskFragment == null) {
                    mGeoCodeTaskFragment = new GeoCodeTaskFragment();

                    Bundle args = new Bundle();
                    args.putFloat("latKey", (float) cur.optDouble("lat"));
                    args.putFloat("lngKey", (float) cur.optDouble("lng"));
                    args.putInt("IDKey", i);
                    mGeoCodeTaskFragment.setArguments(args);

                    fm.beginTransaction().add(mGeoCodeTaskFragment, TAG_GEOCODE_TASK_FRAGMENT).commit();
                }

                //if not null, add earthquake object to the list of earthquake objects
                if (earthquake.getDate() != null) {
                    mDataSourceMainActivity.add(earthquake);
                }
            }

            //update listview adapter with new data
            mEarthquakeListFragment.updateAdapter(mDataSourceMainActivity);


        } catch (Exception e) {
            Log.e("fetchPosts()", e.toString());
        }

    }

    //callback to get geocode information from GeoCodeTaskFragment
    @Override
    public void onPostExecuteGeoCode(String place) {
        if (place != null) {
            int placeNameIndex = place.indexOf(";");
            String placeName = place.substring(0, placeNameIndex);
            String placeID = place.substring(placeNameIndex + 1);
            int placeIDInt = Integer.parseInt(placeID);

            mDataSourceMainActivity.get(placeIDInt).setPlace(placeName);

            //update listview adapter with new data
            mEarthquakeListFragment.updateAdapter(mDataSourceMainActivity);
        }
    }

    //launch a DetailedEarthquakeFragment when clicking on listview item.
    public void earthQuakeListOnClick(int position) {
        FragmentManager fm = getFragmentManager();

        mDetailedEarthquakeFragment = new DetailedEarthquakeFragment();

        Bundle args = new Bundle();
        args.putFloat("latKey", mDataSourceMainActivity.get(position).getLat());
        args.putFloat("lngKey", mDataSourceMainActivity.get(position).getLng());
        args.putFloat("magKey", mDataSourceMainActivity.get(position).getMagnitude());
        args.putString("dateKey", mDataSourceMainActivity.get(position).getDate());
        args.putString("placeKey", mDataSourceMainActivity.get(position).getPlace());
        args.putFloat("depthKey", mDataSourceMainActivity.get(position).getdepth());
        mDetailedEarthquakeFragment.setArguments(args);

        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.fragment_container, mDetailedEarthquakeFragment).addToBackStack(null).commit();

        if (mDataSourceMainActivity.get(position).getPlace() != null) {
            collapsingToolbarLayout.setTitle(mDataSourceMainActivity.get(position).getPlace());
        } else {
            collapsingToolbarLayout.setTitle("Unknown Location");
        }

        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBarLayout.setExpanded(false);

    }

    private void restoreInstance(Bundle savedInstanceState){
        if(savedInstanceState.getFloatArray("magKey") != null) {
            float[] magValues = savedInstanceState.getFloatArray("magKey");
            float[] latValues = savedInstanceState.getFloatArray("latKey");
            float[] lngValues = savedInstanceState.getFloatArray("lngKey");
            String[] dateValues = savedInstanceState.getStringArray("dateKey");
            String[] placeValues = savedInstanceState.getStringArray("placeKey");
            float[] depthValues = savedInstanceState.getFloatArray("depthKey");
            for (int i = 0; i < magValues.length; i++) {
                Earthquake earthquake = new Earthquake();

                earthquake.setDate(dateValues[i]);
                earthquake.setMagnitude(magValues[i]);
                earthquake.setLng(lngValues[i]);
                earthquake.setLat(latValues[i]);
                earthquake.setPlace(placeValues[i]);
                earthquake.setdepth(depthValues[i]);

                if (earthquake.getDate() != null) {
                    mDataSourceMainActivity.add(earthquake);
                }
            }
        }
    }

    private void saveInstance(Bundle outState){
        float[] magValues = new float[mDataSourceMainActivity.size()];
        float[] latValues = new float[mDataSourceMainActivity.size()];
        float[] lngValues = new float[mDataSourceMainActivity.size()];
        String[] dateValues = new String[mDataSourceMainActivity.size()];
        String[] placeValues = new String[mDataSourceMainActivity.size()];
        float[] depthValues = new float[mDataSourceMainActivity.size()];


        for (int i = 0; i < mDataSourceMainActivity.size(); i++) {
            magValues[i] = mDataSourceMainActivity.get(i).getMagnitude();
            latValues[i] = mDataSourceMainActivity.get(i).getLat();
            lngValues[i] = mDataSourceMainActivity.get(i).getLng();
            dateValues[i] = mDataSourceMainActivity.get(i).getDate();
            placeValues[i] = mDataSourceMainActivity.get(i).getPlace();
            depthValues[i] = mDataSourceMainActivity.get(i).getdepth();
        }

        outState.putFloatArray("magKey", magValues);
        outState.putFloatArray("latKey", latValues);
        outState.putFloatArray("lngKey", lngValues);
        outState.putStringArray("dateKey", dateValues);
        outState.putStringArray("placeKey", placeValues);
        outState.putFloatArray("depthKey", depthValues);
    }

    //if we press the back button, we have returned to main screen so
    //change text back to app's title.
    @Override
    public void onBackPressed() {
        collapsingToolbarLayout.setTitle("eBay Earthquakes");
        super.onBackPressed();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
