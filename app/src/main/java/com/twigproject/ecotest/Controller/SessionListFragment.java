package com.twigproject.ecotest.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.twigproject.ecotest.Model.TestSession;
import com.twigproject.ecotest.Model.TestSessionDBHelper;
import com.twigproject.ecotest.Model.Utils.SQLiteCursorLoader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Existing test session report chooser UI controller
 * @author Max Ermakov
 * @see android.support.v4.app.ListFragment
 * @see android.support.v4.content.Loader
 * @see android.support.v4.app.LoaderManager
 */
public class SessionListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "com.twigproject.ecotest.Controller.SessionListFragmen";
    /**
     * Should consists all before created test sessions data
     */
    private Cursor mCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        getLoaderManager().initLoader(0,null,this);  //initiated loader querying on background thread all created
                                                     //before sessions data from DB and updates with received data list view ui
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCursor.moveToPosition(position);              //directing cursor to choosed by user list item(session report)
        String filename = mCursor.getString(mCursor    //extracting filename of choosed report
                .getColumnIndex(TestSessionDBHelper.COLUMN_TEST_REPORT_FILENAME));
        if(filename==null){
            Log.e(TAG,"Error extracting filename from db");
        }
        SharedPreferences preferences = getActivity().getSharedPreferences(TestSession.PREFS_FILE,Context.MODE_PRIVATE);
        preferences.edit().putLong(TestSession.PREF_SESSION_ID,id).apply();
        startListActivity(filename);                   //starting activity for editing loaded test session
    }

    /**
     * Starts activity for editing loaded test session
     * @param filename users choosed session report filename for editing
     */
    private  void startListActivity(String filename){
        TestSession.get(getActivity()).deleteFile();                      //destroying session singleton if it exists
        TestSession.delete();
        Intent i=new Intent(getActivity(),EcoTestListActivity.class);
        i.putExtra(EcoTestListFragment.EXTRA_REPORT_FILE_NAME,filename);  //putting choosed report file name to args
        startActivity(i);                                                 //starting editing choosed session
    }

    /**
     * Simplify loading in background thread early created session reports data from SQLite
     * @see com.twigproject.ecotest.Model.Utils.SQLiteCursorLoader
     */
    private static class SessionListCursorLoader extends SQLiteCursorLoader {
        public SessionListCursorLoader(Context context) {
            super(context);
        }
        @Override
        protected Cursor loadCursor() {                          //executes in doInBackground() method of base class
            return TestSession.get(getContext()).sessionQuery();
        }
    }

    /**
     * Simplify filling UIs listView with extracted from db data
     * @see android.widget.CursorAdapter
     */
    private static class TestSessionCursorAdapter extends CursorAdapter{
        /**
         * Useful for initialisation Cursor field of outer class
         * @see com.twigproject.ecotest.Controller.SessionListFragment
         */
        private Cursor mSessionCursor;

        public TestSessionCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
            mSessionCursor=cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup group) {
            LayoutInflater inflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(android.R.layout.simple_list_item_1,group,false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView textView = (TextView)view;                       //setting items view with extracted from db data
            textView.setText("report "+mSessionCursor.getString(mSessionCursor.getColumnIndex(TestSessionDBHelper.COLUMN_TEST_EQUIPMENT_TYPE))
                             + " " + getDate());
        }

        /**
         * Creates session item date string
         * @return String with date value
         */
        private String getDate(){
            return mSessionCursor.getString(mSessionCursor.getColumnIndex(TestSessionDBHelper.COLUMN_TEST_SESSION_START_DATE));
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new SessionListCursorLoader(getActivity());                 //creating & starting loader
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {     //filling UI with received data
        TestSessionCursorAdapter adapter = new TestSessionCursorAdapter(getActivity(),cursor);
        setListAdapter(adapter);
        mCursor=cursor;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setListAdapter(null);
    }

}
