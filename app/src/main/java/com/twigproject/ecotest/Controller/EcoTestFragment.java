package com.twigproject.ecotest.Controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.twigproject.ecotest.Model.Utils.AssetsPropertyReader;
import com.twigproject.ecotest.Model.EcoTest;
import com.twigproject.ecotest.Model.TestSession;
import com.twigproject.ecotest.R;


import org.joda.time.DateTime;

/**
 * Controller for single test modification UI
 * @author Max Ermakov
 */
public class EcoTestFragment extends Fragment {
    /**
     * TAG label for logcat
     */
    private static final  String TAG="com.twigproject.ecotest.Controller.EcoTestFragment";
    /**
     * String key for equipment type extracting from bundle args
     */
    protected static final String EXTRA_EQUIPMENT_TYPE="com.twigproject.ecotest.Controller.equipment_type";
    /**
     * String key for equipment number extracting from bundle args
     */
    protected static final String EXTRA_EQUIPMENT_NUMBER ="com.twigproject.ecotest.Controller.equipment_count";
    /**
     * picker dialog shower id
     */
    private static final String DIALOG_DATE="date";
    /**
     * Test start date request code id
     */
    private static final int REQUEST_START_DATE=0;
    /**
     * Test start time request code id
     */
    private static final int REQUEST_START_TIME=1;
    /**
     * Test end date request code id
     */
    private static final int REQUEST_END_DATE=2;
    /**
     * Test end time request code id
     */
    private static final int REQUEST_END_TIME=3;

    private String mEquipmentType;
    private int mNumber;
    private EcoTest mTest;
    private Callbacks mCallbacks;

    //-------Views-----
    private TextView mEquipmentTypeNumberView;
    private CheckBox mIsAtWorkCheckBox;
    private EditText mO2field;
    private EditText mTemperatureField;
    private EditText mCOfieldPPM;
    private EditText mCOfieldMG;
    private EditText mNOxfieldPPM;
    private EditText mNOxfieldMG;
    private Button mStartDateButton;
    private Button mStartTimeButton;
    private Button mEndDateButton;
    private Button mEndTimeButton;

    /**
     * @see com.twigproject.ecotest.Model.Utils.AssetsPropertyReader
     */
    private AssetsPropertyReader mPropertyReader;

    /**
     * @see java.util.Properties
     */
    private Properties mLimitProperties;

    /**
     * Creates new EcoTestFragment instance with given equipment type and number
     * @param equipmentType equipment type name
     * @param equipmentNumber equipment number
     * @return fragment instance
     */
    public static Fragment newInstance(String equipmentType, int equipmentNumber){
        Bundle args=new Bundle();
        args.putString(EXTRA_EQUIPMENT_TYPE,equipmentType);
        args.putInt(EXTRA_EQUIPMENT_NUMBER,equipmentNumber);
        EcoTestFragment fragment=new EcoTestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        mEquipmentType=(String)getArguments().getString(EXTRA_EQUIPMENT_TYPE);  //extract equipment type
        mNumber=(int)getArguments().getInt(EXTRA_EQUIPMENT_NUMBER);             //extract equipment number
        mTest= TestSession.get(getActivity(), mEquipmentType).getTest(mNumber); //getting test from session test list
        mPropertyReader=new AssetsPropertyReader(getActivity());                //init property reader
        mLimitProperties = mPropertyReader.getProperties("limits.properties");  //load ecology limits property from assets
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=(View)inflater.inflate(R.layout.fragment_ecotest,container,false);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityIntent(getActivity())!=null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }



        mEquipmentTypeNumberView=(TextView)view.findViewById(R.id.equipment_textView);
        mEquipmentTypeNumberView.setText(mTest.getEquipmentType()+" #"+mTest.getEquipmentNumber());

        mO2field =(EditText)view.findViewById(R.id.edit_O2);
        mO2field.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        if(mTest.getO2()!=null){                                    //if O2 value exists
            mO2field.setText(mTest.getO2().toString());             //set test O2 value to text field
        }
        mO2field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence sequence, int i, int i2, int i3) {
                if (sequence.length()>0) {
                    mTest.setO2(Double.parseDouble(sequence.toString()));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mTemperatureField=(EditText)view.findViewById(R.id.edit_temperature);
        mTemperatureField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        char degree = '\u00B0';                                             //Degree round symbol
        if(mTest.getTemperature()!=null){                                   //if temperature value exists
            mTemperatureField.setText(mTest.getTemperature().toString());   //set test temperature to text field
        }
        else {
            mTemperatureField.setHint("t, " + degree + "C");
        }
        mTemperatureField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i2, int i3) {
                if (sequence.length()>0) {
                    mTest.setTemperature(Double.parseDouble(sequence.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mCOfieldPPM =(EditText)view.findViewById(R.id.edit_CO);
        mCOfieldPPM.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        mCOfieldMG=(EditText)view.findViewById(R.id.edit_CO_mg);
        mCOfieldMG.setEnabled(true);
        if(mTest.getCO()!=null){                                            //if test CO value exists
            mCOfieldPPM.setText(mTest.getCO().toString());                  //set to CO text field CO value
            if (mTest.getO2()!=null) {                                      //O2 value exists
                double COmg=EcoTest.COppmToMg(mTest.getCO(),mTest.getO2()); //Calculate CO in mg/m3
                String str=String.format( "%.2f", COmg );
                String limit=mLimitProperties                               //get limits from properties
                        .getProperty(mTest.getEquipmentType()
                                +"_"+mTest.getEquipmentNumber()+"COlimit","230");
                mCOfieldMG.setText(str);                                    //set calculated value to text field
                if(COmg>Double.parseDouble(limit)){
                    mCOfieldMG.setTextColor(Color.parseColor("#ff0000"));   //notificate if calculated value bigger the limit
                    Toast.makeText(getActivity(),R.string.index_out_of_limit,Toast.LENGTH_LONG).show();
                }
            }
            mCOfieldMG.setEnabled(true);
        }
        else {
            mCOfieldMG.setEnabled(true);
        }

        mCOfieldPPM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i2, int i3) {
                //mTest.setCO(Double.parseDouble(sequence.toString()));
            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i2, int i3) {
                if (sequence.length()>0) {
                    mTest.setCO(Double.parseDouble(sequence.toString()));
                }
                if (mTest.getO2()!=null) {
                    double COmg=EcoTest.COppmToMg(mTest.getCO(),mTest.getO2());
                    String str=String.format( "%.2f", COmg );
                    String limit=mLimitProperties.getProperty(mTest.getEquipmentType()+"_"+mTest.getEquipmentNumber()+"COlimit","230");
                    mCOfieldMG.setText(str);
                    if(COmg>Double.parseDouble(limit)){
                        mCOfieldMG.setTextColor(Color.parseColor("#ff0000"));
                        Toast.makeText(getActivity(),R.string.index_out_of_limit,Toast.LENGTH_LONG).show();
                    }
                }
                mCOfieldMG.setEnabled(true);
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mNOxfieldPPM=(EditText)view.findViewById(R.id.edit_NOx);
        mNOxfieldPPM.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        mNOxfieldMG=(EditText)view.findViewById(R.id.edit_NOx_mg);
        mNOxfieldMG.setEnabled(true);
        if(mTest.getNOx()!=null){                                               //if NOx value exists
            mNOxfieldPPM.setText(mTest.getNOx().toString());                    //set NOx value to text field
            if(mTest.getO2()!=null){                                            //if O2 value exists
                double NOxMg=EcoTest.NOxppmToMg(mTest.getNOx(),mTest.getO2());  //Calculate NOx in mg/m3
                String str=String.format( "%.2f", NOxMg );
                String limit=mLimitProperties                                   //get limits from properties
                        .getProperty(mTest.getEquipmentType()
                                +"_"+mTest.getEquipmentNumber()+"NOxlimit");
                mNOxfieldMG.setText(str);                                       //set calculated value to text field
                if(NOxMg>Double.parseDouble(limit)){                            //if calculated value bigger then limit
                    mNOxfieldMG.setTextColor(Color.parseColor("#ff0000"));      //notificate
                    Toast.makeText(getActivity(),R.string.index_out_of_limit,Toast.LENGTH_LONG).show();
                }
                mNOxfieldMG.setEnabled(true);
            }
            else{
                mNOxfieldMG.setEnabled(true);
            }
        }
        mNOxfieldPPM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i2, int i3) {
                //mTest.setNOx(Double.parseDouble(sequence.toString()));
            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i2, int i3) {
                if (sequence.length()>0) {
                    mTest.setNOx(Double.parseDouble(sequence.toString()));
                }
                if (mTest.getO2()!=null) {
                    double NOxMg=EcoTest.NOxppmToMg(mTest.getNOx(),mTest.getO2());
                    String str=String.format( "%.2f", NOxMg );
                    String limit=mLimitProperties.getProperty(mTest.getEquipmentType()+"_"+mTest.getEquipmentNumber()+"NOxlimit");
                    mNOxfieldMG.setText(str);
                    if(NOxMg>Double.parseDouble(limit)){
                        mNOxfieldMG.setTextColor(Color.parseColor("#ff0000"));
                        Toast.makeText(getActivity(),R.string.index_out_of_limit,Toast.LENGTH_LONG).show();
                    }
                }
                mNOxfieldMG.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mStartDateButton=(Button)view.findViewById(R.id.ecoTest_start_date);
        updateStartDate();  //update button view
        mStartDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm=getActivity().getSupportFragmentManager();
                DatePickerFragment dialog=DatePickerFragment.newInstance(mTest.getStartTime().toDate());
                dialog.setTargetFragment(EcoTestFragment.this,REQUEST_START_DATE);
                dialog.show(fm,DIALOG_DATE);
            }
        });

        mStartTimeButton=(Button)view.findViewById(R.id.ecoTest_start_time);
        updateStartTime();   //update button view
        mStartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm=getActivity().getSupportFragmentManager();
                TimePickerFragment dialog=TimePickerFragment.newInstance(mTest.getStartTime().toDate());
                dialog.setTargetFragment(EcoTestFragment.this,REQUEST_START_TIME);
                dialog.show(fm,DIALOG_DATE);
            }
        });

        mEndDateButton=(Button)view.findViewById(R.id.ecoTest_end_date);
        updateEndDate();    //update button view
        mEndDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm=getActivity().getSupportFragmentManager();
                DatePickerFragment dialog=DatePickerFragment.newInstance(mTest.getEndTime().toDate());
                dialog.setTargetFragment(EcoTestFragment.this,REQUEST_END_DATE);
                dialog.show(fm,DIALOG_DATE);
            }
        });

        mEndTimeButton=(Button)view.findViewById(R.id.ecoTest_end_time);
        updateEndTime();    //update button view
        mEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm=getActivity().getSupportFragmentManager();
                TimePickerFragment dialog=TimePickerFragment.newInstance(mTest.getEndTime().toDate());
                dialog.setTargetFragment(EcoTestFragment.this,REQUEST_END_TIME);
                dialog.show(fm,DIALOG_DATE);
            }
        });

        mIsAtWorkCheckBox=(CheckBox)view.findViewById(R.id.is_equipment_at_work);
        mIsAtWorkCheckBox.setChecked(mTest.isAtWork());   // checked by default
        if(mTest.isAtWork()){
            lockView();
        }
        mIsAtWorkCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                mTest.setAtWork(isChecked);
                TestSession.get(getActivity()).saveTests();
                if(isChecked){
                    lockView();
                }
                else {
                    mO2field.setEnabled(false);
                    mTemperatureField.setEnabled(false);
                    mCOfieldPPM.setEnabled(false);
                    mCOfieldMG.setEnabled(false);
                    mNOxfieldPPM.setEnabled(false);
                    mNOxfieldMG.setEnabled(false);
                    mStartTimeButton.setEnabled(false);
                    mStartDateButton.setEnabled(false);
                    mEndTimeButton.setEnabled(false);
                    mEndDateButton.setEnabled(false);
                }
                mCallbacks.onTestUpdated(mTest);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!= Activity.RESULT_OK){
            return;
        }
        if(requestCode==REQUEST_START_DATE){    //get Date value from picker
            DateTime date=new DateTime((Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE));
            mTest.setStartTime(date);           //set to test entity
            updateStartDate();                  //update button view
        }
       if(requestCode==REQUEST_START_TIME){     //get Date value from picker
            DateTime date=new DateTime((Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME));
            mTest.setStartTime(date);           //set to test entity
            updateStartTime();                  //update button view
        }
        if (requestCode==REQUEST_END_DATE){     //get Date value from picker
            DateTime date=new DateTime((Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE));
            mTest.setEndTime(date);             //set to test entity
            updateEndDate();                    //update button view
        }
        if(requestCode==REQUEST_END_TIME){      //get Date value from picker
            DateTime date=new DateTime((Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE));
            mTest.setEndTime(date);             //set to test entity
            updateEndTime();                    //update button view
        }

    }
    /*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.eco_test,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.remove_current_session:
                //EcoTestsLab.get(getActivity()).removeCurrentSession();
                Intent i=new Intent(getActivity(),StartActivity.class);
                startActivity(i);               //delete current session and show start activity
                return true;
            case R.id.save_and_send_report:
                Uri uri=null;
                TestSession.get(getActivity()).saveTests();             //save session report
                uri= TestSession.get(getActivity()).saveXLSReport();    //save xls file report
                Intent intent =new Intent(Intent.ACTION_SEND);          //send by mail
                intent.setType("application/vnd.ms-excel");
                SimpleDateFormat dateFormat=new SimpleDateFormat("dd.MM.yyyy");
                intent.putExtra(Intent.EXTRA_STREAM,uri);
                intent.putExtra(Intent.EXTRA_SUBJECT,"Report "+ TestSession.mEquipmentType+dateFormat.format(TestSession.get(getActivity()).getStartTime()));
                intent=Intent.createChooser(intent,getString(R.string.send_report));
                startActivity(intent);
                return true;
           case R.id.home:
                if(NavUtils.getParentActivityIntent(getActivity())!=null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
           default: return super.onOptionsItemSelected(item);
        }
    }
    */

    @Override
    public void onResume() {
        super.onResume();
        if(!mTest.isAtWork()){
            lockView();
        }
    }

    @Override
    public void onPause() {
        TestSession.get(getActivity()).saveTests();
        super.onPause();
    }

    @Override
    public void onStop() {
        TestSession.get(getActivity()).saveTests();
        super.onStop();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks=null;
    }

    /**
     * Updates start time button view
     */
    private void updateStartTime(){
        DateFormat format=new SimpleDateFormat("HH:mm:ss");
        mStartTimeButton.setText(format.format(mTest.getStartTime().toDate()));
    }
    /**
     * Updates start date button view
     */
    private void updateStartDate(){
        DateFormat format=new SimpleDateFormat("dd.MM.yyyy");
        mStartDateButton.setText(format.format(mTest.getStartTime().toDate()));
    }

    /**
     * Updates end time button view
     */
    private void updateEndTime(){
        DateFormat format=new SimpleDateFormat("HH:mm:ss");
        mEndTimeButton.setText(format.format(mTest.getEndTime().toDate()));
    }

    /**
     * Updates end date button view
     */
    private void updateEndDate(){
        DateFormat format=new SimpleDateFormat("dd.MM.yyyy");
        mEndDateButton.setText(format.format(mTest.getEndTime().toDate()));
    }

    public interface Callbacks{
        void onTestUpdated(EcoTest test);
    }

    /**
     * locks all views except checkbox
     */
    private void lockView(){
        mO2field.setEnabled(true);
        mTemperatureField.setEnabled(true);
        mCOfieldPPM.setEnabled(true);
        mCOfieldMG.setEnabled(true);
        mNOxfieldPPM.setEnabled(true);
        mNOxfieldMG.setEnabled(true);
        mStartTimeButton.setEnabled(true);
        mStartDateButton.setEnabled(true);
        mEndTimeButton.setEnabled(true);
        mEndDateButton.setEnabled(true);
    }
}