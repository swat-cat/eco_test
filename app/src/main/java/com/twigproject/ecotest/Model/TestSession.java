package com.twigproject.ecotest.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import com.google.common.io.Files;
import com.twigproject.ecotest.Controller.App;
import com.twigproject.ecotest.Model.Utils.EcoTestAnotherXLSUtils;
import com.twigproject.ecotest.Model.Utils.EcoTestXLSUtils;
import com.twigproject.ecotest.Model.Utils.ReportUtils;
import com.twigproject.ecotest.R;

import org.json.JSONException;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton for handling with current test-session
 * @author Max Ermaskov
 */
public class TestSession {
    /**
     * TAG label for LogCat
     */
    private static final String TAG="com.twigproject.ecotest.Model.TestSession";
    /**
     * Default equipment type - for test only
     */
    private static final String DEFAULT_EQUIPMENT_TYPE="boilers";
    /**
     * Internal storage file for saving test list of current session
     */
    public static final String CURRENT_SESSION_REPORT_FILENAME ="session.json";
    /**
     * SharedPreferences file for TestSession entity
     */
    public static final String PREFS_FILE ="sessions";
    public static final String PREF_SESSION_ID ="testSession.Id";
    /**
     * Connects localisation independent equipment type names with number of units of each type
     */
    public static final Map<String,Integer> equipment=new HashMap<String, Integer>() {
        {
            put((App.getContext().getString(R.string.boilers)),10);
            put((App.getContext().getString(R.string.calc_kilns)),5);
            put((App.getContext().getString(R.string.lime_kilns)),3);
        }

    };
    /**
     * Ref to static singleton entity
     */
    private static TestSession sTestSession;
    /**
     * Mutex to make singleton thread-safe
     */
    private final static Object mutex = new Object();
    /**
     * App context
     */
    private Context mContext;
    /**
     * SQLite handling helper
     * @see com.twigproject.ecotest.Model.TestSessionDBHelper
     */
    private TestSessionDBHelper mHelper;
    /**
     * SharedPreferences file of session
     */
    private SharedPreferences mPrefs;
    /**
     * Date of session
     */
    private Date mStartTime;
    /**
     * Testing equipment type
     */
    public static String mEquipmentType;
    /**
     * Number of testing equipment units
     */
    private int mEquipmentCount;
    /**
     * Name of session storage
     */
    private static String mFilename;
    /**
     * Ecology tests list of current test-session. Main collection of app
     * @see com.twigproject.ecotest.Model.EcoTest
     */
    private ArrayList<EcoTest> mTests;
    /**
     * JSON serializer
     * @see com.twigproject.ecotest.Model.EcoTestJSONSerializer
     */
    private EcoTestJSONSerializer mSerializer;
    private long mId;

    /**
     * Creates new session with default equipment type
     * @param context current app context
     */
    private TestSession(Context context){
        mContext=context;
        mHelper=new TestSessionDBHelper(mContext);
        mPrefs=mContext.getSharedPreferences(PREFS_FILE,Context.MODE_PRIVATE);
        mStartTime=new Date();
        mSerializer=new EcoTestJSONSerializer(mContext, CURRENT_SESSION_REPORT_FILENAME);
        try{
            mTests=mSerializer.loadTests();
        }
        catch (Exception ex){
            Log.e(TAG,"Error loading file" );
            mEquipmentType=DEFAULT_EQUIPMENT_TYPE;
            getEquipmentCount();
            mTests=new ArrayList<EcoTest>();
            for(int i=0;i<mEquipmentCount;i++){
                EcoTest test=new EcoTest(i+1,mEquipmentType);
                mTests.add(test);
            }
        }
    }

    /**
     * Loads earlier created session entity from report file
     * @param context current app context
     * @param report report file for earlier session
     */
    private TestSession(Context context, File report){
        mContext=context;
        mFilename=CURRENT_SESSION_REPORT_FILENAME;
        mHelper=new TestSessionDBHelper(mContext);
        mStartTime=new Date();
        mSerializer=new EcoTestJSONSerializer(mContext,mFilename);
        try{                                        //deserialiazeses from json file
            mTests=mSerializer.loadTests(report);
        }
        catch (IOException ex){
            Log.e(TAG,"Error loading file" );
            mTests=new ArrayList<EcoTest>();        //creates empty session
        }
        catch (JSONException ex){
            Log.e(TAG,"JSON error loading file" );
            mTests=new ArrayList<EcoTest>();        //creates empty session
        }
    }

    /**
     * Creates new session object with given equipment type
     * @param context current app context
     * @param equipmentType equipment type name
     */
    private TestSession(Context context, String equipmentType){
        mContext=context;
        mHelper=new TestSessionDBHelper(mContext);
        mStartTime=new Date();
        mEquipmentType=equipmentType;
        if (mEquipmentType!=null) {
            Log.d(TAG,"Singlet equipment type checked and applied. Type is:"+mEquipmentType);
        }
        else {
            Log.d(TAG,"Singlet equipment type checked DID NOT and applied");
        }                                                   //setting test-list size (equipment count)
                                                            // depending of equipment type
        setEquipmentCount();
        Log.d(TAG,"Equipment count is:"+mEquipmentCount);
        mSerializer=new EcoTestJSONSerializer(mContext, CURRENT_SESSION_REPORT_FILENAME);
        try{
            mTests=mSerializer.loadTests();
            Log.d(TAG,"current session file loaded");
        }
        catch (Exception ex){                               //if there is no stored data
                                                            //prepare new session
            Log.e(TAG,"Error loading file" );
            mTests=new ArrayList<EcoTest>();
            for(int i=0;i<mEquipmentCount;i++){
                EcoTest test=new EcoTest(i+1,mEquipmentType);
                mTests.add(test);
            }
        }
    }

    /**
     * singleton default get instance static method
     * @param context current app context
     * @return singleton instance
     */
    public static TestSession get(Context context){
        if(sTestSession ==null){
            synchronized (mutex) {
                if (sTestSession==null) {
                    sTestSession = new TestSession(context.getApplicationContext());
                }
            }
        }
        return sTestSession;
    }

    /**
     * singleton equipment type dependent get instance static method
     * @param context current app context
     * @param equipmentType equipment type name
     * @return singleton instance
     */
    public static TestSession get(Context context,String equipmentType){
        if(sTestSession ==null){
            synchronized (mutex) {
                if (sTestSession==null) {
                    sTestSession = new TestSession(context.getApplicationContext(),equipmentType);
                }
            }
        }
        return sTestSession;
    }

    /**
     * singleton file stored get instance static method
     * @param context current app context
     * @param report report file for earlier session
     * @return singleton instance
     */
    public static TestSession get(Context context,File report){
        if(sTestSession ==null){
            synchronized (mutex) {
                if (sTestSession==null) {
                    sTestSession = new TestSession(context.getApplicationContext(),report);
                }
            }
        }
        return sTestSession;
    }

    /**
     * public test-list getter
     * @return current test-list
     */
    public ArrayList<EcoTest> getTests(){ return mTests; }

    public EcoTest getTest(int number){
        for (EcoTest test:mTests){
            if(test.getEquipmentNumber()==number){
                return test;
            }
        }
        return null;
    }

    /**
     * Creates report name of current test-session
     * @return report file name
     */
    public String getReportFilename(){
        DateFormat format =new SimpleDateFormat("dd.MM.yyyy");
        return "report"+mEquipmentType+format.format(mStartTime)+".json";

    }

    /**
     * Serializes current test-list to json file
     * @return is serializing success boolean value
     */
    public boolean saveTests(){
        try {
            mSerializer.saveTests(mTests);
            Log.d(TAG,"File saved");
            return true;
        }
        catch (Exception e){
            Log.e(TAG,"Fail file saving");
            return false;
        }
    }

    /**
     * Creates and saves session report in spreed sheet file on external storage
     * @return URI of newly created XLS file
     * @see com.twigproject.ecotest.Model.Utils.EcoTestXLSUtils
     */
    public Uri saveXLSReport(){
        DateFormat format =new SimpleDateFormat("dd.MM.yyyy");
        try {
            saveReport();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!mEquipmentType.equals(App.getContext().getString(R.string.boilers))) {
            return new EcoTestXLSUtils(mContext,"report"+mEquipmentType+format.format(mStartTime)+".xls").saveXLSFile(mTests);
        } else {
            return new EcoTestAnotherXLSUtils(mContext,"report"+mEquipmentType+format.format(mStartTime)+".xls").sendXlsReport(mTests);
        }
    }
    /**
     * deletes current session storing file
     */
    public void deleteFile(){
        File file = new File(mContext.getFilesDir(),CURRENT_SESSION_REPORT_FILENAME);
        file.delete();
    }

    /**
     * destroys singleton entity
     */
    public static void delete(){
        sTestSession =null;
    }

    /**
     * Saves current sesion-list data to report file on external storage, adds its id to SQLite DB
     * @return Report file reference
     * @throws IOException
     * @see com.twigproject.ecotest.Model.TestSessionDBHelper
     */
    public File saveReport() throws IOException {
        File newFile = ReportUtils.getReportFile(getReportFilename());
        //File newFile = new File(mContext.getFilesDir(),getReportFilename());                //dest file
        File oldFile = new File(mContext.getFilesDir(), CURRENT_SESSION_REPORT_FILENAME);   //source file

        if (oldFile.exists()) {                                                             //write report
            Files.copy(oldFile,newFile);
        }
        newFile.createNewFile();
        setId( mHelper.insertSession() );                                                   //add session data to DB
        return oldFile;
    }

    /**
     * Querying all stored sessions data from DB
     * @return Cursor object on sessions data
     */
    public Cursor sessionQuery(){
        return mHelper.sessionQuery();
    }

    //--------------------Setters & Getters -------------------------------
    public String getEquipmentType() {
        return mEquipmentType;
    }

    public void setEquipmentCount(){
        mEquipmentCount=equipment.get(mEquipmentType);
    }

    public int getEquipmentCount() {
        return mEquipmentCount;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public Date getStartTime() {
        return mStartTime;
    }

    /*
    public void setStartTime(Date startTime) {
        mStartTime = startTime;
    }

    public static String getmEquipmentType() {
        return mEquipmentType;
    }

    public static void setFilename(String filename){
        mFilename=filename;
    }

    public boolean addTest(EcoTest test){
        if(test.getEquipmentNumber()>0||test.getEquipmentNumber()<mEquipmentCount){
            mTests.add(test.getEquipmentNumber(),test);
            return true;
        }
        else {
            return false;
        }
    }

    public void removeCurrentSession(){
        saveTests();
        try {
            File f=saveReport();
            f.delete();
            Log.d(TAG,"report saved");
        } catch (IOException e) {
            Log.e(TAG,"Error moving report file");
            e.printStackTrace();
        }
        mTests=null;
    }
    */
}
