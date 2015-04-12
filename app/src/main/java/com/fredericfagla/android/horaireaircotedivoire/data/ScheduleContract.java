package com.fredericfagla.android.horaireaircotedivoire.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the shedule database.
 */
public class ScheduleContract {

/************************************Declare variables*********************************************/
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.fredericfagla.android.horaireaircotedivoire";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.fredericfagla.android.horaireaircotedivoire/shedule/ is a valid path for
    // looking at shedule data. content://com.fredericfagla.android.horaireaircotedivoire/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_SHEDULE = "shedule";
/************************************End declare variables*****************************************/

/************************************Create tables contents****************************************/
    /* Inner class that defines the table contents of the shedule table */
    public static final class SheduleEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHEDULE).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_SHEDULE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_SHEDULE;

        public static final String TABLE_NAME = "shedule";

        // Column with the start and arrival foreign key into the city table.
        public static final String COLUMN_START_CITY = "start_city";
        public static final String COLUMN_ARRIVAL_CITY = "arrival_city";

        // Flight date stores as Text with format yyyy-MM-dd
        public static final String COLUMN_DATETEXT = "date";

        // Column with the start time city.
        public static final String COLUMN_START_TIME = "start_time";

        // Column with the arrival time city.
        public static final String COLUMN_ARRIVAL_TIME = "arrival_time";

        // Column with the flight number.
        public static final String COLUMN_FLIGHT_NO = "flight_no";

        public static Uri buildSheduleUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildAllSheduleWithDate(String date) {
            return CONTENT_URI.buildUpon().appendPath(date).build();

        }

        public static Uri buildSheduleCities(String startCity, String arrivalCity) {
            return CONTENT_URI.buildUpon().appendPath(startCity)
                    .appendPath(arrivalCity).build();
        }

        public static Uri buildSheduleCitiesWithDate(String startCity, String arrivalCity, String date) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(startCity))
                    .appendPath(String.valueOf(arrivalCity))
                    .appendPath(date).build();
        }

        public static Uri getPeriodUri() {
            return CONTENT_URI;
        }

        public static String getStartCityFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getArrivalCityFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(3);
        }
    }
/************************************End create tables contents************************************/
}
