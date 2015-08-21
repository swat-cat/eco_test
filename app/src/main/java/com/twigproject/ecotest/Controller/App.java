package com.twigproject.ecotest.Controller;

import android.app.Application;
import android.content.Context;

/**
 * Util class for easily getting for app context
 * @author Max Ermakov
 */
public class App extends Application {
    /**
     * Current app context
     */
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    /**
     * @return app context
     */
    public static Context getContext(){
        return mContext;
    }
}