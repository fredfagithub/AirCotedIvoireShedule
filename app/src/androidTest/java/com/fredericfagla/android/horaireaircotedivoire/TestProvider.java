package com.fredericfagla.android.horaireaircotedivoire;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.fredericfagla.android.horaireaircotedivoire.data.ScheduleContract.SheduleEntry;

public class TestProvider extends AndroidTestCase {

/************************************Declare variables*********************************************/
    public static final String LOG_TAG = TestProvider.class.getSimpleName();
/************************************End declare variables*****************************************/



    // brings our database to an empty state
    public void deleteAllRecords() {
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
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    public void setUp() {
        deleteAllRecords();
    }

    public void testInsertReadProvider() {

        ContentValues testSheduleValues = TestDb.createSheduleValues(TestDb.START_CITY, TestDb.ARRIVAL_CITY);

        Uri sheduleInsertUri = mContext.getContentResolver().insert(SheduleEntry.CONTENT_URI, testSheduleValues);
        long sheduleRowId = ContentUris.parseId(sheduleInsertUri);

        // Verify we got a row back.
        assertTrue(sheduleInsertUri != null);
        Log.d(LOG_TAG, "New shedule: " + sheduleInsertUri);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor sheduleCursor = mContext.getContentResolver().query(
                SheduleEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );
        TestDb.validateCursor(sheduleCursor, testSheduleValues);

        // Now see if we can successfully query if we include the row id
        sheduleCursor = mContext.getContentResolver().query(
                SheduleEntry.buildSheduleUri(sheduleRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(sheduleCursor, testSheduleValues);
    }

    public void testGetType() {
        // content://com.fredericfagla.android.horaireaircotedivoire/shedule/
        String type = mContext.getContentResolver().getType(SheduleEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.fredericfagla.android.horaireaircotedivoire/shedule
        assertEquals(SheduleEntry.CONTENT_TYPE, type);

        String testDate = "12/03/2015";
        String testStartCity = "Abidjan";
        String testArrivalCity = "Conakry";
        // content://com.fredericfagla.android.horaireaircotedivoire/shedule/Abidjan/Conakry
        type = mContext.getContentResolver().getType(
                SheduleEntry.buildSheduleCities(testStartCity, testArrivalCity));
        // vnd.android.cursor.dir/com.fredericfagla.android.horaireaircotedivoire/shedule
        assertEquals(SheduleEntry.CONTENT_TYPE, type);

        // content://com.fredericfagla.android.horaireaircotedivoire/shedule/Abidjan/Conakry/12%2F03%2F2015
        type = mContext.getContentResolver().getType(
                SheduleEntry.buildSheduleCitiesWithDate(testStartCity, testArrivalCity, testDate));
        // vnd.android.cursor.dir/com.fredericfagla.android.horaireaircotedivoire/shedule
        assertEquals(SheduleEntry.CONTENT_TYPE, type);

    }

    public void testUpdateShedule() {
        ContentValues values = TestDb.createSheduleValues("Dakar", "Lagos");

        Uri sheduleUri = mContext.getContentResolver().
                insert(SheduleEntry.CONTENT_URI, values);
        long sheduleRowId = ContentUris.parseId(sheduleUri);

        // Verify we got a row back.
        assertTrue(sheduleRowId != -1);
        Log.d(LOG_TAG, "New row id: " + sheduleRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(SheduleEntry._ID, sheduleRowId);
        updatedValues.put(SheduleEntry.COLUMN_DATETEXT, "18/03/2017");

        int count = mContext.getContentResolver().update(
                SheduleEntry.CONTENT_URI, updatedValues, SheduleEntry._ID + "= ?",
                new String[] { Long.toString(sheduleRowId)});

        assertEquals(count, 1);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                SheduleEntry.buildSheduleUri(sheduleRowId),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );

        TestDb.validateCursor(cursor, updatedValues);
    }

    // Make sure we can still delete after adding/updating stuff
    public void testDeleteRecordsAtEnd() {
        deleteAllRecords();
    }


    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void addAllContentValues(ContentValues destination, ContentValues source) {
        for (String key : source.keySet()) {
            destination.put(key, source.getAsString(key));
        }
    }

    static final String CotonouAccra_DATE = "12/03/2015";
    String cityCotonou = "Cotonou";
    String cityAccra = "Accra";

    static ContentValues createSheduleCotonouAccraValues(String STARTCITY, String ARRIVALCITY) {
        ContentValues sheduleValues = new ContentValues();
        sheduleValues.put(SheduleEntry.COLUMN_START_CITY, STARTCITY);
        sheduleValues.put(SheduleEntry.COLUMN_ARRIVAL_CITY, ARRIVALCITY);
        sheduleValues.put(SheduleEntry.COLUMN_DATETEXT, CotonouAccra_DATE);
        sheduleValues.put(SheduleEntry.COLUMN_START_TIME, "10.00");
        sheduleValues.put(SheduleEntry.COLUMN_ARRIVAL_TIME, "11.00");
        sheduleValues.put(SheduleEntry.COLUMN_FLIGHT_NO, "HF500");

        return sheduleValues;
    }

    // Inserts shedule data for the shedule CotonouAccra data set.
    public void insertCotonouAccraData() {

        ContentValues CotonouAccraSheduleValues = createSheduleCotonouAccraValues(cityCotonou, cityAccra);
        Uri sheduleInsertUri = mContext.getContentResolver()
                .insert(SheduleEntry.CONTENT_URI, CotonouAccraSheduleValues);
        assertTrue(sheduleInsertUri != null);
    }

    public void testUpdateAndReadShedule() {
        insertCotonouAccraData();
        String FLIGHT_NO = "HF450";

        // Make an update to one value.
        ContentValues CotonouAccraUpdate = new ContentValues();
        CotonouAccraUpdate.put(SheduleEntry.COLUMN_FLIGHT_NO, FLIGHT_NO);

        mContext.getContentResolver().update(
                SheduleEntry.CONTENT_URI, CotonouAccraUpdate, null, null);

        // A cursor is your primary interface to the query results.
        Cursor sheduleCursor = mContext.getContentResolver().query(
                SheduleEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make the same update to the full ContentValues for comparison.
        ContentValues sheduleAltered = createSheduleCotonouAccraValues(cityCotonou, cityAccra);
        sheduleAltered.put(SheduleEntry.COLUMN_FLIGHT_NO, FLIGHT_NO);

        TestDb.validateCursor(sheduleCursor, sheduleAltered);
    }

}
