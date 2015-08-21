package com.twigproject.ecotest.Controller;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.twigproject.ecotest.Model.EcoTest;
import com.twigproject.ecotest.Model.TestSession;
import com.twigproject.ecotest.R;

import java.text.SimpleDateFormat;

/**
 * Activity container for EcoTestListFragment
 * @author Max Ermakov
 * @see com.twigproject.ecotest.Controller.EcoTestListFragment
 * @see com.twigproject.ecotest.Controller.SingleFragmentActivity
 */
public class EcoTestListActivity extends SingleFragmentActivity implements EcoTestListFragment.CallBacks,EcoTestFragment.Callbacks {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    protected Fragment createFragment() {
        String type=getIntent().getStringExtra(EcoTestListFragment.EXTRA_EQUIPMENT_TYPE);
        String filename = getIntent().getStringExtra(EcoTestListFragment.EXTRA_REPORT_FILE_NAME);
        return EcoTestListFragment.newInstance(type,filename);
    }

    @Override
    public void onEquipmentUnitSelected(EcoTest ecoTest) {
        if (findViewById(R.id.detailFragmentContainer) == null) {
            Intent i =new Intent(this, EcoTestPagerActivity.class);
            i.putExtra(EcoTestFragment.EXTRA_EQUIPMENT_NUMBER,ecoTest.getEquipmentNumber());
            startActivity(i);
        }
        else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
            Fragment newDetail = EcoTestFragment.newInstance(ecoTest.getEquipmentType(),ecoTest.getEquipmentNumber());
            if(oldDetail!=null){
                ft.remove(oldDetail);
            }
            ft.add(R.id.detailFragmentContainer,newDetail);
            ft.commit();
        }
    }

    @Override
    public void onTestUpdated(EcoTest test) {
        FragmentManager fm = getSupportFragmentManager();
        EcoTestListFragment fragment = (EcoTestListFragment)fm.findFragmentById(R.id.fragmentContainer);
        fragment.updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.eco_test,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.remove_current_session:
                //EcoTestsLab.get(getActivity()).removeCurrentSession();
                Intent i=new Intent(this,StartActivity.class);
                startActivity(i);               //delete current session and show start activity
                return true;
            case R.id.save_and_send_report:
                Uri uri=null;
                TestSession.get(this).saveTests();             //save session report
                uri= TestSession.get(this).saveXLSReport();    //save xls file report
                Intent intent =new Intent(Intent.ACTION_SEND);          //send by mail
                intent.setType("application/vnd.ms-excel");
                SimpleDateFormat dateFormat=new SimpleDateFormat("dd.MM.yyyy");
                intent.putExtra(Intent.EXTRA_STREAM,uri);
                intent.putExtra(Intent.EXTRA_SUBJECT,"Report "+ TestSession.mEquipmentType+dateFormat.format(TestSession.get(this).getStartTime()));
                intent=Intent.createChooser(intent,getString(R.string.send_report));
                startActivity(intent);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}
