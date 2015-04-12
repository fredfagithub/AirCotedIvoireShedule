package com.fredericfagla.android.horaireaircotedivoire;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link ScheduleAdapter} exposes a list of shedule
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ScheduleAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_DAY = 1;

    /**
     * Cache of the children views for a shedule list item.
     */
    public static class ViewHolder {
        //public final TextView shedule_id;
        public final TextView list_item_day_textview;
        public final TextView list_item_day_number_textview;
        public final TextView list_item_month_textview;
        public final TextView list_item_year_textview;
        public final TextView list_item_start_time_textView;
        public final TextView list_item_Num_Airline_textView;
        public final TextView list_item_arrival_time_textView;

        public ViewHolder(View view) {
            list_item_day_textview = (TextView) view.findViewById(R.id.list_item_day_textview);
            list_item_day_number_textview = (TextView) view.findViewById(R.id.list_item_day_number_textview);
            list_item_month_textview = (TextView) view.findViewById(R.id.list_item_month_textview);
            list_item_year_textview = (TextView) view.findViewById(R.id.list_item_year_textview);
            list_item_start_time_textView = (TextView) view.findViewById(R.id.list_item_start_time_textView);
            list_item_Num_Airline_textView = (TextView) view.findViewById(R.id.list_item_Num_Airline_textView);
            list_item_arrival_time_textView = (TextView) view.findViewById(R.id.list_item_arrival_time_textView);
        }
    }

    public ScheduleAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // The layout
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_DAY: {
                layoutId = R.layout.list_item_shedule;
                break;
            }
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read start time from cursor
        String startTime = cursor.getString(ScheduleFragment.COL_SHEDULE_START_TIME);
        // Find TextView and set formatted date on it
        viewHolder.list_item_start_time_textView.setText(startTime);

        // Read arrival time from cursor
        String arrivalTime = cursor.getString(ScheduleFragment.COL_SHEDULE_ARRIVAL_TIME);
        // Find TextView and set weather forecast on it
        viewHolder.list_item_arrival_time_textView.setText(arrivalTime);

        // Read flight no from cursor
        String flightNo = cursor.getString(ScheduleFragment.COL_SHEDULE_FLIGHT_NO);
        // Find TextView and set weather forecast on it
        viewHolder.list_item_Num_Airline_textView.setText(flightNo);

        // Read date from cursor
        String dateShedule = cursor.getString(ScheduleFragment.COL_SHEDULE_DATE);
        viewHolder.list_item_day_textview.setText((CharSequence) Utility.splitDate(dateShedule).get(0));
        viewHolder.list_item_day_number_textview.setText((CharSequence) Utility.splitDate(dateShedule).get(1));
        viewHolder.list_item_month_textview.setText((CharSequence) Utility.splitDate(dateShedule).get(2));
        viewHolder.list_item_year_textview.setText((CharSequence) Utility.splitDate(dateShedule).get(3));
    }

    @Override
    public int getItemViewType(int position) {
        return (VIEW_TYPE_DAY);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}