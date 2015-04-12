package com.fredericfagla.android.horaireaircotedivoire;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fredericfagla.android.horaireaircotedivoire.data.ScheduleContract;
import com.fredericfagla.android.horaireaircotedivoire.data.ScheduleContract.SheduleEntry;

/**
 * Created by frederic on 03/03/2015.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

/************************************Declare variables*********************************************/
    private TextView detail_view;
    private String LOG_TAG = DetailFragment.class.getSimpleName();
    private String SHEDULE_SHARE_HASHTAG = "#HoraireAirCotedIvoireApp";

    private ShareActionProvider mShareActionProvider;
    private String mShedule;
    private Long mShedule_id;

    private static final int DETAIL_LOADER = 0;

    private static final String[] SHEDULE_COLUMNS = {
            SheduleEntry._ID,
            SheduleEntry.COLUMN_START_CITY,
            SheduleEntry.COLUMN_ARRIVAL_CITY,
            SheduleEntry.COLUMN_DATETEXT,
            SheduleEntry.COLUMN_START_TIME,
            SheduleEntry.COLUMN_ARRIVAL_TIME,
            SheduleEntry.COLUMN_FLIGHT_NO,
    };
/************************************End declare variables*****************************************/

/********************************Declare textView**************************************************/
    private TextView date_shedule_textView;
    private TextView flight_numb_textView;
    private TextView start_city_value_textView;
    private TextView arrival_city_value_textView;
    private TextView start_time_textView;
    private TextView arrival_time_textView;
/********************************End declare textView**********************************************/

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

/************************************Overrides methods*********************************************/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if(mShedule_id != null){
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);}
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment,menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if(mShedule != null){//Log.d("mShedule", mShedule);
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
        else {
            Log.i(LOG_TAG, "share action provider is null");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mShedule_id = Long.parseLong(arguments.getString(DetailActivity.SHEDULE_ID));
            Log.d("ID VALUE",String.valueOf(mShedule_id) );
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

/********************************Reference TextView*********************************************/
        date_shedule_textView = (TextView)rootView.findViewById(R.id.date_shedule_textView);
        flight_numb_textView = (TextView)rootView.findViewById(R.id.flight_numb_textView);
        start_city_value_textView = (TextView)rootView.findViewById(R.id.start_city_value_textView);
        arrival_city_value_textView = (TextView)rootView.findViewById(R.id.arrival_city_value_textView);
        start_time_textView = (TextView)rootView.findViewById(R.id.start_time_textView);
        arrival_time_textView = (TextView)rootView.findViewById(R.id.arrival_time_textView);
/********************************End instantiate imageButton***************************************/

        //detail_view = (TextView)rootView.findViewById(R.id.test);
        //Intent intent = getActivity().getIntent();
        //if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
        //    detail = intent.getStringExtra(Intent.EXTRA_TEXT);
        //    detail_view.setText(detail);
        //}
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri sheduleUri = ScheduleContract.SheduleEntry.buildSheduleUri(mShedule_id);
        Log.d("SHEDULE URI",String.valueOf(sheduleUri) );

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                sheduleUri,
                SHEDULE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.d("DETAIL DONE",String.valueOf(data.getCount()) );
        if (data != null && data.moveToFirst()) {

            // Read flight_numb from cursor
            String flight_numb = data.getString(data.getColumnIndex(SheduleEntry.COLUMN_FLIGHT_NO));
            flight_numb_textView.setText(flight_numb);

            // Read start_city from cursor
            String start_city = data.getString(data.getColumnIndex(SheduleEntry.COLUMN_START_CITY));
            start_city_value_textView.setText(start_city);

            // Read arrival_city from cursor
            String arrival_city = data.getString(data.getColumnIndex(SheduleEntry.COLUMN_ARRIVAL_CITY));
            arrival_city_value_textView.setText(arrival_city);

            // Read start_time from cursor
            String start_time = data.getString(data.getColumnIndex(SheduleEntry.COLUMN_START_TIME));
            start_time_textView.setText(start_time);

            // Read arrival_time from cursor
            String arrival_time = data.getString(data.getColumnIndex(SheduleEntry.COLUMN_ARRIVAL_TIME));
            arrival_time_textView.setText(arrival_time);

            // Read date_shedule from cursor
            String date_shedule = data.getString(data.getColumnIndex(SheduleEntry.COLUMN_DATETEXT));
            date_shedule_textView.setText(Utility.localDate(date_shedule));

            // We still need this for the share intent
            mShedule = String.format("%s - %s/%s - %s/%s - %s", "Date: "+Utility.defaultLocalFormat(date_shedule), "Start City: "+start_city, start_time, "Arrival City: "+arrival_city, arrival_time, "Flight No: "+flight_numb);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
/************************************And overrides methods*****************************************/

/*******************************************Methods************************************************/
    //This method will allow us to share the data through another application
    private Intent createShareForecastIntent(){
        Intent shareIntent = new Intent((Intent.ACTION_SEND));
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShedule + "\n" +SHEDULE_SHARE_HASHTAG);
        return  shareIntent;
    }
/*******************************************End methods********************************************/
}
