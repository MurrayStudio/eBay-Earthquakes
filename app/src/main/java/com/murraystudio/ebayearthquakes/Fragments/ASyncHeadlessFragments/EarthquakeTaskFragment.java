package com.murraystudio.ebayearthquakes.Fragments.ASyncHeadlessFragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Author: Shamus Murray
 *
 * Launches an AsyncTask attached to a headless fragment (so we have no issues
 * with the task getting interrupted during an orientation change). This
 * Async task downloads the Json from our earthquake API.
 */
public class EarthquakeTaskFragment extends Fragment {
    /**
     * Callback interface through which the fragment will report the
     * task's progress and results back to the Activity.
     */
    public interface AsyncTaskCallbacks {
        void onPostExecute(String rawData);
    }

    private AsyncTaskCallbacks mCallbacks;
    private EarthquakePullTask mTask;

    //Earthquake JSON file URL
    private static final String JSON_URL = "http://api.geonames.org/earthquakesJSON?formatted=true&north=44.1&south=-9.9&east=-22.4&west=55.2&username=mkoppelman";

    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (AsyncTaskCallbacks) activity;
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

        // Create and execute the background task.
        mTask = new EarthquakePullTask();
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
    private class EarthquakePullTask extends AsyncTask<String, Void, String> {

        public HttpURLConnection getConnection(){
            HttpURLConnection hcon = null;
            try {
                hcon=(HttpURLConnection)new URL(JSON_URL).openConnection();
                hcon.setReadTimeout(30000); // Timeout at 30 seconds
                //hcon.setRequestProperty("User-Agent", "Alien V1.0");
            } catch (MalformedURLException e) {
                Log.e("getConnection()",
                        "Invalid URL: "+e.toString());
            } catch (IOException e) {
                Log.e("getConnection()",
                        "Could not connect: "+e.toString());
            }
            return hcon;
        }

        /**
         * Note that we do NOT call the callback object's methods
         * directly from the background thread, as this could result
         * in a race condition.
         */
        @Override
        protected String doInBackground(String... url) {
            HttpURLConnection hcon = getConnection();
            Log.i("Earthquake Task", "URL: " + hcon.getURL());
            if(hcon==null){
                Log.e("Remote Data", "hcon was null");
                return null;
            }
            try{
                StringBuffer sb=new StringBuffer(8192);
                String tmp="";
                BufferedReader br=new BufferedReader(
                        new InputStreamReader(
                                hcon.getInputStream()
                        )
                );
                while((tmp=br.readLine())!=null)
                    sb.append(tmp).append("\n");
                br.close();
                return sb.toString();
            }catch(IOException e){
                Log.d("READ FAILED", e.toString());
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String rawData) {
            if (mCallbacks != null) {
                mCallbacks.onPostExecute(rawData);
            }
        }
    }
}
