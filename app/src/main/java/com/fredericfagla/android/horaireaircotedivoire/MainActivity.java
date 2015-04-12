package com.fredericfagla.android.horaireaircotedivoire;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

public class MainActivity extends ActionBarActivity implements ScheduleFragment.Callback,
                                                                ChooseCityDialog.CityDialogListener,
                                                                ChooseDateDialog.DateDialogListener{

/********************************Declare variables*************************************************/
    String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane;
/********************************End declare variables*********************************************/

/************************************Overrides methods*********************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.shedule_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.shedule_detail_container, new DetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;}

        //SheduleFragment sheduleFragment =  ((SheduleFragment)getSupportFragmentManager()
        //       .findFragmentById(R.id.fragment_shedules));
        //if (savedInstanceState == null) {
        //    getSupportFragmentManager().beginTransaction()
        //            .add(R.id.fragment_shedules, new SheduleFragment())
        //            .commit();
        //}
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
        switch (id)
        {
            case R.id.action_map:
                //Start location of the departure city by default on a map
                openPreferredLocationInMap();
                break;

            case R.id.action_settings:
                //Start SettingsActivity activity
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.action_help:
                //Start HelpActivity
                startActivity(new Intent(this, HelpActivity.class));
                break;

            case R.id.action_about:
                //Start AboutActivity
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String shedule_id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putString(DetailActivity.SHEDULE_ID, shedule_id);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.shedule_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivity.SHEDULE_ID, shedule_id);
            startActivity(intent);
        }
    }

/************************************And overrides methods*****************************************/

/*******************************************Functions**********************************************/
        //Function return default City
        public String defaultCity(int key, int value){
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            String City = sharedPrefs.getString(
                    getString(key),
                    getString(value));
            return City;
        }

        //Location of the departure city by default on a map
        private void openPreferredLocationInMap() {
            String location = defaultCity(R.string.pref_start_city_key, R.string.pref_city_abidjan);
            // Using the URI scheme for showing a location found on a map.  This super-handy
            // intent can is detailed in the "Common Intents" page of Android's developer site:
            // http://developer.android.com/guide/components/intents-common.html#Maps
            Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                    .appendQueryParameter("q", location)
                    .build();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(geoLocation);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
            }
        }
/*******************************************End functions******************************************/

/************************************Overrides methods 2********************************************/
    /*************************Listeners for ChooseCityDialog***************************************/
    @Override
    public void onDialogCityPositiveClick(DialogFragment dialog, String city) {
        ScheduleFragment.change_city_value(city);
        Log.i("DialogCityPositiveClick", city);

    }

    @Override
    public void onDialogCityNegativeClick(DialogFragment dialog) {
        Log.i("DialogCityNegativeClick", "no action");
    }
    /*************************End listeners for ChooseCityDialog***********************************/

    /*************************Listeners for ChooseDateDialog***************************************/
    public void onDialogDateResetClick(DialogFragment dialog) {
    }

    @Override
    public void onDialogDateValidateClick(DatePicker view, Boolean Choice, int year, String monthOfYear, String dayOfMonth) {
        ScheduleFragment.change_date_value(Choice, year, monthOfYear, dayOfMonth);
        Log.i("DateDialog validate", Choice + "  " + year + "  " + monthOfYear + "  " + dayOfMonth);
    }
    /*************************End listeners for ChooseDateDialog***********************************/
/************************************End overrides methods 2***************************************/

}