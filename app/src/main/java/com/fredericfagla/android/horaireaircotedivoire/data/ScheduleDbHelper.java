package com.fredericfagla.android.horaireaircotedivoire.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fredericfagla.android.horaireaircotedivoire.data.ScheduleContract.SheduleEntry;

/**
 * Manages a local database for shedule data.
 */

/*To create and upgrade a database in your Android application you create a subclass of the SQLiteOpenHelper class*/
public class ScheduleDbHelper extends SQLiteOpenHelper {

/************************************Declare variables*********************************************/
/*specifying the database name and the current database version.*/

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "shedule.db";
/************************************End declare variables*****************************************/

    public ScheduleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

/************************************Overrides methods*********************************************/
/*overrides the following method to create database.*/
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
/*SQLiteDatabase is the base class for working with a SQLite database in Android and provides methods to open, query, update and close the database.*/
        // Create a table to hold cities.  A city consists of the string supplied in the
        // city name, and the latitude and longitude

/*The database tables should use the identifier _id for the primary key of the table*/
        final String SQL_CREATE_SHEDULE_TABLE = "CREATE TABLE " + SheduleEntry.TABLE_NAME + " (" +
                // Create a table to hold shedules
                SheduleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the start and arrival city entry associated with this shedule data
                SheduleEntry.COLUMN_START_CITY + " TEXT NOT NULL, " +
                SheduleEntry.COLUMN_ARRIVAL_CITY + " TEXT NOT NULL, " +

                SheduleEntry.COLUMN_DATETEXT + " TEXT NOT NULL, " +
                SheduleEntry.COLUMN_START_TIME + " TEXT NOT NULL, " +
                SheduleEntry.COLUMN_ARRIVAL_TIME + " TEXT NOT NULL," +
                SheduleEntry.COLUMN_FLIGHT_NO + " TEXT NOT NULL " +
                ")";

/*In addition it provides the execSQL() method, which allows to execute an SQL statement directly.*/
        sqLiteDatabase.execSQL(SQL_CREATE_SHEDULE_TABLE);
    }

    /*overrides the following method to update database.*/
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SheduleEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
/************************************End overrides methods*****************************************/
}
