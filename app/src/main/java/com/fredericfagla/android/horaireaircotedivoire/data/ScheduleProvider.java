package com.fredericfagla.android.horaireaircotedivoire.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ScheduleProvider extends ContentProvider {

    /************************************Declare variables*********************************************/
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ScheduleDbHelper mOpenHelper;

    private static final int SHEDULEALLCITY = 200;
    private static final int SHEDULE_ID = 202;
    private static final int SHEDULECITY = 300;
    private static final int SHEDULECITYWITHDATE = 301;
/************************************End declare variables*****************************************/

/************************************Declare variables selection***********************************/
    //Selection request for all shedule data
    private static final String sSheduleSelection =
            ScheduleContract.SheduleEntry.TABLE_NAME+
                    "." + ScheduleContract.SheduleEntry.COLUMN_START_CITY + " = ? AND " +
                    ScheduleContract.SheduleEntry.COLUMN_ARRIVAL_CITY + " = ? ";

    //Selection request for all shedule data with date
    private static final String sSheduleWithDateSelection =
            ScheduleContract.SheduleEntry.TABLE_NAME+
                    "." + ScheduleContract.SheduleEntry.COLUMN_START_CITY + " = ? AND " +
                    ScheduleContract.SheduleEntry.COLUMN_ARRIVAL_CITY + " = ? AND " +
                    ScheduleContract.SheduleEntry.COLUMN_DATETEXT + " = ? ";
/************************************End declare variables selection*******************************/

/************************************Cursor to select *********************************************/
    //Cursor for shedule of all cities without date
    private Cursor getSheduleAllCity(String[] projection, String sortOrder) {


        return mOpenHelper.getReadableDatabase().query(
                ScheduleContract.SheduleEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    //Cursor for shedule by cities
    private Cursor getShedule(Uri uri, String[] projection, String sortOrder) {
        String selection;
        selection = sSheduleSelection;
        String start_city = ScheduleContract.SheduleEntry.getStartCityFromUri(uri);
        String arrival_city = ScheduleContract.SheduleEntry.getArrivalCityFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                ScheduleContract.SheduleEntry.TABLE_NAME,
                projection,
                selection,
                new String[]{start_city, arrival_city},
                null,
                null,
                sortOrder
        );
    }

    //Cursor for shedule by cities  with date
    private Cursor getSheduleWithDate(Uri uri, String[] projection, String sortOrder) {
        String selection;
        selection = sSheduleWithDateSelection;
        String start_city = ScheduleContract.SheduleEntry.getStartCityFromUri(uri);
        String arrival_city = ScheduleContract.SheduleEntry.getArrivalCityFromUri(uri);
        String date = ScheduleContract.SheduleEntry.getDateFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                ScheduleContract.SheduleEntry.TABLE_NAME,
                projection,
                selection,
                new String[]{start_city, arrival_city, date},
                null,
                null,
                sortOrder
        );
    }
/************************************End cursor to select *****************************************/

/**************************************************************************************************/
/*
All paths added to the UriMatcher have a corresponding code to return when a match is
found.  The code passed into the constructor represents the code to return for the root
URI.  It's common to use NO_MATCH as the code for this case.
*/
    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ScheduleContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, ScheduleContract.PATH_SHEDULE + "/#/", SHEDULE_ID);
        matcher.addURI(authority, ScheduleContract.PATH_SHEDULE, SHEDULEALLCITY);
        matcher.addURI(authority, ScheduleContract.PATH_SHEDULE + "/*/*", SHEDULECITY);
        matcher.addURI(authority, ScheduleContract.PATH_SHEDULE + "/*/*/*", SHEDULECITYWITHDATE);

        return matcher;
    }
/**************************************************************************************************/

/************************************Overrides methods*********************************************/
    @Override
    public boolean onCreate() {
        mOpenHelper = new ScheduleDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "shedule"
            case SHEDULEALLCITY: {
                retCursor = getSheduleAllCity(projection, sortOrder);
                break;
            }

            // "shedule/#"
            case SHEDULE_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScheduleContract.SheduleEntry.TABLE_NAME,
                        projection,
                        ScheduleContract.SheduleEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            // "shedule/*/*"
            case SHEDULECITY: {
                retCursor = getShedule(uri, projection, sortOrder);
                break;
            }
            // "shedule/*/*/*"
            case SHEDULECITYWITHDATE: {
                retCursor =  getSheduleWithDate(uri, projection, sortOrder);
                break;
            }

            default:
                /*In case you do not support certain methods its good practice to throw an UnsupportedOperationException()*/
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
           case SHEDULE_ID:
                return ScheduleContract.SheduleEntry.CONTENT_ITEM_TYPE;

            case SHEDULEALLCITY:
                return ScheduleContract.SheduleEntry.CONTENT_TYPE;

            case SHEDULECITY:
                return ScheduleContract.SheduleEntry.CONTENT_TYPE;

            case SHEDULECITYWITHDATE:
                return ScheduleContract.SheduleEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case SHEDULEALLCITY: {
                long _id = db.insert(ScheduleContract.SheduleEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ScheduleContract.SheduleEntry.buildSheduleUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case SHEDULEALLCITY:
                rowsDeleted = db.delete(
                        ScheduleContract.SheduleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case SHEDULEALLCITY:
                rowsUpdated = db.update(ScheduleContract.SheduleEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SHEDULEALLCITY:
                db.beginTransaction();
                int returnCountShedules = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ScheduleContract.SheduleEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCountShedules++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCountShedules;
            default:
                return super.bulkInsert(uri, values);
        }
    }
/************************************End overrides methods*****************************************/
}
