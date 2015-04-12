package com.fredericfagla.android.horaireaircotedivoire;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.fredericfagla.android.horaireaircotedivoire.data.ScheduleContract.SheduleEntry;
import com.fredericfagla.android.horaireaircotedivoire.data.ScheduleDbHelper;

import java.util.Map;
import java.util.Set;

public class TestDb extends AndroidTestCase {

/************************************Declare variables*********************************************/
    public static final String LOG_TAG = TestDb.class.getSimpleName();
    static final String TEST_DATE = "12/03/2015";
    static final String TEST_START_DATE = "12/03/2015";
    static final String START_CITY = "Abidjan";
    static final String ARRIVAL_CITY = "Conakry";
/************************************End declare variables*****************************************/

/************************************Methods*******************************************************/
    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(ScheduleDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new ScheduleDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

/****************************************Test insert in tables*************************************/
    public void testInsertReadDb() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        ScheduleDbHelper dbHelper = new ScheduleDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        /************************************Test shedule*******************************************/
        // Now test shedules data
        ContentValues testSheduleValues = createSheduleValues(START_CITY, ARRIVAL_CITY);

        long sheduleRowId = db.insert(SheduleEntry.TABLE_NAME, null, testSheduleValues);
        assertTrue(sheduleRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor sheduleCursor = db.query(
                SheduleEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        validateCursor(sheduleCursor, testSheduleValues);
/********************************************End test shedule**************************************/

        dbHelper.close();
    }
/****************************************End test insert in tables*********************************/

/****************************************Create values to insert***********************************/
    static ContentValues createSheduleValues(String START_CITY, String ARRIVAL_CITY) {
        ContentValues sheduleValues = new ContentValues();
        sheduleValues.put(SheduleEntry.COLUMN_START_CITY, START_CITY);
        sheduleValues.put(SheduleEntry.COLUMN_ARRIVAL_CITY, ARRIVAL_CITY);
        sheduleValues.put(SheduleEntry.COLUMN_DATETEXT, TEST_DATE);
        sheduleValues.put(SheduleEntry.COLUMN_START_TIME, "15.40");
        sheduleValues.put(SheduleEntry.COLUMN_ARRIVAL_TIME, "16.40");
        sheduleValues.put(SheduleEntry.COLUMN_FLIGHT_NO, "HF500");

        return sheduleValues;
    }
/****************************************End create values to insert*******************************/

/****************************************Validate cursor*******************************************/
    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }

/****************************************End validate cursor***************************************/

/************************************End methods***************************************************/
}
