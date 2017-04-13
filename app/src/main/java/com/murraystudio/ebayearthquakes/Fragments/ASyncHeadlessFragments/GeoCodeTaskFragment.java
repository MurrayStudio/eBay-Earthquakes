package com.murraystudio.ebayearthquakes.Fragments.ASyncHeadlessFragments;

import android.app.Activity;
import android.app.Fragment;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/*
 * Author: Shamus Murray
 *
 * AsyncTask that that gets Geo-location based off
 * the lat and long coordinates from the JSON file.
 */
public class GeoCodeTaskFragment extends Fragment {
    /**
     * Callback interface through which the fragment will report the
     * task's progress and results back to the Activity.
     */
    public interface GeoCodeAsyncTaskCallbacks {
        void onPostExecuteGeoCode(String place);
    }

    private GeoCodeAsyncTaskCallbacks mCallbacks;
    private GeoCodePullTask mTask;


    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (GeoCodeAsyncTaskCallbacks) activity;
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        float lat = getArguments().getFloat("latKey");
        float lng = getArguments().getFloat("lngKey");
        int ID = getArguments().getInt("IDKey");

        // Create and execute the background task.
        mTask = new GeoCodePullTask(lat, lng, ID);
        mTask.execute();
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /**
     * A dummy task that performs some (dumb) background work and
     * proxies progress updates and results back to the Activity.
     *
     * Note that we need to check if the callbacks are null in each
     * method in case they are invoked after the Activity's and
     * Fragment's onDestroy() method have been called.
     */
    public class GeoCodePullTask extends AsyncTask<String, Void, String> {

        private float lat;
        private float lng;
        private int ID;

        public GeoCodePullTask(float lat, float lng, int ID){
            this.lat = lat;
            this.lng = lng;
            this.ID = ID;
        }

        private String getGeoLocation(float lat, float lng) throws IOException {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getContext(), Locale.getDefault());

            double latDouble = lat;
            double lngDouble = lng;

            addresses = geocoder.getFromLocation(latDouble, lngDouble, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if(addresses.size() > 0) {
                //return country name (if exists)
                String country = addresses.get(0).getCountryName();
                return  country + " ;" + ID;
            }
            else {
                return null;
            }
        }


        @Override
        protected String doInBackground(String... url) {
            try {
                return getGeoLocation(lat, lng);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String place) {
            if (mCallbacks != null) {
                mCallbacks.onPostExecuteGeoCode(place);
            }
        }
    }
}
