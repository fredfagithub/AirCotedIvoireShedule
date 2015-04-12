package com.fredericfagla.android.horaireaircotedivoire;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fredericfagla.android.horaireaircotedivoire.data.ScheduleContract.SheduleEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class FetchSheduleTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchSheduleTask.class.getSimpleName();
    private final Context mContext;

    public FetchSheduleTask(Context context) {
        mContext = context;Log.i("ENREGISTREMENT","LA");
    }

    /*Method calls for delete all record before insert news data*/
    private void deleteAllRecords() {
        mContext.getContentResolver().delete(
                SheduleEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                SheduleEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        cursor.close();
    }

    /**
     * Helper method to handle insertion of a new shedule in the shedule database.
     */

    /**
     * Take the String representing the complete shedule in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getSheduleDataFromJson(String sheduleJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        // Shedule information

        // Each day's shedule info is an element of the "list" array.
        final String OWM_LIST = "list";

        final String OWM_START_CITY = "start_city";
        final String OWM_ARRIVAL_CITY = "arrival_city";
        final String OWM_DATETEXT = "date";
        final String OWM_START_TIME = "start_time";
        final String OWM_ARRIVAL_TIME = "arrival_time";
        final String OWM_FLIGHT_NO = "flight_no";

        JSONObject sheduleJson = new JSONObject(sheduleJsonStr);
        JSONArray sheduleArray = sheduleJson.getJSONArray(OWM_LIST);

        // Get and insert the new shedule information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(sheduleArray.length());
        for(int i = 0; i < sheduleArray.length(); i++) {
            // These are the values that will be collected.

            String start_city;
            String arrival_city;
            String date;
            String start_time;
            String arrival_time;
            String flight_no;
            // Get the JSON object representing the day
            JSONObject dayShedule = sheduleArray.getJSONObject(i);

            start_city = dayShedule.getString(OWM_START_CITY);
            arrival_city = dayShedule.getString(OWM_ARRIVAL_CITY);
            date = dayShedule.getString(OWM_DATETEXT);
            start_time = dayShedule.getString(OWM_START_TIME);
            arrival_time = dayShedule.getString(OWM_ARRIVAL_TIME);
            flight_no = dayShedule.getString(OWM_FLIGHT_NO);

            ContentValues sheduleValues = new ContentValues();

            sheduleValues.put(SheduleEntry.COLUMN_START_CITY, start_city);
            sheduleValues.put(SheduleEntry.COLUMN_ARRIVAL_CITY, arrival_city);
            sheduleValues.put(SheduleEntry.COLUMN_DATETEXT, date);
            sheduleValues.put(SheduleEntry.COLUMN_START_TIME, start_time);
            sheduleValues.put(SheduleEntry.COLUMN_ARRIVAL_TIME, arrival_time);
            sheduleValues.put(SheduleEntry.COLUMN_FLIGHT_NO, flight_no);

            cVVector.add(sheduleValues);
        }
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(SheduleEntry.CONTENT_URI, cvArray);
            Log.i("ENREGISTREMENT","DONNEES ENREGISTREES");
        }
        else Log.i("ENREGISTREMENT","DONNEES NON ENREGISTREES");
    }

    @Override
    protected Void doInBackground(String... params) {
        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String sheduleJsonStr = null;

        String format = "json";

        try {
            // Construct the URL for the OpenShedule query
            final String SHEDULE_BASE_URL =
                    "http://www.barnusgbekide.com/flux/shedule.json";

            Uri builtUri = Uri.parse(SHEDULE_BASE_URL).buildUpon()
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            sheduleJsonStr = buffer.toString();

            if (sheduleJsonStr != ""){
                deleteAllRecords();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the shedule data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            getSheduleDataFromJson(sheduleJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        // This will only happen if there was an error getting or parsing the shedule.
        return null;
    }
}
