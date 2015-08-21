package com.twigproject.ecotest.Controller;

import android.support.v4.app.Fragment;

/**
 * Activity container for existing report chooser UI controller
 * @author Max Ermakov
 * @see com.twigproject.ecotest.Controller.SingleFragmentActivity
 * @see com.twigproject.ecotest.Controller.SessionListFragment
 */
public class SessionListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new SessionListFragment();
    }
}
