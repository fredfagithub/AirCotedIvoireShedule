package com.fredericfagla.android.horaireaircotedivoire;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


/**
 * Created by frederic on 07/03/2015.
 */
public class ChooseCityDialog extends DialogFragment {

  /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface CityDialogListener {
        public void onDialogCityPositiveClick(DialogFragment dialog, String city);
        public void onDialogCityNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    CityDialogListener mListener;

/************************************Overrides methods*********************************************/
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (CityDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement CityDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Load Cities with string-array of string.xml
        final String[] arrayCity = getResources().getStringArray(R.array.pref_city_options);

        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        // LayoutInflater inflater = getActivity().getLayoutInflater();
        // builder.setView(inflater.inflate(R.layout.activity_choose_city_dialog, null));
        // Add radio_buttons city
         builder.setSingleChoiceItems(arrayCity, -1,
                         new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 // Send the result to the host activity
                                 mListener.onDialogCityPositiveClick(ChooseCityDialog.this, arrayCity[which]);
                                 dialog.cancel();

                             }
//                             @Override
//                             public void onClick(DialogInterface dialog, int which,
//                                                 boolean isChecked) {
//                                 if (isChecked) {
//                                     // If the user checked the item, add it to the selected items
////                                     mSelectedItems.add(which);
//                                 } else if (which > 0) {//if (mSelectedItems.contains(which))
//                                     // Else, if the item is already in the array, remove it
////                                     mSelectedItems.remove(Integer.valueOf(which));
//                                 }
//                             }
                         })
                 //Set the action buttons
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onDialogCityNegativeClick(ChooseCityDialog.this);
                    }
                });
        return builder.create();
    }
/************************************End overrides methods*****************************************/
}