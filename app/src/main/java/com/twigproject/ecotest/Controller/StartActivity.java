package com.twigproject.ecotest.Controller;

import android.support.v4.app.Fragment;

import java.text.SimpleDateFormat;

/**
 * Activity container for StartFragment
 * @see com.twigproject.ecotest.Controller.SingleFragmentActivity
 * @see com.twigproject.ecotest.Controller.StartFragment
 */
public class StartActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new StartFragment();
    }
}
