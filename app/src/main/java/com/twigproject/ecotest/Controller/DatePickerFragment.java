package com.twigproject.ecotest.Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;


import com.twigproject.ecotest.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * DialogFragment subclass for handling with DatePicker Dialog
 * @author Max Ermakov
 */
public class DatePickerFragment extends DialogFragment {
    /**
     * String key for sending result to target fragment
     */
    public static final String EXTRA_DATE ="com.twigproject.android.ecotest.date";
    /**
     * Ref on Date object for modifying
     */
    private Date mDate;

    /**
     * Sends user choosed Date value to target fragment
     * @param resultCode integer confirm code(OK or CANCEL)
     */
    private void sendResult(int resultCode){
        if(getTargetFragment()==null){
            return;
        }
        Intent i = new Intent();
        i.putExtra(EXTRA_DATE,mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,i);
    }

    /**
     * Creates new DatePicker dialog instance with given Date value
     * @param date Date value for pre-created picker ui initialisation
     * @return DatePicker instance
     */
    public static DatePickerFragment newInstance(Date date){
        Bundle args=new Bundle();
        args.putSerializable(EXTRA_DATE,date);
        DatePickerFragment fragment=new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mDate=(Date)getArguments().getSerializable(EXTRA_DATE);                         //getting date obj. from bundle args
        Calendar calendar=Calendar.getInstance();                                       //init Calendar arguments with date value
        calendar.setTime(mDate);
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        View view=getActivity().getLayoutInflater().inflate(R.layout.dialog_date,null);
        DatePicker picker=(DatePicker)view.findViewById(R.id.dialog_date_datePicker);
                                                                                        //init picker with Calendar args
        picker.init(year,month,day,new DatePicker.OnDateChangedListener() {             //set date change listener
            @Override
            public void onDateChanged(DatePicker picker, int year, int month, int day) {
                mDate=new GregorianCalendar(year,month,day).getTime();

                getArguments().putSerializable(EXTRA_DATE,mDate);
            }
        });
                                                                                        //build picker dialog
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }
}
