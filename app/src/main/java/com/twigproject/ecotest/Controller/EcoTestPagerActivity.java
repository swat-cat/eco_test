package com.twigproject.ecotest.Controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.twigproject.ecotest.Model.EcoTest;
import com.twigproject.ecotest.Model.TestSession;
import com.twigproject.ecotest.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Activity container for single test modifying UI
 * Uses ViewPager tool to simplify navigation between single tests
 * @author Max Ermakov
 */
public class EcoTestPagerActivity extends FragmentActivity implements EcoTestFragment.Callbacks{
    private ViewPager mPager;
    private ArrayList<EcoTest> mTests;

    @Override
    public void onTestUpdated(EcoTest test) {
        //stub for correct class cast
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPager = new ViewPager(this);
        mPager.setId(R.id.viewPager);
        setContentView(mPager);

        mTests= TestSession.get(getApplicationContext()).getTests();            //getting current session test list
        FragmentManager fm =getSupportFragmentManager();                        //and setting pager adapter wit it
        mPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int i) {
                EcoTest test=mTests.get(i);                                     //setting current pager view with EcoTestFragment binded with every single test
                return EcoTestFragment.newInstance(test.getEquipmentType(),test.getEquipmentNumber());
            }

            @Override
            public int getCount() {
                return mTests.size();
            }
        });
                                                                               //to start new pager activity not from first
        int number=(int)getIntent()                                            //item in array (by default), but from choosed by user
                .getIntExtra(EcoTestFragment.EXTRA_EQUIPMENT_NUMBER, 1);       //extracting choosed equipm.number from bundle args
        for(int i=0;i<mTests.size();i++){                                      //and setting page with it
            if(mTests.get(i).getEquipmentNumber()==(number)){
                mPager.setCurrentItem(i);
                break;
            }
        }
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

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

            case R.id.home:
                if(NavUtils.getParentActivityIntent(this)!=null){
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}
