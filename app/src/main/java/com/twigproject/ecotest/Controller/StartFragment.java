package com.twigproject.ecotest.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twigproject.ecotest.Model.TestSession;
import com.twigproject.ecotest.R;

/**
 * Start screen, session type chooser UI controller.
 * @author Max Ermakov
 */
public class StartFragment extends Fragment {
    private static final String TAG="com.twigproject.ecotest.Controller.Strat_fragment";

    private CustomSquareButton mNewBoilersSessionbutton;
    private CustomSquareButton mNewCalcKilnsSessionButton;
    private CustomSquareButton mNewLimeKilnsSessionButton;
    private CustomSquareButton mEditExistingReportButton;
    /*
    public static Fragment newInstance(String equipmentType, int equipmentCount){
        Bundle args=new Bundle();
        Fragment fragment=new StartFragment();
        fragment.setArguments(args);
        return fragment;
    }
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.app_name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.start_fragment,container,false);
        mNewBoilersSessionbutton=(CustomSquareButton)view.findViewById(R.id.boilers_session_button);
        mNewBoilersSessionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startListActivity(getString(R.string.boilers));    //start new boilers test session
            }
        });
        mNewCalcKilnsSessionButton=(CustomSquareButton)view.findViewById(R.id.calc_kilns_session_button);
        mNewCalcKilnsSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startListActivity(getString(R.string.calc_kilns)); //start new Calcinacium kilns test session
            }
        });
        mNewLimeKilnsSessionButton=(CustomSquareButton)view.findViewById(R.id.lime_kilns_session_button);
        mNewLimeKilnsSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startListActivity(getString(R.string.lime_kilns)); //start new lime kilns test session
            }
        });
        mEditExistingReportButton=(CustomSquareButton)view.findViewById(R.id.edit_report_button);
        mEditExistingReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TestSession.get(getActivity()).deleteFile();                    //destroys previous session singleton,if exists
                TestSession.delete();
                Intent i=new Intent(getActivity(),SessionListActivity.class);   //go to existing report choosing UI
                startActivity(i);
            }
        });
        return view;
    }

    /**
     * Starts new session editing activity with users choosed type
     * @param equipmentType users choosed new session equipment type
     */
    private  void startListActivity(String equipmentType){
        TestSession.get(getActivity()).deleteFile();                        //destroys previous session singleton,if exists
        TestSession.delete();
        Intent i=new Intent(getActivity(),EcoTestListActivity.class);
        i.putExtra(EcoTestListFragment.EXTRA_EQUIPMENT_TYPE,equipmentType); //starts new test session
        startActivity(i);
    }
}
