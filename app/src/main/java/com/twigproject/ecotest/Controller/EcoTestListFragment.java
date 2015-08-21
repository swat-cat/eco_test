package com.twigproject.ecotest.Controller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.twigproject.ecotest.Model.EcoTest;
import com.twigproject.ecotest.Model.TestSession;
import com.twigproject.ecotest.Model.Utils.ReportUtils;
import com.twigproject.ecotest.R;


import org.joda.time.DateTime;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Single test chooser UI controller
 * @author MaX Ermakov
 * @see android.support.v4.app.ListFragment
 */
public class EcoTestListFragment extends ListFragment {
    /**
     * Tag label for logcat
     */
    private static final String TAG = "com.twigproject.ecotest.Controller.EcoTestListFragment";
    /**
     * String key for equipment type extracting from bundle args
     */
    public static final String EXTRA_EQUIPMENT_TYPE ="com.twigproject.ecotest.Controller.equipmet_type" ;
    /**
     * String key for equipment number extracting from bundle args
     */
    public static final String EXTRA_REPORT_FILE_NAME ="com.twigproject.ecotest.Controller.ReporrtUri";
    /**
     * Current test-session list of tests
     */
    private ArrayList<EcoTest> mTests;

    private String mEquipmentType;
    private String mFileReportName;
    private CallBacks mCallBacks;
    private TestsAdapter mAdapter;
    /**
     * Creates new instance of EcoTestListFragment with given equipment type, or file name
     * @param equipmentType equipment type name
     * @param reportFilename stored session report filename
     * @return EcoTestListFragment obj.
     */
    public static EcoTestListFragment newInstance(String equipmentType,String reportFilename){
        Bundle args=new Bundle();
        args.putString(EXTRA_EQUIPMENT_TYPE,equipmentType);
        args.putString(EXTRA_REPORT_FILE_NAME,reportFilename);
        EcoTestListFragment fragment=new EcoTestListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.app_name);
                                                                                  //fragment bundle args could consist
                                                                                  //only one not null value
        mEquipmentType=getArguments().getString(EXTRA_EQUIPMENT_TYPE);            //String equipment type name
        mFileReportName =getArguments().getString(EXTRA_REPORT_FILE_NAME);        //OR sString report file name
        if(mEquipmentType!=null) {
            Log.d(TAG, "Type extra received");
            mTests = TestSession.get(getActivity(), mEquipmentType).getTests();    //so creates new session for equipment of given typw
        }
        else
        if(mFileReportName !=null){
            Log.d(TAG,"Report name received "+ mFileReportName);
            File report = new File(ReportUtils.getReportPath(),mFileReportName);
            //File report = new File(getActivity().getFilesDir(),mFileReportName);
            if(report.exists()){
               Log.d(TAG,"report file exists");
            }
            else {
                Log.e(TAG,"report file doesn't exists");
            }
            mTests=TestSession.get(getActivity(), report).getTests();              //then re-creates session from file
        }
        else{
            mTests=TestSession.get(getActivity()).getTests();
        }
        if (mTests.size()>0) {
            Log.d(TAG,"Loaded tests with equipment type");
        }
        mAdapter =new TestsAdapter(mTests);
        setListAdapter(mAdapter);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        EcoTest test =(EcoTest)(getListAdapter()).getItem(position);
        mCallBacks.onEquipmentUnitSelected(test);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ecotest_list_fragment,container,false);
        ListView listView = (ListView)view.findViewById(android.R.id.list);
        return view;
    }

    /**
     * updates adapter view
     */
    public void updateUI(){
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Adapter simplifies viewing current session test list
     * @see android.widget.ArrayAdapter
     */
    private class TestsAdapter extends ArrayAdapter<EcoTest> {
        private TestsAdapter(ArrayList<EcoTest> tests){
            super(getActivity(), R.layout.ecotest_list_fragment,tests);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=getActivity().getLayoutInflater().inflate(R.layout.fragment_item_list,null);
            }
            EcoTest test=getItem(position);                 //getting EcoTest instance binded with current list item
                                                            //and modifying every listview item according received data
            TextView equipmentLabel=(TextView)convertView.findViewById(R.id.equipmentType_list_item_label);
            equipmentLabel.setText(test.getEquipmentType()+"#"+test.getEquipmentNumber());
            TextView StartTimeTextView=(TextView)convertView.findViewById(R.id.date_of_test_item);
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH.mm");
            StartTimeTextView.setText(format.format(test.getStartTime().toDate()));
            CheckBox isAtWork=(CheckBox)convertView.findViewById(R.id.list_equipment_isAtWork);
            isAtWork.setChecked(test.isAtWork());
            return convertView;
        }
    }
    /*
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
                startActivity(i);
                return true;
            case R.id.save_and_send_report:
                TestSession.get(getActivity()).saveTests();                 //saves current session test report
                Uri uri= TestSession.get(getActivity()).saveXLSReport();    //creates & saves xls report
                Intent intent =new Intent(Intent.ACTION_SEND);              //sends report by mail using implicit intent
                intent.setType("application/json");
                SimpleDateFormat dateFormat=new SimpleDateFormat("dd.MM.yyyy");
                intent.putExtra(Intent.EXTRA_STREAM,uri);
                intent.putExtra(Intent.EXTRA_SUBJECT,"Report "+mEquipmentType+dateFormat.format((DateTime.now()).toDate()));
                intent=Intent.createChooser(intent,getString(R.string.send_report));
                startActivity(intent);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
    */
    @Override
    public void onPause() {
        super.onPause();
        //EcoTestsLab.get(getActivity()).saveTests();
    }

    public interface CallBacks{
        void onEquipmentUnitSelected(EcoTest ecoTest);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallBacks = (CallBacks)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBacks=null;
    }
}
