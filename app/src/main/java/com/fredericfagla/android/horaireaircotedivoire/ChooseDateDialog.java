package com.fredericfagla.android.horaireaircotedivoire;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by frederic on 08/03/2015.
 */
public class ChooseDateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DateDialogListener {
        public void onDialogDateValidateClick(DatePicker view, Boolean Choice, int year, String monthOfYear, String dayOfMonth);
    }

    //Boolean takes false if user press on reset button
    Boolean Choice = true;

    // Use this instance of the interface to deliver action events
    DateDialogListener mListener;

/************************************Overrides methods*********************************************/
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DateDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DateDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog DateDialog = new DatePickerDialog(getActivity(), this, year, month, day);

        //Set the action buttons
        DateDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Reset",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Choice = false;
            }

        });
        // Create a new instance of DatePickerDialog and return it
        return DateDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("00");
        // Send the result event back to the host activity
        mListener.onDialogDateValidateClick(view, Choice, year, decimalFormat.format(monthOfYear+1), decimalFormat.format(dayOfMonth));
    }
/************************************End overrides methods*****************************************/
}
