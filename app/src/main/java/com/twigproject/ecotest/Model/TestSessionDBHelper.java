package com.twigproject.ecotest.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * SQLiteOpenHelper subclass
 * Creates, updates, drops SQLite DB and tables
 * Helps easy inserting and querying data
 * @author Max Ermakov
 * @see android.database.sqlite.SQLiteOpenHelper
 */
public class TestSessionDBHelper extends SQLiteOpenHelper {
    /**
     * TAG label for LogCat
     */
    private static final String TAG = "com.twigproject.ecotest.Model.TestSessionDBHelper";
    private static final String DB_NAME = "EcoTest.sqlite";
    private static final int VERSION = 1;
    public static final String TABLE_TEST_SESSION="test_sessions";
    public static final String COLUMN_TEST_SESSION_ID="_id";
    public static final String COLUMN_TEST_SESSION_START_DATE = "start_date";
    public static final String COLUMN_TEST_EQUIPMENT_TYPE="equipment_type";
    public static final String COLUMN_TEST_EQUIPMENT_COUNT="equipment_count";
    public static final String COLUMN_TEST_REPORT_FILENAME ="report_reference";

    /*
    private static final String TABLE_TESTS="tests";
    private static final String COLUMN_TESTS_ID="tests_id";
    private static final String COLUMN_TESTS_EQUIPMENT_TYPE = "type";
    private static final String COLUMN_TESTS_EQUIPMENT_NUMBER="number";
    private static final String COLUMN_TESTS_START_TIME="start_time";
    private static final String COLUMN_TESTS_END_TIME="end_time";
    private static final String COLUMN_TESTS_O2="o2";
    private static final String COLUMN_TESTS_CO="co";
    private static final String COLUMN_TESTS_NOX="mox";
    private static final String COLUMN_TESTS_TEMPERATURE="temperature";
    private static final String COLUMN_TESTS_ATWORK="atwork";
    */
    /**
     * Current app context reference
     */
    private Context mContext;

    /**
     * Creates DBHelper entity with given DB file name
     * @param context app context
     */
    public TestSessionDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create table test_sessions (" +
                        "_id integer primary key autoincrement, start_date text," +
                        " equipment_type varchar(100), equipment_count real, report_reference text)");

        /*
        database.execSQL("create table tests(test_id references test_sessions(_id), equipment_type varchar(100),"+
                " equipment_number real, start_time integer, end_time integer, o2 double, co double,"+
                " nox double, temperature double, atwork boolean)");
        */
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2) {
        Log.w(TAG, "This action destroys Session database with all data");
        database.execSQL("drop table if it exists "+DB_NAME);
        onCreate(database);
    }

    /**
     * Easily storing current test-session data to local DB
     * @return record id
     * @see android.content.ContentValues
     */
    public long insertSession(){
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TEST_SESSION_START_DATE, format.format(TestSession.get(mContext)
                .getStartTime()));
        cv.put(COLUMN_TEST_EQUIPMENT_TYPE,TestSession.get(mContext).getEquipmentType());
        cv.put(COLUMN_TEST_EQUIPMENT_COUNT,TestSession.get(mContext).getEquipmentCount());
        cv.put(COLUMN_TEST_REPORT_FILENAME,TestSession.get(mContext).getReportFilename());
        return getWritableDatabase().insert(TABLE_TEST_SESSION,null,cv);
    }

    /**
     * Easily querying all sessions from DB
     * @return Cursor object directing on all sessions stored in DB in "asc" order
     * @see android.database.Cursor
     */
    public Cursor sessionQuery(){
        Cursor cursor = getReadableDatabase().query(TABLE_TEST_SESSION,
                null,
                null,
                null,
                null,
                null,
                COLUMN_TEST_SESSION_START_DATE+" asc");
        return cursor;
    }

    /*
    public static class TestSessionCursor extends CursorWrapper{
        public TestSessionCursor(Cursor cursor) {
            super(cursor);
        }

        public String getReport(){
            if (isBeforeFirst() || isAfterLast())
                return null;
            SharedPreferences preferences = App.getContext().getSharedPreferences(TestSession.PREFS_FILE,Context.MODE_PRIVATE);
            long sessionId=getLong(getColumnIndex(COLUMN_TEST_SESSION_ID));
            preferences.edit().putLong(TestSession.PREF_SESSION_ID,sessionId).apply();
            return getString(getColumnIndex(COLUMN_TEST_REPORT_FILENAME));
        }

        public String getText(){
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            return "report "+format.format(new Date(getInt(getColumnIndex(COLUMN_TEST_SESSION_START_DATE))))+" "+getString(getColumnIndex(COLUMN_TEST_EQUIPMENT_TYPE));
        }
    }
    */
}
