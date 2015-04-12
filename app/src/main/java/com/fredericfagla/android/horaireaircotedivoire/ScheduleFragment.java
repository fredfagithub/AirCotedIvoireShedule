package com.fredericfagla.android.horaireaircotedivoire;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fredericfagla.android.horaireaircotedivoire.data.ScheduleContract.SheduleEntry;

import java.util.Locale;

/**
 * Created by frederic on 03/03/2015.
 */

/**
 * Encapsulates fetching the shedule and displaying it as a {@link android.widget.ListView} layout.
 */
public class ScheduleFragment extends Fragment implements LoaderCallbacks<Cursor>{

/********************************Declare variables*************************************************/
    static String TEST_CITY = ""; /*TEST_CITY uses for define if city is the start or arrival*/

    final String[] sheduleArray = {
            "HF500",
            "HF480",
            "HF540",
            "HF532",
            "HF480",
            "HF540",
            "HF532"
    };

//    List<String> listShedule = new ArrayList<String>(Arrays.asList(sheduleArray));
//    ArrayAdapter<String> mSheduleAdapter;

    private ScheduleAdapter mScheduleAdapter;

    private int mPosition = ListView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    private static final int SHEDULE_LOADER = 0; //To load all shedules for select cities
    private static final int SHEDULE_LOADER_MAX_PERIOD = 1; //To load max date period
    private static final int SHEDULE_LOADER_MIN_PERIOD = 2; //To load min date period

    // For the shedule view we're showing subset of the stored data.
    // Specify the columns we need.
    private static final String[] SHEDULE_COLUMNS = {
            SheduleEntry._ID,
            SheduleEntry.COLUMN_START_CITY,
            SheduleEntry.COLUMN_ARRIVAL_CITY,
            SheduleEntry.COLUMN_DATETEXT,
            SheduleEntry.COLUMN_START_TIME,
            SheduleEntry.COLUMN_ARRIVAL_TIME,
            SheduleEntry.COLUMN_FLIGHT_NO
    };

    //To take only max or min date, specify the date column
    private static final String[] SHEDULE_DATE = {
            SheduleEntry.COLUMN_DATETEXT,
    };

    // These indices are tied to SHEDULE_COLUMNS.  If SHEDULE_COLUMNS changes, these
    // must change.
    public static final int COL_SHEDULE_ID = 0;
    public static final int COL_SHEDULE_START_CITY = 1;
    public static final int COL_SHEDULE_ARRIVAL_CITY = 2;
    public static final int COL_SHEDULE_DATE = 3;
    public static final int COL_SHEDULE_START_TIME = 4;
    public static final int COL_SHEDULE_ARRIVAL_TIME = 5;
    public static final int COL_SHEDULE_FLIGHT_NO = 6;

    //FetchSheduleTask sheduleTask;
/********************************End declare variables*********************************************/

/********************************Declare imageButton***********************************************/
    ImageButton imageButton_startCity;
    ImageButton imageButton_dateTime;
    ImageButton imageButton_endCity;
/********************************End declare imageButton*******************************************/

/********************************Declare textView**************************************************/
    public static TextView textView_startCity;
    static TextView textView_dateTime;
    public static TextView textView_endCity;
    static TextView textView_start_date_period;
    static TextView textView_end_date_period;
/********************************End declare textView**********************************************/

/********************************Declare ListView**************************************************/
    //mListView contains items shedules
    ListView mListView;
/********************************End declare ListView**********************************************/

/********************************Declare LinearLayout**********************************************/
    static LinearLayout synchronizinglinearlayout;
/********************************End declare ListView**********************************************/

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String _id);
    }

    public ScheduleFragment(){
    }

/************************************Overrides methods*********************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.shedulefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_refresh:
                //Start refresh
                updateShedule();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //Load default start end arrival City in default preference
        String preference_start_city = displayDefaultCity(R.string.pref_start_city_key, R.string.pref_city_abidjan);
        String preference_end_city = displayDefaultCity(R.string.pref_end_city_key, R.string.pref_city_conakry);
        textView_startCity.setText(preference_start_city);
        textView_endCity.setText(preference_end_city);

        //Initialise loaders
        getLoaderManager().initLoader(SHEDULE_LOADER, null, this);
        getLoaderManager().initLoader(SHEDULE_LOADER_MAX_PERIOD, null, this);
        getLoaderManager().initLoader(SHEDULE_LOADER_MIN_PERIOD, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(SHEDULE_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

/********************************Reference imageButton*********************************************/
        imageButton_startCity = (ImageButton)rootView.findViewById(R.id.imageButton_startCity);
        imageButton_dateTime = (ImageButton)rootView.findViewById(R.id.imageButton_dateTime);
        imageButton_endCity = (ImageButton)rootView.findViewById(R.id.imageButton_endCity);
/********************************End instantiate imageButton***************************************/

/********************************Reference TextView*********************************************/
        textView_startCity = (TextView)rootView.findViewById(R.id.textView_startCity);
        textView_dateTime = (TextView)rootView.findViewById(R.id.textView_dateTime);
        textView_endCity = (TextView)rootView.findViewById(R.id.textView_endCity);
        textView_start_date_period = (TextView)rootView.findViewById(R.id.textView_start_date_period);
        textView_end_date_period = (TextView)rootView.findViewById(R.id.textView_end_date_period);
/********************************End instantiate imageButton***************************************/

/********************************Reference ListView**********************************************//**/
        mListView = (ListView)rootView.findViewById(R.id.listview_shedule);
/********************************End reference ListView********************************************/

/********************************Reference LinearLayout********************************************/
        synchronizinglinearlayout = (LinearLayout)rootView.findViewById(R.id.synchronizinglinearlayout);
/********************************End reference LinearLayout****************************************/

/********************************Attribute listener to imageButton*********************************/
        imageButton_startCity.setOnClickListener(listener_startCity);
        imageButton_dateTime.setOnClickListener(listener_dateTime);
        imageButton_endCity.setOnClickListener(listener_endCity);
/********************************End attribute listener to imageButton*****************************/

/********************************Attribute listener to ListView************************************/
        mListView.setOnItemClickListener(listener_items_shedules);
/********************************End attribute listener to ListView********************************/

/********************************Attribute listener to TextView************************************/
        textView_startCity.addTextChangedListener(text_change_textView_startCity);
        textView_dateTime.addTextChangedListener(text_change_textView_dateTime);
        textView_endCity.addTextChangedListener(text_change_textView_endCity);
/********************************End attribute listener to TextView********************************/

        //mSheduleAdapter = new ArrayAdapter<String>(
        //        getActivity(),
        //        R.layout.list_item_shedule,
        //            R.id.list_item_Num_Airline_textView,
        //        listShedule
        //);
        //mListView.setAdapter(mSheduleAdapter);

/********************************Attach to Listview the adapter************************************/
        // The ArrayAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mScheduleAdapter = new ScheduleAdapter(getActivity(), null, 0);
        mListView.setAdapter(mScheduleAdapter);
/********************************End attach to Listview the adapter********************************/


        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.

        String sortOrder;
        Uri sheduleForCityUri;

        switch (id) {
            case SHEDULE_LOADER: //Load all shedules by cities select
                // Sort order:  Ascending, by date.
                sortOrder = SheduleEntry.COLUMN_DATETEXT + " ASC ";

                sheduleForCityUri = uriForLoader();            Log.d("URI URI", String.valueOf(sheduleForCityUri));
                // Now create and return a CursorLoader that will take care of
                // creating a Cursor for the data being displayed.
                return new CursorLoader(
                        getActivity(),
                        sheduleForCityUri,
                        SHEDULE_COLUMNS,
                        null,
                        null,
                        sortOrder
                );

            case SHEDULE_LOADER_MAX_PERIOD: //Load max date period
                sortOrder = SheduleEntry.COLUMN_DATETEXT + " DESC LIMIT 1 ";

                // creating a Cursor for the data being displayed.
                return new CursorLoader(
                        getActivity(),
                        SheduleEntry.getPeriodUri(),
                        SHEDULE_DATE,
                        null,
                        null,
                        sortOrder
                );

            case SHEDULE_LOADER_MIN_PERIOD: //Load min date period
                sortOrder = SheduleEntry.COLUMN_DATETEXT + " ASC LIMIT 1 ";

                // creating a Cursor for the data being displayed.
                return new CursorLoader(
                        getActivity(),
                        SheduleEntry.getPeriodUri(),
                        SHEDULE_DATE,
                        null,
                        null,
                        sortOrder
                );

            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SHEDULE_LOADER:
                mScheduleAdapter.swapCursor(data);
                Log.d("LOAD DATA FROM DB DONE", String.valueOf(data.getCount()));
                if (mPosition != ListView.INVALID_POSITION) {
                    // If we don't need to restart the loader, and there's a desired position to restore
                    // to, do so now.
                    mListView.smoothScrollToPosition(mPosition);
                }
                ScheduleFragment.synchronizinglinearlayout.setVisibility(View.INVISIBLE);
                break;

            case SHEDULE_LOADER_MAX_PERIOD:
                if (data != null && data.moveToFirst()) {
                    try {
                        textView_end_date_period.setText(Utility.defaultLocalFormat(data.getString(data.getColumnIndex(SheduleEntry.COLUMN_DATETEXT))));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case SHEDULE_LOADER_MIN_PERIOD:
                if (data != null && data.moveToFirst()) {
                    try {
                        textView_start_date_period.setText(Utility.defaultLocalFormat(data.getString(data.getColumnIndex(SheduleEntry.COLUMN_DATETEXT))));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mScheduleAdapter.swapCursor(null);
    }

/************************************End overrides methods*****************************************/

/*************************Listeners for widgets****************************************************/
    //listener event for "mListView"
    public AdapterView.OnItemClickListener listener_items_shedules = new AdapterView.OnItemClickListener()
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //((Callback)getActivity()).onItemSelected("date");
           // mPosition = position;
            Log.d("evenement clicklistitem", "click!");

            Cursor cursor = mScheduleAdapter.getCursor();
            if (cursor != null && cursor.moveToPosition(position)) {
                ((Callback)getActivity())
                        .onItemSelected(cursor.getString(COL_SHEDULE_ID));
            }
            //mPosition = position;
            //String forecastStr = mSheduleAdapter.getItem(position);
            //Intent intent = new Intent(getActivity(), DetailActivity.class);
            //intent.putExtra(Intent.EXTRA_TEXT, forecastStr);
            //startActivity(intent);
        }
    };

    //listener event for "imageButton_startCity"
    public View.OnClickListener listener_startCity = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            TEST_CITY = "START"; //start city
            showChooseCityDialog();
        }
    };

    //listener event for "imageButton_dateTime"
    public View.OnClickListener listener_dateTime = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            showChooseDateDialog(v);
        }
    };

    //listener event for "imageButton_endCity"
    public View.OnClickListener listener_endCity = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            TEST_CITY = "ARRIVAL"; //Arrival city
            showChooseCityDialog();
        }
    };

    //listener event for "textView_startCity"
    public TextWatcher text_change_textView_startCity = new TextWatcher()
    {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {onResume();}
    };

    //listener event for "textView_dateTime"
    public TextWatcher text_change_textView_dateTime = new TextWatcher()
    {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {onResume();}
    };

    //listener event for "textView_endCity"
    public TextWatcher text_change_textView_endCity = new TextWatcher()
    {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {onResume();}
    };
/*************************End listeners for widgets************************************************/

/******************************Function for refresh************************************************/
    private void updateShedule() {
        ScheduleFragment.synchronizinglinearlayout.setVisibility(View.VISIBLE);
        new FetchSheduleTask(getActivity()).execute("shedule");

        //Clear date textView
        textView_dateTime.setText("");

        //Reload period
        getLoaderManager().restartLoader(SHEDULE_LOADER_MAX_PERIOD, null, this);
        getLoaderManager().restartLoader(SHEDULE_LOADER_MIN_PERIOD, null, this);

    }
/******************************End function for refresh********************************************/

/*************************Functions for start dialogs**********************************************/
    //Show CityDialog
    public void showChooseCityDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ChooseCityDialog();
        dialog.show(getFragmentManager(), "Choose City");
    }

    //Show DialogDate
    public void showChooseDateDialog(View v) {
        DialogFragment newFragment = new ChooseDateDialog();
        newFragment.show(getFragmentManager(), "datePicker");
    }
/*************************End functions for start dialogs******************************************/

/*************************Functions for change filter city and date********************************/
    //Change start_city or end_city in TextView
    public static void change_city_value(String city){
        switch (TEST_CITY){
            case "START":
                textView_startCity.setText(city);
                break;

            case "ARRIVAL":
                textView_endCity.setText(city);
                break;
        }
    }

    //Change Date or reset it
    public static void change_date_value(Boolean Choice, int year, String monthOfYear, String dayOfMonth){
        if (Choice == true) {//Add date

            //if default language contains en ( is english or other but not french), display date in english format
            if(String.valueOf(Locale.getDefault()).contains("en")){
                textView_dateTime.setText(monthOfYear + "/" + dayOfMonth + "/" + year);}
            else { //display date in french format
                textView_dateTime.setText(dayOfMonth + "/" + monthOfYear + "/" + year);}
        }
        else {//Reset date
            textView_dateTime.setText("");
        }
    }

/*************************End functions for change filter city and date****************************/

/*************************Functions for load default cities and display it*************************/
    public String displayDefaultCity(int key, int value){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String City = sharedPrefs.getString(
                getString(key),
                getString(value));
        return City;
    }
/*************************End functions for load default cities and display it*********************/

/*********************************Functions for uri Loader*****************************************/
    private Uri uriForLoader()
    {
        if(String.valueOf(textView_dateTime.getText()).equals("")){
            return SheduleEntry.buildSheduleCities(
                    String.valueOf(textView_startCity.getText()), String.valueOf(textView_endCity.getText()));
        }

        else {
            return SheduleEntry.buildSheduleCitiesWithDate(
                    String.valueOf(textView_startCity.getText()), String.valueOf(textView_endCity.getText()), Utility.convertToFrenshFormat(String.valueOf(textView_dateTime.getText())));
                    //Convert date in french format before query because he was save in database in french format
        }
    }
/*********************************End functions for uri Loader*************************************/
}
