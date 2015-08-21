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
import android.widget.TimePicker;


import com.twigproject.ecotest.R;

import java.util.Calendar;
import java.util.Date;

/**
 * DialogFragment subclass for handling with TimePicker Dialog
 */
public class TimePickerFragment extends DialogFragment {
    /**
     * String key for sending result to target fragment
     */
    public static final String EXTRA_TIME =
            "com.twigproject.android.ecotest.date";
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
        i.putExtra(EXTRA_TIME,mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,i);
    }
    /**
     * Creates new TimePicker dialog instance with given Date value
     * @param date Date value for pre-created picker ui initialisation
     * @return TimePicker instance
     */
    public static TimePickerFragment newInstance(Date date){
        Bundle args=new Bundle();
        args.putSerializable(EXTRA_TIME,date);
        TimePickerFragment fragment=new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @SuppressWarnings("deprecated")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mDate=(Date)getArguments().getSerializable(EXTRA_TIME);              //getting date obj. from bundle args

        final Calendar calendar=Calendar.getInstance();                            //init Calendar arguments with date value
        calendar.setTime(mDate);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        //int second=calendar.get(Calendar.SECOND);

        View view=getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_time,null);
        TimePicker picker=(TimePicker)view
                .findViewById(R.id.dialog_time_timePicker);
        picker.setIs24HourView(true);                                       //init picker with Calendar args
        picker.setCurrentHour(hour);
        picker.setCurrentMinute(minute);
        picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker picker, int hour, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY,hour);
                calendar.set(Calendar.MINUTE,minute);
                //mDate.setHours(hour);
                //mDate.setMinutes(minute);
                mDate=calendar.getTime();
                getArguments().putSerializable(EXTRA_TIME,mDate);
            }
        });

        return new AlertDialog.Builder(getActivity())                       //build picker dialog
                .setView(view)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }
}
